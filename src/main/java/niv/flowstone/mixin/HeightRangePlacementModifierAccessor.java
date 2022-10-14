package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;

@Mixin(HeightRangePlacementModifier.class)
public interface HeightRangePlacementModifierAccessor {

    @Accessor("height")
    HeightProvider getHeight();

}
