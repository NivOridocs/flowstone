package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.gen.UniformIntDistribution;

@Mixin(UniformIntDistribution.class)
public interface UniformIntDistributionHook {

    @Accessor("base")
    public int getBase();

}
