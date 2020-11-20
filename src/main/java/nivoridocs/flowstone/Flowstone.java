package nivoridocs.flowstone;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ModInitializer;
import nivoridocs.flowstone.config.ConfigurationImpl;

public class Flowstone implements ModInitializer {

	private static final Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_NAME);

		AutoConfig.register(ConfigurationImpl.class, Toml4jConfigSerializer::new);

	}

}
