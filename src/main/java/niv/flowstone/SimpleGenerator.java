package niv.flowstone;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class SimpleGenerator implements BiFunction<LevelAccessor, BlockPos, Optional<BlockState>> {

    private static final Map<ResourceLocation, Set<SimpleGenerator>> featureStoneCache = new HashMap<>(256);
    private static final Map<ResourceLocation, Set<SimpleGenerator>> biomeStoneCache = new HashMap<>(64);

    private static final Map<ResourceLocation, Set<SimpleGenerator>> featureNetherrackCache = new HashMap<>(256);
    private static final Map<ResourceLocation, Set<SimpleGenerator>> biomeNetherrackCache = new HashMap<>(64);

    private final Cache<BlockPos, Set<BlockPos>> cache;

    private final Optional<PlacedFeature> feature;

    private final BlockState state;

    private final List<PlacementModifier> modifiers;

    private SimpleGenerator(PlacedFeature feature, BlockState state, List<PlacementModifier> modifiers) {
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
            var stream = IntStream.range(0, 16).mapToObj(i -> origin);
            for (var modifier : this.modifiers) {
                stream = stream.flatMap(pos -> modifier.getPositions(context, level.getRandom(), pos));
            }
            return stream.distinct().collect(toSet());
        } else {
            return Set.of();
        }
    }

    public static final Set<SimpleGenerator> getSimpleStoneGenerators(LevelAccessor level, BlockPos pos) {
        var biome = level.getBiome(pos).value();
        var biomeKey = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        return biomeStoneCache.computeIfAbsent(biomeKey,
                key -> biome.getGenerationSettings().features()
                        .get(6).stream()
                        .map(Holder::value)
                        .map(feature -> getSimpleStoneGenerators(level, feature))
                        .flatMap(Set::stream)
                        .collect(toSet()));
    }

    private static final Set<SimpleGenerator> getSimpleStoneGenerators(LevelAccessor level, PlacedFeature feature) {
        var featureKey = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getKey(feature);
        return featureStoneCache.computeIfAbsent(featureKey,
                key -> feature.getFeatures()
                        .map(ConfiguredFeature::config)
                        .filter(OreConfiguration.class::isInstance)
                        .findFirst().stream()
                        .map(OreConfiguration.class::cast)
                        .flatMap(config -> config.targetStates.stream())
                        .filter(target -> target.target.test(Blocks.STONE.defaultBlockState(), RandomSource.create(0)))
                        .map(target -> target.state).distinct()
                        .flatMap(state -> feature.placement().isEmpty() ? Stream.empty()
                                : Stream.of(new SimpleGenerator(feature, state, feature.placement())))
                        .collect(toSet()));
    }

    public static final class CacheInvalidator implements Load {
        @Override
        public void onWorldLoad(MinecraftServer server, ServerLevel level) {
            SimpleGenerator.featureStoneCache.clear();
            SimpleGenerator.biomeStoneCache.clear();
            SimpleGenerator.featureNetherrackCache.clear();
            SimpleGenerator.biomeNetherrackCache.clear();
        }
    }
}
