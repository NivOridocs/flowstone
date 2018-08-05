package nivoridocs.flowstone.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.PostConfigChangedEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import nivoridocs.flowstone.Flowstone;

@EventBusSubscriber
public class ConfigEventHandler {
	
	@SubscribeEvent
	public static void onConfigChange(PostConfigChangedEvent event) {
		if (event.getModID().equals(Flowstone.MODID)) {
			ConfigManager.sync(Flowstone.MODID, Config.Type.INSTANCE);
			FlowstoneConfig.reset();
		}
	}

}
