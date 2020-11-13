package nivoridocs.flowstone.config;

import java.util.Collection;
import java.util.Optional;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

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
import nivoridocs.flowstone.config.custom.Exists;

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
	private Collection<@Valid Item> items = Sets.newHashSet();

	@ConfigSerializable
	@Getter
	@ToString
	@EqualsAndHashCode
	@AllArgsConstructor
	@NoArgsConstructor
	public static class ItemImpl implements Configuration.Item {

		@Setting
		@NotNull
		@Exists
		private Identifier ore;

		@Setting
		@NotNull
		@Exists
		private Optional<Identifier> block;

	}

}
