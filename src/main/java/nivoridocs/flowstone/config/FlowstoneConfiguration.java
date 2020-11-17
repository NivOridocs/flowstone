package nivoridocs.flowstone.config;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.util.Optional;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationException;

import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import nivoridocs.flowstone.Flowstone;
import nivoridocs.flowstone.config.ConfigurationImpl.ItemImpl;

public class FlowstoneConfiguration {

	private static FlowstoneConfiguration instance = null;

	public static final FlowstoneConfiguration getInstance() {
		if (instance == null)
			instance = new FlowstoneConfiguration();
		return instance;
	}

	private static final Logger log = LogManager.getLogger();

	private LongSupplier lastModifiedSupplier;
	private long lastModified;

	private ConfigurationLoader<? extends ConfigurationNode> loader;

	private ConfigurationImpl configuration;

	private FlowstoneConfiguration() {
	}

	public void load(final LongSupplier lastModifiedSupplier,
			final ConfigurationLoader<? extends ConfigurationNode> loader)
			throws IOException, ObjectMappingException {
		this.lastModifiedSupplier = requireNonNull(lastModifiedSupplier, "lastModifiedSupplier");
		this.loader = requireNonNull(loader, "loader");

		this.lastModified = this.lastModifiedSupplier.getAsLong();
		final ConfigurationNode node = loader.load();
		this.configuration = ConfigurationImpl.MAPPER.bind(newConfiguration()).populate(node);

		validate(this.configuration);

		ConfigurationImpl.MAPPER.bind(this.configuration).serialize(node);
		loader.save(node);
	}

	public Configuration getConfiguration() {
		return new ConfigurationProxy(this::proxyInstance);
	}

	private Configuration proxyInstance() {
		if (this.lastModified != this.lastModifiedSupplier.getAsLong()) {
			this.lastModified = this.lastModifiedSupplier.getAsLong();
			try {
				final ConfigurationNode node = this.loader.load();
				final ConfigurationImpl localConfiguration = ConfigurationImpl.MAPPER.bind(newConfiguration())
						.populate(node);

				validate(localConfiguration);

				this.configuration.version = localConfiguration.version + 1;
				this.configuration = localConfiguration;
			} catch (ObjectMappingException | ConfigurationException | IOException ex) {
				log.warn("[{}] Unable to reload configurations: {}", Flowstone.MOD_ID, ex.getMessage(), ex);
			}
		}
		return this.configuration;
	}

	private ConfigurationImpl newConfiguration() {
		ConfigurationImpl localConfiguration = new ConfigurationImpl();
		localConfiguration.getItemsMutable().clear();
		localConfiguration.getItemsMutable()
				.addAll(Registry.BLOCK.getIds().stream().filter(id -> id.getPath().endsWith("_ore")).map(ore -> {
					Identifier block = new Identifier(ore.getNamespace(),
							ore.getPath().replaceFirst("_ore$", "_block"));
					if (Registry.BLOCK.containsId(block))
						return new ItemImpl(ore, Optional.of(block));
					return new ItemImpl(ore, Optional.empty());
				}).collect(Collectors.toSet()));
		return localConfiguration;
	}

	private void validate(ConfigurationImpl configuration) {
		
	}

}
