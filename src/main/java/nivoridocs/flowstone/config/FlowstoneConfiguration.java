package nivoridocs.flowstone.config;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import nivoridocs.flowstone.Flowstone;
import nivoridocs.flowstone.config.Configuration.Item;
import nivoridocs.flowstone.config.ConfigurationImpl.ItemImpl;

public class FlowstoneConfiguration {

	private static FlowstoneConfiguration instance = null;

	public static final FlowstoneConfiguration getInstance() {
		if (instance == null)
			instance = new FlowstoneConfiguration();
		return instance;
	}

	private static final Logger log = LogManager.getLogger();

	private Gson gson;
	private File configurationFile;
	private long lastModified;

	private ConfigurationImpl configuration;

	private FlowstoneConfiguration() {
	}

	public void load(Gson gson, Path path) {
		this.gson = requireNonNull(gson, "gson");
		this.configurationFile = requireNonNull(path, "path").toFile();

		this.lastModified = configurationFile.lastModified();

		this.configuration = newConfiguration();

		if (configurationFile.exists()) {
			final ConfigurationImpl config = gson.fromJson(runtimeReader(configurationFile), ConfigurationImpl.class);
			if (isValid(config))
				this.configuration = config;
			else
				log.warn("Invalid file configuration; default configuration will be used insted");
		} else {
			this.configuration = newConfiguration();
			gson.toJson(configuration, ConfigurationImpl.class, runtimeWriter(configurationFile));
		}
	}

	private FileReader runtimeReader(File file) {
		try {
			return new FileReader(file);
		} catch (FileNotFoundException ex) {
			throw new JsonIOException(ex.getMessage(), ex);
		}
	}

	private FileWriter runtimeWriter(File file) {
		try {
			return new FileWriter(file);
		} catch (IOException ex) {
			throw new JsonIOException(ex.getMessage(), ex);
		}
	}

	public Configuration getConfiguration() {
		return new ConfigurationProxy(this::proxyInstance);
	}

	private Configuration proxyInstance() {
		long newLastModified = configurationFile.lastModified();
		if (this.lastModified != newLastModified) {
			this.lastModified = newLastModified;
			try {
				final ConfigurationImpl localConfiguration = gson.fromJson(new FileReader(configurationFile),
						ConfigurationImpl.class);
				if (isValid(localConfiguration)) {
					this.configuration.setVersion(localConfiguration.getVersion() + 1);
					this.configuration = localConfiguration;
				} else
					log.warn("Invalid file configuration; older configuration will be used insted");
			} catch (IOException ex) {
				log.warn("[{}] Unable to reload configurations: {}", Flowstone.MOD_ID, ex.getMessage(), ex);
			}
		}
		return this.configuration;
	}

	private ConfigurationImpl newConfiguration() {
		ConfigurationImpl localConfiguration = new ConfigurationImpl();
		localConfiguration
				.setItems(Registry.BLOCK.getIds().stream().filter(id -> id.getPath().endsWith("_ore")).map(ore -> {
					Identifier block = new Identifier(ore.getNamespace(),
							ore.getPath().replaceFirst("_ore$", "_block"));
					return new ItemImpl(ore, Optional.of(block).filter(Registry.BLOCK::containsId));
				}).collect(toList()));
		return localConfiguration;
	}

	private boolean isValid(Configuration value) {
		if (value.getMinChance() < .0d || value.getMinChance() > value.getMaxChance() || value.getMaxChance() > 1.0d)
			return false;

		if (value.getBlocksLimit() < 0 || value.getBlocksLimit() > 98)
			return false;

		for (Item item : value.getItems()) {
			if (!Registry.BLOCK.containsId(item.getOre())
					|| !item.getBlock().filter(Registry.BLOCK::containsId).isPresent())
				return false;
		}

		return true;
	}

}
