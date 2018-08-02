package nivoridocs.flowstone.proxy;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		System.out.println("Server side!");
	}

}
