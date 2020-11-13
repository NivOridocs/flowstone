package nivoridocs.flowstone.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationException;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import nivoridocs.flowstone.Flowstone;

@ConfigSerializable
@Getter
@ToString
@EqualsAndHashCode
public class Configuration {

	private static final Logger log = LogManager.getLogger();

	private static final ObjectMapper<Configuration> MAPPER;

	private static Configuration configuration = null;

	static {
		try {
			MAPPER = ObjectMapper.forClass(Configuration.class);
		} catch (ObjectMappingException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static Configuration loadFrom(ConfigurationNode node) throws ObjectMappingException {
		return MAPPER.bindToNew().populate(node);
	}

	@SuppressWarnings("serial")
	public static Configuration getInstance() {
		if (configuration == null) {
			try {
				final Path path = FabricLoader.getInstance().getConfigDir()
						.resolve(Flowstone.MOD_ID + "-configuration.json");
				final GsonConfigurationLoader loader = GsonConfigurationLoader.builder().setPath(path).build();

				TypeSerializerCollection.defaults().register(new TypeToken<Identifier>() {
				}, new IdentifierSerializer());
				TypeSerializerCollection.defaults().register(new TypeToken<Optional<Identifier>>() {
				}, new OptionalIdentifierSerializer());

				final ConfigurationNode node = loader.load();
				configuration = Configuration.loadFrom(node);

				Locale.setDefault(Locale.US);
				final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
				final Validator validator = factory.getValidator();

				final Set<ConstraintViolation<Configuration>> violations = validator.validate(configuration);
				boolean anyViolation = !violations.isEmpty();
				for (ConstraintViolation<Configuration> violation : violations)
					log.error("[{}] Configuration constraint violation: '{}' {}", Flowstone.MOD_ID,
							violation.getPropertyPath(), violation.getMessage());

				if (configuration.getMinChance() > configuration.getMaxChance()) {
					anyViolation = true;
					log.error(
							"[{}] Configuration constraint violation: 'minChances' can't be greated than 'maxChances'; 'minChances' is {}, 'maxChances' is {}",
							Flowstone.MOD_ID, configuration.getMinChance(), configuration.getMaxChance());
				}

				if (anyViolation)
					throw new ConfigurationException("Constraint violations detected");

				configuration.saveTo(node);
				loader.save(node);
			} catch (IOException | ObjectMappingException ex) {
				throw new ConfigurationException(ex.getMessage(), ex);
			}
		}
		return configuration;
	}

	@Setting
	@PositiveOrZero
	@Max(1)
	private double minChance = .05d;

	@Setting
	@PositiveOrZero
	@Max(1)
	private double maxChance = .5d;

	@Setting
	@PositiveOrZero
	@Max(98)
	private int blocksLimit = 45;

	@Setting
	@NotNull
	private Set<@Valid Item> items = Sets.newHashSet();

	public Configuration() {
		Registry.BLOCK.getIds().stream().filter(id -> id.getPath().endsWith("_ore")).map(ore -> {
			if (ore.getPath().endsWith("_ore")) {
				Identifier block = new Identifier(ore.getNamespace(), ore.getPath().replaceFirst("_ore$", "_block"));
				if (Registry.BLOCK.containsId(block))
					return new Item(ore, block);
			}
			return new Item(ore);
		}).forEach(items::add);
	}

	public void saveTo(ConfigurationNode node) throws ObjectMappingException {
		MAPPER.bind(this).serialize(node);
	}

	@ConfigSerializable
	@Getter
	@ToString
	@EqualsAndHashCode
	public static class Item {
		@Setting
		@NotNull
		@Exists
		private Identifier ore;

		@Setting
		@NotNull
		private Optional<@Exists Identifier> block;

		public Item() {
			this(Registry.BLOCK.getId(Blocks.STONE));
		}

		public Item(Identifier ore) {
			this(ore, Optional.empty());
		}

		public Item(Identifier ore, Identifier block) {
			this(ore, Optional.ofNullable(block));
		}

		public Item(Identifier ore, Optional<Identifier> block) {
			super();
			this.ore = ore;
			this.block = block;
		}

	}

}
