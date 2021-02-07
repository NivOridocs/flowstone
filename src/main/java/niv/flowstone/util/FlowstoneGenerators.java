package niv.flowstone.util;

import java.util.LinkedList;
import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.DefaultBiomeFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import niv.flowstone.mixin.UniformIntDistributionHook;

public class FlowstoneGenerators {

    private static final List<FlowstoneGenerator> generators = new LinkedList<>();

    public static List<FlowstoneGenerator> all() {
        if (generators.isEmpty()) {
            addDefaultOres();
        }
        return ImmutableList.copyOf(generators);
    }

    private static void addDefaultOres() {
        DefaultBiomeFeatures.addDefaultOres(FlowstoneBuilder.proxy((x, feature) -> {
            BlockState state = null;
            var minY = 0;
            var maxY = 0;
            var veins = 1;
            var repeats = 1;

            var featureConfig = feature.config;

            while (featureConfig instanceof DecoratedFeatureConfig) {
                var decoratedFeatureConfig = (DecoratedFeatureConfig) featureConfig;

                var decoratorConfig = decoratedFeatureConfig.decorator.getConfig();
                if (decoratorConfig instanceof CountConfig) {
                    repeats = Math.max(1,
                            1 + ((UniformIntDistributionHook) ((CountConfig) decoratorConfig)
                                    .getCount()).getBase());
                } else if (decoratorConfig instanceof RangeDecoratorConfig) {
                    var aux = (RangeDecoratorConfig) decoratorConfig;
                    minY = Math.max(0, aux.topOffset);
                    maxY = Math.max(minY, aux.maximum);
                } else if (decoratorConfig instanceof DepthAverageDecoratorConfig) {
                    var aux = (DepthAverageDecoratorConfig) decoratorConfig;
                    minY = Math.max(0, aux.baseline - (aux.spread / 2));
                    maxY = Math.max(minY, minY + aux.spread);
                }

                featureConfig = decoratedFeatureConfig.feature.get().config;
            }

            if (featureConfig instanceof OreFeatureConfig) {
                var aux = (OreFeatureConfig) featureConfig;
                state = aux.state;
                veins = Math.max(1, aux.size);
            }

            if (state != null)
                generators.add(new SimpleFlowstonGenerator(state, minY, maxY, veins * repeats));
        }));
    }

}
