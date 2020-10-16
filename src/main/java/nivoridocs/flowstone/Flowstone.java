package nivoridocs.flowstone;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;

@Mod("flowstone")
public class Flowstone {

	public Flowstone() {
		MinecraftForge.EVENT_BUS.register(new FlowstoneEventHandler());
	}

}
