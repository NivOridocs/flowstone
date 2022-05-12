package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;

@Mixin(CountPlacementModifier.class)
public interface CountPlacementModifierAccessor {

    @Accessor("count")
    IntProvider getCount();

}
