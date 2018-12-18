package nivoridocs.flowstone;

import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nivoridocs.flowstone.config.FlowstoneConfig;
import nivoridocs.flowstone.event.LavaExtinguishEvent;

@EventBusSubscriber
public class LavaExtinguishEventHandler {

	@SubscribeEvent
	public static void onLavaExtinguish(LavaExtinguishEvent event) {
		World world = event.getWorld();
		FlowstoneConfig.randomFor(event.getState().getBlock(), world.rand)
		.ifPresent(block -> world.setBlockState(event.getPos(), block.getDefaultState()));
	}

}
