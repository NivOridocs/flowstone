package nivoridocs.flowstone.config;

import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import nivoridocs.flowstone.Flowstone;

@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Config(name = Flowstone.MOD_ID)
public final class ConfigurationImpl implements Configuration, ConfigData {

	private int minChance = 5;

	private int maxChance = 50;

	@Getter
	private int blocksLimit = 45;

	@NotNull
	private List<ItemImpl> items = Registry.BLOCK.getIds().stream().filter(id -> id.getPath().endsWith("_ore"))
			.map(ore -> {
				Identifier block = new Identifier(ore.getNamespace(), ore.getPath().replaceFirst("_ore$", "_block"));
				return new ItemImpl(ore.toString(),
						Optional.of(block).filter(Registry.BLOCK::containsId).map(Identifier::toString).orElse(null));
			}).collect(toList());;

	@Override
	public double getMinChance() {
		return minChance / 100d;
	}

	@Override
	public double getMaxChance() {
		return maxChance / 100d;
	}

	@Override
	public Collection<? extends Item> getItems() {
		return ImmutableSet.copyOf(items);
	}

	@Override
	public void validatePostLoad() throws ValidationException {
		minChance = Math.max(0, minChance);
		maxChance = Math.min(100, maxChance);
		if (minChance > maxChance) {
			int mem = maxChance;
			maxChance = minChance;
			minChance = mem;
		}
		blocksLimit = Math.max(0, blocksLimit);
		blocksLimit = Math.min(98, blocksLimit);

		for (ItemImpl item : items) {
			if (!Registry.BLOCK.containsId(item.getOre()))
				throw new ValidationException(
						item.getOre().toString() + " should be a registerd block identifier; it's not");
			if (item.getBlock().isPresent() && !Registry.BLOCK.containsId(item.getBlock().get()))
				throw new ValidationException(
						item.getBlock().get().toString() + " should be a registerd block identifier; it's not");
		}
	}

	@ToString
	@EqualsAndHashCode
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemImpl implements Configuration.Item {

		@NotNull
		private String ore;

		private String block;

		@Override
		public Identifier getOre() {
			return new Identifier(ore);
		}

		@Override
		public Optional<Identifier> getBlock() {
			return Optional.ofNullable(block).map(Identifier::new);
		}

	}

}
