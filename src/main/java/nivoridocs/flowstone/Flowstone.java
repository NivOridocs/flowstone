package nivoridocs.flowstone;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import nivoridocs.flowstone.config.FlowstoneConfig;

@Mod(Flowstone.MODID)
public class Flowstone {

	public static final String MODID = "flowstone";

	public Flowstone() {
		FlowstoneConfig.setup();

		MinecraftForge.EVENT_BUS.register(new FlowstoneEventHandler());
	}

}
