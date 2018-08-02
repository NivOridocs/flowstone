package nivoridocs.flowstone;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import nivoridocs.flowstone.proxy.CommonProxy;

import org.apache.logging.log4j.Logger;

@Mod(
		modid = Flowstone.MODID,
		name = Flowstone.NAME,
		version = Flowstone.VERSION,
		useMetadata = true)
public class Flowstone {
    public static final String MODID = "flowstone";
    public static final String NAME = "Flowstone";
    public static final String VERSION = "1";
    
    @SidedProxy(
    		clientSide = "nivoridocs.flowstone.proxy.ClientProxy",
    		serverSide = "nivoridocs.flowstone.proxy.ServerProxy")
    public static CommonProxy proxy;
    
    @Mod.Instance
    public static Flowstone instance;

    public static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
    	proxy.postInit(event);
    }
}
