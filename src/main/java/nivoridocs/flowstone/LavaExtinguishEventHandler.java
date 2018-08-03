package nivoridocs.flowstone;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nivoridocs.flowstone.config.FlowstoneConfig;
import nivoridocs.flowstone.event.LavaExtinguishEvent;
import scala.actors.threadpool.Arrays;

@EventBusSubscriber
public class LavaExtinguishEventHandler {

	@SubscribeEvent
	public static void onLavaExtinguish(LavaExtinguishEvent event) {
		Block block = event.getState().getBlock();
		World world = event.getWorld();
		if (block == Blocks.COBBLESTONE)
			world.setBlockState(event.getPos(),
					FlowstoneConfig.randomForCobblestone(world.rand).getDefaultState());
		else if (block == Blocks.STONE)
			world.setBlockState(event.getPos(),
					FlowstoneConfig.randomForStone(world.rand).getDefaultState());
		else if (block == Blocks.OBSIDIAN)
			world.setBlockState(event.getPos(),
					FlowstoneConfig.randomForObsidian(world.rand).getDefaultState());
	}

}
