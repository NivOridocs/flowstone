package nivoridocs.flowstone;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import nivoridocs.flowstone.event.LavaExtinguishEvent;

@EventBusSubscriber
public class FlowstoneEventHandler {
	
	private static final String LAVA_EXTINGUISH = "block.lava.extinguish";
	
	@SubscribeEvent
	public static void onEvent(PlaySoundSourceEvent event) {
		if (event.getName().equals(LAVA_EXTINGUISH)) {
			BlockPos pos = createBlockPos(
					(int) event.getSound().getXPosF(),
					(int) event.getSound().getYPosF(),
					(int) event.getSound().getZPosF());
			World world = DimensionManager.getWorld(0);
			IBlockState state = world.getBlockState(pos);
			if (isLavaExtinguishedBlock(state.getBlock()) && hasWaterAround(world, pos))
				MinecraftForge.EVENT_BUS.post(new LavaExtinguishEvent(world, pos, state));
		}
		
	}
	
	private static boolean isLavaExtinguishedBlock(Block block) {
		return block == Blocks.COBBLESTONE || block == Blocks.STONE || block == Blocks.OBSIDIAN;
	}
	
	private static BlockPos createBlockPos(int x, int y, int z) {
		return new BlockPos(x < 0 ? x-1 : x, y, z < 0 ? z-1 : z);
	}
	
	private static boolean hasWaterAround(World world, BlockPos pos) {
		return isWater(world, pos.up())
				|| isWater(world, pos.north()) || isWater(world, pos.south())
				|| isWater(world, pos.east()) || isWater(world, pos.west());
	}
	
	private static boolean isWater(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block == Blocks.WATER || block == Blocks.FLOWING_WATER;
	}

}
