package niv.flowstone.util;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.world.biome.GenerationSettings;
import net.minecraft.world.biome.GenerationSettings.Builder;
import net.minecraft.world.gen.GenerationStep.Feature;
import net.minecraft.world.gen.feature.ConfiguredFeature;

public class FlowstoneBuilder extends GenerationSettings.Builder {

	public final static AtomicBoolean NET = new AtomicBoolean(false);

	private final BiConsumer<Feature, ConfiguredFeature<?, ?>> proxyConsumer;

	public FlowstoneBuilder(BiConsumer<Feature, ConfiguredFeature<?, ?>> proxyConsumer) {
		this.proxyConsumer = proxyConsumer;
	}

	@Override
	public Builder feature(Feature featureStep, ConfiguredFeature<?, ?> feature) {
		proxyConsumer.accept(featureStep, feature);
		return this;
	}

}
