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
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import niv.flowstone.mixin.TrapezoidHeightProviderHook;
import niv.flowstone.mixin.UniformHeightProviderHook;
import niv.flowstone.mixin.YOffsetHook;

public final class FlowstoneGenerators {

	private static final Map<Biome, FlowstoneGenerators> cache = Maps.newHashMap();

	private final WorldAccess world;
	private final BlockPos pos;
	private List<FlowstoneGenerator> generators;

	private FlowstoneGenerators(WorldAccess world, BlockPos pos) {
		this.world = world;
		this.pos = pos;
	}

	public List<FlowstoneGenerator> get() {
		if (this.generators == null) {
			List<List<Supplier<ConfiguredFeature<?, ?>>>> features =
					world.getBiome(pos).getGenerationSettings().getFeatures();
			int index = GenerationStep.Feature.UNDERGROUND_ORES.ordinal();
			List<FlowstoneGenerator> generators = new ArrayList<FlowstoneGenerator>();
			if (features.size() > index) {
				generators = features.get(index).stream().map(this::createGenerator)
						.flatMap(Optional::stream).collect(Collectors.toList());
			}
			this.generators = generators;
		}
		return this.generators;
	}

	private Optional<FlowstoneGenerator> createGenerator(
			Supplier<ConfiguredFeature<?, ?>> supplier) {

		// From heightProvider
		int minY = world.getBottomY();
		int maxY = world.getTopY();
		int plateau = maxY - minY;

		// From countProvider
		int count = 1;

		// From oreFeatureConfig
		Optional<BlockState> blockState = Optional.empty();
		int size = 1;

		FeatureConfig featureConfig = supplier.get().config;

		while (featureConfig instanceof DecoratedFeatureConfig) {
			DecoratedFeatureConfig decoratedFeatureConfig = (DecoratedFeatureConfig) featureConfig;

			DecoratorConfig decoratorConfig = decoratedFeatureConfig.decorator.getConfig();

			if (decoratorConfig instanceof RangeDecoratorConfig) {
				HeightProvider heightProvider =
						((RangeDecoratorConfig) decoratorConfig).heightProvider;
				if (heightProvider instanceof UniformHeightProvider) {
					UniformHeightProviderHook providerHook =
							(UniformHeightProviderHook) heightProvider;
					minY = getY(providerHook.getMinOffset());
					maxY = getY(providerHook.getMaxOffset());
					plateau = maxY - minY;
				} else if (heightProvider instanceof TrapezoidHeightProvider) {
					TrapezoidHeightProviderHook providerHook =
							(TrapezoidHeightProviderHook) heightProvider;
					minY = getY(providerHook.getMinOffset());
					maxY = getY(providerHook.getMaxOffset());
					plateau = providerHook.getPlateau();
				}
			}

			else if (decoratorConfig instanceof CountConfig) {
				IntProvider countProvider = ((CountConfig) decoratorConfig).getCount();
				if (countProvider instanceof ConstantIntProvider)
					count = countProvider.getMin();
			}

			featureConfig = decoratedFeatureConfig.feature.get().config;
		}

		if (featureConfig instanceof OreFeatureConfig) {
			OreFeatureConfig oreFeatureConfig = (OreFeatureConfig) featureConfig;
			size = Math.max(1, oreFeatureConfig.size);
			blockState = oreFeatureConfig.targets.stream()
					.map(target -> target.state)
					.filter(state -> Registry.BLOCK
							.getId(state.getBlock()).getPath().endsWith("_ore"))
					.findAny();
		}

		if (blockState.isPresent() && minY <= maxY) {
			return Optional.of(new SimpleFlowstonGenerator(blockState.get(), minY, maxY, plateau,
					count * size));
		} else
			return Optional.empty();
	}

	private int getY(YOffset yOffset) {
		int bottomY = world.getBottomY();
		int topY = world.getTopY();
		int offset = ((YOffsetHook) yOffset).getRawOffset();

		if (YOffset.getBottom().getClass().isInstance(yOffset)) {
			offset = bottomY + offset;
		} else if (YOffset.getTop().getClass().isInstance(yOffset)) {
			offset = topY - 1 + bottomY - offset;
		}

		return offset;
	}

	public static List<FlowstoneGenerator> allFor(WorldAccess world, BlockPos pos) {
		return cache
				.computeIfAbsent(world.getBiome(pos), key -> new FlowstoneGenerators(world, pos))
				.get();
	}

}
