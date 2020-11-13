package nivoridocs.flowstone;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationException;

import com.google.common.reflect.TypeToken;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import nivoridocs.flowstone.config.Configuration;
import nivoridocs.flowstone.config.FlowstoneConfiguration;
import nivoridocs.flowstone.config.custom.IdentifierSerializer;
import nivoridocs.flowstone.config.custom.OptionalIdentifierSerializer;

public class Flowstone implements ModInitializer {

	private static final Logger log = LogManager.getLogger();

	public static final String MOD_ID = "flowstone";
	public static final String MOD_NAME = "Flowstone";

	private static Flowstone instance;

	private FlowstoneConfiguration configuration;

	@SuppressWarnings("serial")
	@Override
	public void onInitialize() {
		log.info("[{}] Initializing", MOD_NAME);

		final Path path = FabricLoader.getInstance().getConfigDir().resolve(Flowstone.MOD_ID + "-configuration.json")
				.toAbsolutePath();
		final File file = path.toFile();
		final GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setPath(path).build();

		TypeSerializerCollection.defaults().register(new TypeToken<Identifier>() {
		}, new IdentifierSerializer());
		TypeSerializerCollection.defaults().register(new TypeToken<Optional<Identifier>>() {
		}, new OptionalIdentifierSerializer());

		Locale.setDefault(Locale.US);
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();

		this.configuration = new FlowstoneConfiguration(file::lastModified, loader, validator);

		try {
			this.configuration.init();
		} catch (IOException | ObjectMappingException ex) {
			log.error("[{}] {}", MOD_ID, ex.getMessage());
			throw new ConfigurationException(ex);
		} catch (ConfigurationException ex) {
			log.error("[{}] {}", MOD_ID, ex.getMessage());
			throw ex;
		}

		Flowstone.instance = this;
	}
	
	public static final Configuration getConfiguration() {
		return instance.configuration.getConfiguration();
	}

}
