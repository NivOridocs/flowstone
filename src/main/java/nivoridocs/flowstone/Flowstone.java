package nivoridocs.flowstone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationException;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import nivoridocs.flowstone.config.FlowstoneConfiguration;

public class Flowstone implements ModInitializer {

	private static final Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_NAME);

		final Path path = FabricLoader.getInstance().getConfigDir().resolve(Flowstone.MOD_ID + "-configuration.json")
				.toAbsolutePath();
		final File file = path.toFile();
		final GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setPath(path).build();

		try {
			FlowstoneConfiguration.getInstance().load(file::lastModified, loader);
		} catch (IOException | ObjectMappingException ex) {
			log.error("[{}] {}", MOD_ID, ex.getMessage());
			throw new ConfigurationException(ex);
		} catch (ConfigurationException ex) {
			log.error("[{}] {}", MOD_ID, ex.getMessage());
			throw ex;
		}

	}

}
