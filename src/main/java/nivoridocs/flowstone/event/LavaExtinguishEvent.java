package nivoridocs.flowstone.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class LavaExtinguishEvent extends BlockEvent {
	
	public LavaExtinguishEvent(World world, BlockPos pos, IBlockState state) {
		super(world, pos, state);
	}
	
}
