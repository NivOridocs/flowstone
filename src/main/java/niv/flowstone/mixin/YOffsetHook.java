package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.gen.YOffset;

@Mixin(YOffset.class)
public interface YOffsetHook {
	
	@Accessor("offset")
	public int getRawOffset();

}
