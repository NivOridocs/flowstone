package niv.flowstone.mixin;

import java.util.ArrayList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import niv.flowstone.util.FlowstoneGenerator;
import niv.flowstone.util.FlowstoneGenerators;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

	@Redirect(method = "flow(" + "Lnet/minecraft/world/WorldAccess;"
			+ "Lnet/minecraft/util/math/BlockPos;" + "Lnet/minecraft/block/BlockState;"
			+ "Lnet/minecraft/util/math/Direction;" + "Lnet/minecraft/fluid/FluidState;" + ")V",
			at = @At(value = "INVOKE",
					target = "Lnet/minecraft/world/WorldAccess;" + "setBlockState("
							+ "Lnet/minecraft/util/math/BlockPos;"
							+ "Lnet/minecraft/block/BlockState;" + "I" + ")Z"))
	public boolean setBlockStateProxy(WorldAccess world, BlockPos pos, BlockState state,
			int flags) {

		if (state.getBlock().is(Blocks.STONE)) {
			var states = new ArrayList<BlockState>();
			var magmaCount = 0;

			for (int x = -2; x <= 2; x++)
				for (int y = -2; y <= 2; y++)
					for (int z = -2; z <= 2; z++)
						if (world.getBlockState(
								new BlockPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z))
								.isOf(Blocks.MAGMA_BLOCK))
							magmaCount++;

			for (FlowstoneGenerator gen : FlowstoneGenerators.allFor(world, pos))
				if (gen.isValidPos(world, pos))
					gen.generateOre(world, magmaCount).ifPresent(states::add);

			if (!states.isEmpty())
				state = states.get(world.getRandom().nextInt(states.size()));
		}
		return world.setBlockState(pos, state, flags);
	}

}
