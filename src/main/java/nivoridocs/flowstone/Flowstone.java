package nivoridocs.flowstone;

import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import nivoridocs.flowstone.config.FlowstoneConfiguration;

public class Flowstone implements ModInitializer {

	private static final Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_NAME);

		final Path path = FabricLoader.getInstance().getConfigDir().resolve(Flowstone.MOD_ID + "-config.json")
				.toAbsolutePath();
		Gson gson = new GsonBuilder().registerTypeAdapter(Identifier.class, new IdentifierAdapter()).setPrettyPrinting()
				.create();

		FlowstoneConfiguration.getInstance().load(gson, path);
	}

}
