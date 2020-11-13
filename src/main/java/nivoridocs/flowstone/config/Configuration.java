package nivoridocs.flowstone.config;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import com.google.common.collect.Sets;
import com.google.common.reflect.TypeToken;

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
public class Configuration {

//	private static final Logger log = LogManager.getLogger();

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

//				final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//				final Validator validator = factory.getValidator();
//
//				final Set<ConstraintViolation<Configuration>> violations = validator.validate(configuration);
//				for (ConstraintViolation<Configuration> violation : violations)
//					log.error("[{}] {}", Flowstone.MOD_ID, violation.getMessage());

				configuration.saveTo(node);
				loader.save(node);
			} catch (IOException | ObjectMappingException ex) {
				throw new ExceptionInInitializerError(ex);
			}
		}
		return configuration;
	}
	
	@Setting("min-chance")
	@PositiveOrZero
	@Max(1)
	private double minChance = .05d;

	@Setting("max-chance")
	@PositiveOrZero
	@Max(1)
	private double maxChance = .5d;

	@Setting("blocks-limit")
	@PositiveOrZero
	@Max(98)
	private int blocksLimit = 45;

	@Setting("items")
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

	public double getMinChance() {
		return minChance;
	}

	public double getMaxChance() {
		return maxChance;
	}

	public int getBlocksLimit() {
		return blocksLimit;
	}

	public Set<Item> getItems() {
		return items;
	}

	public void saveTo(ConfigurationNode node) throws ObjectMappingException {
		MAPPER.bind(this).serialize(node);
	}

	@Override
	public String toString() {
		return "Configuration [minChance=" + minChance + ", maxChance=" + maxChance + ", blocksLimit=" + blocksLimit
				+ ", items=" + items + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blocksLimit;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
		long temp;
		temp = Double.doubleToLongBits(maxChance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(minChance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Configuration other = (Configuration) obj;
		if (blocksLimit != other.blocksLimit)
			return false;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return Double.doubleToLongBits(maxChance) != Double.doubleToLongBits(other.maxChance)
				&& Double.doubleToLongBits(minChance) != Double.doubleToLongBits(other.minChance);
	}

	@ConfigSerializable
	public static class Item {
		@Setting("ore")
		@NotNull
		@Valid
		private Identifier ore;

		@Setting("block")
		@NotNull
		private Optional<@Valid Identifier> block;

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

		public Identifier getOre() {
			return ore;
		}

		public Optional<Identifier> getBlock() {
			return block;
		}

		@Override
		public String toString() {
			return "Item [ore=" + ore + ", block=" + block + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + block.hashCode();
			result = prime * result + ((ore == null) ? 0 : ore.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Item other = (Item) obj;
			if (!block.equals(other.block))
				return false;
			if (ore == null) {
				if (other.ore != null)
					return false;
			} else if (!ore.equals(other.ore))
				return false;
			return true;
		}

	}

}
