package nivoridocs.flowstone;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import nivoridocs.flowstone.config.FlowstoneConfig;
import nivoridocs.flowstone.event.LavaExtinguishEvent;
import scala.actors.threadpool.Arrays;

@EventBusSubscriber
public class LavaExtinguishEventHandler {

	@SubscribeEvent
	public static void onLavaExtinguish(LavaExtinguishEvent event) {
		World world = event.getWorld();
		FlowstoneConfig.randomFor(event.getState().getBlock(), world.rand)
		.ifPresent(block -> world.setBlockState(event.getPos(), block.getDefaultState()));
	}

}
