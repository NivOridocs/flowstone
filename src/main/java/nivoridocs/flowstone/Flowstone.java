package nivoridocs.flowstone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.fabricmc.api.ModInitializer;

public class Flowstone implements ModInitializer {

	public static Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	@Override
	public void onInitialize() {
		log.info("[{}] {}", MOD_NAME, "Initializing");
	}

}
