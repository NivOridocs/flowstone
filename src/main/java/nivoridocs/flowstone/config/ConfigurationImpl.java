package nivoridocs.flowstone.config;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minecraft.util.Identifier;

@Data
@NoArgsConstructor
public final class ConfigurationImpl implements Configuration {

	private long version = 0;

	private double minChance = .05d;

	private double maxChance = .5d;

	private int blocksLimit = 45;

	@NotNull
	private List<ItemImpl> items = Lists.newArrayList();

	@Override
	public Collection<? extends Item> getItems() {
		return ImmutableSet.copyOf(items);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemImpl implements Configuration.Item {

		@NotNull
		private Identifier ore;

		@NotNull
		private Optional<Identifier> block;

	}

}
