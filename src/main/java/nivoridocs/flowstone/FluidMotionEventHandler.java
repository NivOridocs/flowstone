package nivoridocs.flowstone;

import java.util.Arrays;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.spectator.PlayerMenuObject;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.event.sound.PlaySoundSourceEvent;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.event.world.BlockEvent.CreateFluidSourceEvent;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidEvent.FluidMotionEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(Side.CLIENT)
public class FluidMotionEventHandler {
	
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
			if (hasWaterAround(world, pos))
				System.out.println("Handled!"); // TODO
		}
		
	}
	
	private static BlockPos createBlockPos(int x, int y, int z) {
		return new BlockPos(x < 0 ? x-1 : x, y, z < 0 ? z-1 : z);
	}
	
	private static boolean hasWaterAround(World world, BlockPos pos) {
		return isWater(world, pos.north()) || isWater(world, pos.south())
				|| isWater(world, pos.east()) || isWater(world, pos.west())
				|| isWater(world, pos.up()) || isWater(world, pos.down());
	}
	
	private static boolean isWater(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		return block == Blocks.WATER || block == Blocks.FLOWING_WATER;
	}

}
