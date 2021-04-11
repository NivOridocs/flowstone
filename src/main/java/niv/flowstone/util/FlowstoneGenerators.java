package niv.flowstone.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import com.google.common.collect.Maps;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import niv.flowstone.mixin.UniformIntDistributionHook;

public class FlowstoneGenerators {

	private static final Map<Biome, List<FlowstoneGenerator>> cache = Maps.newHashMap();

	public static List<FlowstoneGenerator> allFor(WorldAccess world, BlockPos pos) {
		return cache.computeIfAbsent(world.getBiome(pos), FlowstoneGenerators::getBiomeGenerators);
	}

	private static final List<FlowstoneGenerator> getBiomeGenerators(Biome biome) {
		List<List<Supplier<ConfiguredFeature<?, ?>>>> features =
				biome.getGenerationSettings().getFeatures();
		int index = GenerationStep.Feature.UNDERGROUND_ORES.ordinal();
		List<FlowstoneGenerator> generators = new ArrayList<FlowstoneGenerator>();
		if (features.size() > index) {
			generators = features.get(index).stream().map(Supplier::get)
					.map(FlowstoneGenerators::createGenerator).filter(Optional::isPresent)
					.map(Optional::get).collect(Collectors.toList());
		}
		return generators;
	}

	private static final Optional<FlowstoneGenerator> createGenerator(
			ConfiguredFeature<?, ?> feature) {
		BlockState state = null;
		int minY = 0;
		int maxY = 0;
		int veins = 1;
		int repeats = 1;

		Object featureConfig = feature.config;

		while (featureConfig instanceof DecoratedFeatureConfig) {
			DecoratedFeatureConfig decoratedFeatureConfig = (DecoratedFeatureConfig) featureConfig;

			Object decoratorConfig = decoratedFeatureConfig.decorator.getConfig();
			if (decoratorConfig instanceof CountConfig) {
				repeats = Math.max(1, 1
						+ ((UniformIntDistributionHook) ((CountConfig) decoratorConfig).getCount())
								.getBase());
			} else if (decoratorConfig instanceof RangeDecoratorConfig) {
				RangeDecoratorConfig aux = (RangeDecoratorConfig) decoratorConfig;
				minY = Math.max(0, aux.topOffset);
				maxY = Math.max(minY, aux.maximum);
			} else if (decoratorConfig instanceof DepthAverageDecoratorConfig) {
				DepthAverageDecoratorConfig aux = (DepthAverageDecoratorConfig) decoratorConfig;
				minY = Math.max(0, aux.baseline - (aux.spread / 2));
				maxY = Math.max(minY, minY + aux.spread);
			}

			featureConfig = decoratedFeatureConfig.feature.get().config;
		}

		if (featureConfig instanceof OreFeatureConfig) {
			OreFeatureConfig aux = (OreFeatureConfig) featureConfig;
			state = aux.state;
			veins = Math.max(1, aux.size);
		}

		if (state != null && Registry.BLOCK.getId(state.getBlock()).getPath().endsWith("_ore"))
			return Optional.of(new SimpleFlowstonGenerator(state, minY, maxY, veins * repeats));
		else
			return Optional.empty();
	}

}
