package niv.flowstone.impl;

import static java.util.stream.Collectors.toSet;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import niv.flowstone.Constants;
import niv.flowstone.api.Generator;

public class WorldlyGenerator implements Generator {

    private static final Table<Block, Biome, Set<Generator>> biomeCache = HashBasedTable.create();
    private static final Table<Block, PlacedFeature, Set<Generator>> featureCache = HashBasedTable.create();

    private final Cache<BlockPos, Set<BlockPos>> cache;

    private final Optional<PlacedFeature> feature;

    private final BlockState state;

    private final List<PlacementModifier> modifiers;

    private WorldlyGenerator(PlacedFeature feature, BlockState state, List<PlacementModifier> modifiers) {
        this.feature = Optional.of(feature);
        this.state = state;
        this.modifiers = modifiers;

        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public Optional<BlockState> apply(LevelAccessor level, BlockPos pos) {
        if (applyModifiers(level, pos).anyMatch(pos::equals)) {
            return Optional.of(state);
        } else {
            return Optional.empty();
        }
    }

    private Stream<BlockPos> applyModifiers(LevelAccessor level, BlockPos pos) {
        var origin = level.getChunk(pos).getPos().getWorldPosition();
        try {
            return cache.get(origin, () -> loadModifiers(level, origin)).stream();
        } catch (ExecutionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Set<BlockPos> loadModifiers(LevelAccessor accessor, BlockPos origin) {
        if (accessor instanceof ServerLevel level) {
            var context = new PlacementContext(level, level.getChunkSource().getGenerator(), feature);
            var stream = IntStream.range(0, Constants.AMPLIFICATION).mapToObj(i -> origin);
            for (var modifier : this.modifiers) {
                stream = stream.flatMap(pos -> modifier.getPositions(context, level.getRandom(), pos));
            }
            return stream.distinct().collect(toSet());
        } else {
            return Set.of();
        }
    }

    public static final Set<Generator> getGenerators(LevelAccessor level, BlockPos pos, BlockState state) {
        var biome = level.getBiome(pos).value();
        var result = biomeCache.get(state.getBlock(), biome);
        if (result == null) {
            result = biome.getGenerationSettings().features().stream()
                    .flatMap(HolderSet::stream)
                    .map(Holder::value)
                    .map(feature -> getGenerators(feature, state))
                    .flatMap(Set::stream)
                    .collect(toSet());
            biomeCache.put(state.getBlock(), biome, result);
        }
        return result;
    }

    private static final Set<Generator> getGenerators(PlacedFeature feature, BlockState state) {
        var result = featureCache.get(state.getBlock(), feature);
        if (result == null) {
            result = feature.getFeatures()
                    .map(ConfiguredFeature::config)
                    .filter(OreConfiguration.class::isInstance)
                    .findFirst().stream()
                    .map(OreConfiguration.class::cast)
                    .flatMap(config -> config.targetStates.stream())
                    .filter(target -> target.target.test(state, RandomSource.create(0)))
                    .map(target -> target.state).distinct()
                    .filter(s -> s.is(ConventionalBlockTags.ORES))
                    .flatMap(s -> feature.placement().isEmpty() ? Stream.empty()
                            : Stream.of(new WorldlyGenerator(feature, s, feature.placement())))
                    .collect(toSet());
            featureCache.put(state.getBlock(), feature, result);
        }
        return result;
    }

    public static final class CacheInvalidator implements Load {
        @Override
        public void onWorldLoad(MinecraftServer server, ServerLevel level) {
            WorldlyGenerator.biomeCache.clear();
            WorldlyGenerator.featureCache.clear();
        }
    }
}
