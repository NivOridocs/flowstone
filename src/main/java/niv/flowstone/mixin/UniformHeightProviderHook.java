package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;

@Mixin(UniformHeightProvider.class)
public interface UniformHeightProviderHook {

	@Accessor("minOffset")
	public YOffset getMinOffset();

	@Accessor("maxOffset")
	public YOffset getMaxOffset();

}
