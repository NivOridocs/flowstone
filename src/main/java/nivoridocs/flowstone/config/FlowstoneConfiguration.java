package nivoridocs.flowstone.config;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.LongSupplier;
import java.util.stream.Collectors;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationException;

import com.google.common.collect.Sets;

import lombok.NonNull;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import nivoridocs.flowstone.Flowstone;
import nivoridocs.flowstone.config.ConfigurationImpl.ItemImpl;

public class FlowstoneConfiguration {
	
	private static final Logger log = LogManager.getLogger();

	private final LongSupplier lastModifiedSupplier;
	private long lastModified;

	private final ConfigurationLoader<? extends ConfigurationNode> loader;

	private ConfigurationImpl instance;

	private final Validator validator;

	public FlowstoneConfiguration(final @NonNull LongSupplier lastModifiedSupplier,
			final @NonNull ConfigurationLoader<? extends ConfigurationNode> loader,
			final @NonNull Validator validator) {
		this.lastModifiedSupplier = lastModifiedSupplier;
		this.loader = loader;
		this.validator = validator;

		this.lastModified = this.lastModifiedSupplier.getAsLong();
	}

	public void init() throws IOException, ObjectMappingException {
		final ConfigurationNode node = loader.load();
		instance = ConfigurationImpl.MAPPER.bind(newConfiguration()).populate(node);

		validate(instance);

		ConfigurationImpl.MAPPER.bind(instance).serialize(node);
	}

	public Configuration getConfiguration() {
		return new ConfigurationProxy(this::getInstance);
	}

	private Configuration getInstance() {
		if (lastModified != lastModifiedSupplier.getAsLong()) {
			lastModified = lastModifiedSupplier.getAsLong();
			try {
				final ConfigurationNode node = loader.load();
				final ConfigurationImpl configuration = ConfigurationImpl.MAPPER.bind(newConfiguration()).populate(node);

				validate(configuration);
				
				configuration.version = instance.version+1;
				instance = configuration;
			} catch (ObjectMappingException | ConfigurationException | IOException ex) {
				log.warn("[{}] Unable to reload configurations: {}", Flowstone.MOD_ID, ex.getMessage(), ex);
			}
		}
		return instance;
	}

	private ConfigurationImpl newConfiguration() {
		ConfigurationImpl configuration = new ConfigurationImpl();
		configuration.getItems().clear();
		configuration.getItems()
				.addAll(Registry.BLOCK.getIds().stream().filter(id -> id.getPath().endsWith("_ore")).map(ore -> {
					Identifier block = new Identifier(ore.getNamespace(),
							ore.getPath().replaceFirst("_ore$", "_block"));
					if (Registry.BLOCK.containsId(block))
						return new ItemImpl(ore, Optional.of(block));
					return new ItemImpl(ore, Optional.empty());
				}).collect(Collectors.toSet()));
		return configuration;
	}

	private void validate(ConfigurationImpl configuration) {
		final Set<ConstraintViolation<ConfigurationImpl>> violations = validator.validate(configuration);
		final Set<String> errors = Sets.newTreeSet(String::compareTo);
		for (ConstraintViolation<ConfigurationImpl> violation : violations)
			errors.add(violation.getPropertyPath().toString().replace("[]", "") + " " + violation.getMessage());

		if (configuration.getMinChance() > configuration.getMaxChance())
			errors.add(String.format("minChance can't be greated than maxChance; minChance is %s, maxChance is %s",
					configuration.getMinChance(), configuration.getMaxChance()));

		if (!errors.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("Errors in the configuration file:");
			for (String error : errors)
				builder.append("\n\t").append(error);
			throw new ConfigurationException(builder.toString());
		}
	}

}
