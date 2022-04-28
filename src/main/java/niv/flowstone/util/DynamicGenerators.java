package niv.flowstone.util;

import static net.minecraft.world.gen.GenerationStep.Feature.UNDERGROUND_ORES;
import static net.minecraft.world.gen.feature.OreConfiguredFeatures.STONE_ORE_REPLACEABLES;
import static niv.flowstone.FlowstoneMod.MOD_NAME;
import static niv.flowstone.FlowstoneMod.log;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.YOffset.AboveBottom;
import net.minecraft.world.gen.YOffset.BelowTop;
import net.minecraft.world.gen.YOffset.Fixed;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.heightprovider.TrapezoidHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import niv.flowstone.mixin.CountPlacementModifierAccessor;
import niv.flowstone.mixin.HeightRangePlacementModifierAccessor;
import niv.flowstone.mixin.TrapezoidHeightProviderAccessor;
import niv.flowstone.mixin.UniformHeightProviderAccessor;

public class DynamicGenerators {

    private static final int BOTTOM = -64;
    private static final int TOP = 256;

    private static final Map<Identifier, Set<Generator>> GENERATORS = new LinkedHashMap<>();

    private DynamicGenerators() {
    }

    public static Set<Generator> get(Identifier biome) {
        return GENERATORS.computeIfAbsent(biome, DynamicGenerators::load);
    }

    private static Set<Generator> load(Identifier biomeId) {
        var result = new LinkedHashSet<Generator>();

        var features = BuiltinRegistries.BIOME.get(biomeId).getGenerationSettings().getFeatures();
        if (features.size() <= UNDERGROUND_ORES.ordinal())
            return result;

        for (var item : features.get(UNDERGROUND_ORES.ordinal())) {
            var builder = Generator.builder();

            var placedFeature = item.getKey()
                    .map(RegistryKey::getValue)
                    .map(BuiltinRegistries.PLACED_FEATURE::get);
            if (placedFeature.isEmpty())
                return result;

            for (var placementModifier : placedFeature.get().placementModifiers()) {
                if (placementModifier instanceof CountPlacementModifier countPlacementModifier) {
                    builder.count(((CountPlacementModifierAccessor) countPlacementModifier).getCount().getMax());
                } else if (placementModifier instanceof HeightRangePlacementModifier heightRangePlacementModifier) {
                    var heightProvider = ((HeightRangePlacementModifierAccessor) heightRangePlacementModifier)
                            .getHeight();
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

            var configuredFeature = placedFeature.get().feature().getKey()
                    .map(RegistryKey::getValue)
                    .map(BuiltinRegistries.CONFIGURED_FEATURE::get);
            if (configuredFeature.isEmpty())
                return result;

            if (configuredFeature.get().config() instanceof OreFeatureConfig oreFeatureConfig) {
                builder.size(oreFeatureConfig.size);
                for (var target : oreFeatureConfig.targets)
                    if (STONE_ORE_REPLACEABLES.equals(target.target)) {
                        var generator = builder.state(target.state).build();
                        if (generator != null) {
                            log.info("[{}] Add {} generator to {} biome", () -> MOD_NAME,
                                    () -> Registry.BLOCK.getId(target.state.getBlock()),
                                    () -> biomeId);
                            result.add(generator);
                        }
                    }
            }
        }
        return result;
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
