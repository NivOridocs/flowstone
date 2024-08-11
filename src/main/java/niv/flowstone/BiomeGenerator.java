package niv.flowstone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableList;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents.Load;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration.TargetBlockState;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

public class BiomeGenerator {

    private static final Map<ResourceLocation, List<BiomeGenerator>> biomeCache = new HashMap<>();
    private static final Map<ResourceLocation, List<BiomeGenerator>> featureCache = new HashMap<>();

    private final Optional<PlacedFeature> feature;

    private final TargetBlockState target;

    private final ImmutableList<PlacementModifier> modifiers;

    private final Cache<BlockPos, List<BlockPos>> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(1, TimeUnit.SECONDS)
            .build();

    private BiomeGenerator(PlacedFeature feature, TargetBlockState target, List<PlacementModifier> modifier) {
        this.feature = Optional.of(feature);
        this.target = target;
        this.modifiers = ImmutableList.copyOf(modifier);
    }

    public Optional<BlockState> apply(LevelAccessor accessor, BlockPos pos) {
        var zero = accessor.getChunk(pos).getPos().getWorldPosition().atY(pos.getY());
        var result = cache.getIfPresent(zero);
        if (result == null) {
            if (accessor instanceof ServerLevel level) {
                var context = new PlacementContext(level, level.getChunkSource().getGenerator(), feature);
                var stream = Stream.of(zero);
                for (var modifier : this.modifiers) {
                    stream = stream.flatMap(x -> modifier.getPositions(context, level.getRandom(), x));
                }
                result = stream.toList();
                cache.put(zero, result);
            } else {
                result = List.of();
            }
        }
        if (result.stream().anyMatch(pos::equals)) {
            return Optional.of(target.state);
        } else {
            return Optional.empty();
        }
    }

    public static Stream<BiomeGenerator> all(LevelAccessor level, BlockPos pos) {
        var biome = level.getBiome(pos).value();
        var key = level.registryAccess().registryOrThrow(Registries.BIOME).getKey(biome);
        return biomeCache.computeIfAbsent(key, x -> toGenerators(level, biome)).stream();
    }

    private static List<BiomeGenerator> toGenerators(LevelAccessor level, Biome biome) {
        return biome.getGenerationSettings().features()
                .get(6).stream()
                .map(Holder::value)
                .map(feature -> fromFeature(level, feature))
                .flatMap(List::stream).toList();
    }

    private static List<BiomeGenerator> fromFeature(LevelAccessor level, PlacedFeature feature) {
        var key = level.registryAccess().registryOrThrow(Registries.PLACED_FEATURE).getKey(feature);
        return featureCache.computeIfAbsent(key, x -> toGenerators(feature));
    }

    private static List<BiomeGenerator> toGenerators(PlacedFeature feature) {
        var modifier = feature.placement();
        if (modifier.isEmpty()) {
            return List.of();
        }

        return feature.getFeatures()
                .map(ConfiguredFeature::config)
                .filter(OreConfiguration.class::isInstance)
                .map(OreConfiguration.class::cast)
                .flatMap(config -> config.targetStates.stream())
                .distinct()
                .map(target -> new BiomeGenerator(feature, target, modifier))
                .toList();
    }

    public static final class CacheInvalidator implements Load {
        @Override
        public void onWorldLoad(MinecraftServer server, ServerLevel level) {
            BiomeGenerator.biomeCache.clear();
            BiomeGenerator.featureCache.clear();
        }
    }
}
