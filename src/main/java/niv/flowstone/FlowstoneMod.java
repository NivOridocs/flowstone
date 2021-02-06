package niv.flowstone;

import net.fabricmc.api.ModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FlowstoneMod implements ModInitializer {

	public static Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_ID);
	}

}
