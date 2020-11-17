package nivoridocs.flowstone.config;

import java.util.Collection;
import java.util.Optional;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import net.minecraft.util.Identifier;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public final class ConfigurationImpl implements Configuration {

	static final ObjectMapper<ConfigurationImpl> MAPPER;

	static {
		try {
			MAPPER = ObjectMapper.forClass(ConfigurationImpl.class);
		} catch (ObjectMappingException ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	long version;

	@Setting
	private double minChance = .05d;

	@Setting
	private double maxChance = .5d;

	@Setting
	private int blocksLimit = 45;

	@Setting
	private Collection<Item> items = Sets.newHashSet();
	
	final Collection<Item> getItemsMutable() {
		return items;
	}

	@Override
	public Collection<Item> getItems() {
		return ImmutableSet.copyOf(items);
	}

	@ConfigSerializable
	@Getter
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ItemImpl implements Configuration.Item {

		@Setting
		private Identifier ore;

		@Setting
		private Optional<Identifier> block;

	}

}
