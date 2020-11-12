package nivoridocs.flowstone;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.ValueDeserializationException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class Flowstone implements ModInitializer {

	private static final Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	private static Flowstone instance;

	public static Flowstone getInstance() {
		if (instance == null)
			throw new IllegalStateException("Flowstone is not initialized yet");
		return instance;
	}

	private FlowstoneConfig config = new FlowstoneConfig();

	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_NAME);

		try {
			loadConfiguration();
		} catch (IOException ex) {
			throw new IllegalStateException(ex);
		}

		Flowstone.instance = this;
	}

	public void loadConfiguration() throws IOException {
		AnnotatedSettings settings = AnnotatedSettings.builder().build();

		JanksonValueSerializer jk = new JanksonValueSerializer(false);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			File configFile = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID + ".conf").toAbsolutePath()
					.toFile();

			ConfigTree tree = ConfigTree.builder().applyFromPojo(config, settings).build();

			if (configFile.exists()) {
				fis = new FileInputStream(configFile);
				FiberSerialization.deserialize(tree, fis, jk);
			} else {
				fos = new FileOutputStream(configFile);
				FiberSerialization.serialize(tree, fos, jk);
			}

		} catch (ValueDeserializationException ex) {
			log.error("[{}] Configuration error", MOD_ID, ex);
			throw new IllegalStateException(ex);
		} finally {
			if (fis != null)
				fis.close();
			if (fos != null)
				fos.close();
		}
	}

	public FlowstoneConfig getConfig() {
		return config;
	}

}
