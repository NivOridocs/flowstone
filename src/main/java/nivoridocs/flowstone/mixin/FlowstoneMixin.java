package nivoridocs.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

@Mixin(LavaFluid.class)
public class FlowstoneMixin {

	@Redirect(method = "flow(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	public boolean setBlockStateProxy(IWorld world, BlockPos pos, BlockState state, int flags) {
		return world.setBlockState(pos, Blocks.GLASS.getDefaultState(), 3);
	}

}
