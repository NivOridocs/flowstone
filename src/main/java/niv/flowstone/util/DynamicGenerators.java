package niv.flowstone.util;

import static net.minecraft.world.gen.GenerationStep.Feature.UNDERGROUND_ORES;
import static net.minecraft.world.gen.feature.OreConfiguredFeatures.STONE_ORE_REPLACEABLES;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.YOffset.AboveBottom;
import net.minecraft.world.gen.YOffset.BelowTop;
import net.minecraft.world.gen.YOffset.Fixed;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;
import niv.flowstone.mixin.CountPlacementModifierAccessor;
import niv.flowstone.mixin.HeightRangePlacementModifierAccessor;
import niv.flowstone.mixin.TrapezoidHeightProviderAccessor;
import niv.flowstone.mixin.UniformHeightProviderAccessor;

public class DynamicGenerators {

    private static final int BOTTOM = -64;
    private static final int TOP = 256;

    private static final Map<Biome, Set<Generator>> GENERATORS = new LinkedHashMap<>();

    private DynamicGenerators() {
    }

    public static Set<Generator> get(Biome biome) {
        return GENERATORS.computeIfAbsent(biome, DynamicGenerators::load);
    }

    private static Set<Generator> load(Biome biome) {
        var result = new LinkedHashSet<Generator>();

        var features = biome.getGenerationSettings().getFeatures();
        if (features.size() <= UNDERGROUND_ORES.ordinal())
            return result;

        for (var item : features.get(UNDERGROUND_ORES.ordinal())) {

            var placedFeature = item.getKeyOrValue().right();
            if (placedFeature.isEmpty())
                return result;

            var builder = processPlacementModifiers(
                    Generator.builder(),
                    placedFeature.get().placementModifiers());

            var configuredFeature = placedFeature.get().feature().getKeyOrValue().right();
            if (configuredFeature.isEmpty())
                return result;

            if (configuredFeature.get().config() instanceof OreFeatureConfig oreFeatureConfig) {
                builder.size(oreFeatureConfig.size);
                for (var target : oreFeatureConfig.targets)
                    if (STONE_ORE_REPLACEABLES.equals(target.target))
                        result.add(builder.state(target.state).build());
            }
        }

        return result;
    }

    private static Generator.Builder processPlacementModifiers(
            Generator.Builder builder,
            List<PlacementModifier> placementModifiers) {
        for (var placementModifier : placementModifiers) {
            if (placementModifier instanceof CountPlacementModifier countPlacementModifier) {
                builder.count(((CountPlacementModifierAccessor) countPlacementModifier).getCount().getMax());
            } else if (placementModifier instanceof HeightRangePlacementModifier heightRangePlacementModifier) {
                var heightProvider = ((HeightRangePlacementModifierAccessor) heightRangePlacementModifier).getHeight();
                if (heightProvider instanceof UniformHeightProvider uniformHeightProvider) {
                    var uniform = ((UniformHeightProviderAccessor) uniformHeightProvider);
                    builder
                            .minY(getY(uniform.getMinOffset()))
                            .maxY(getY(uniform.getMaxOffset()));
                } else if (heightProvider instanceof TrapezoidHeightProvider trapezoidHeightProvider) {
                    var trapezoid = ((TrapezoidHeightProviderAccessor) trapezoidHeightProvider);
                    builder
                            .minY(getY(trapezoid.getMinOffset()))
                            .maxY(getY(trapezoid.getMaxOffset()))
                            .plateau(trapezoid.getPlateau());
                }
            }
        }
        return builder;
    }

    private static int getY(YOffset yOffset) {
        if (yOffset instanceof Fixed fixed) {
            return fixed.y();
        } else if (yOffset instanceof AboveBottom aboveBottom) {
            return BOTTOM + aboveBottom.offset();
        } else if (yOffset instanceof BelowTop belowTop) {
            return TOP - belowTop.offset();
        } else {
            throw new IllegalArgumentException("Unknown YOffset implementation");
        }
    }

}
