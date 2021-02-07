package niv.flowstone.util;

import java.util.function.BiConsumer;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.GenerationSettings.Builder;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FlowstoneBuilder extends GenerationSettings.Builder {

	private static final FlowstoneBuilder INSTANCE = new FlowstoneBuilder();

	private final ThreadLocal<BiConsumer<Feature, ConfiguredFeature<?, ?>>> localProxyConsumer;

	private FlowstoneBuilder() {
		localProxyConsumer = ThreadLocal.withInitial(() -> (x, feature) -> {
		});
	}

	@Override
	public Builder feature(Feature featureStep, ConfiguredFeature<?, ?> feature) {
		localProxyConsumer.get().accept(featureStep, feature);
		return this;
	}

	public static final GenerationSettings.Builder proxy(
			BiConsumer<Feature, ConfiguredFeature<?, ?>> proxyConsumer) {
		INSTANCE.localProxyConsumer.set(proxyConsumer);
		return INSTANCE;
	}

}
