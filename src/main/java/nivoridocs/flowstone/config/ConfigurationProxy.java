package nivoridocs.flowstone.config;

import java.util.Collection;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class ConfigurationProxy implements Configuration {

	@NonNull
	private final Supplier<Configuration> proxy;

	@Override
	public long getVersion() {
		return proxy.get().getVersion();
	}

	@Override
	public double getMinChance() {
		return proxy.get().getMinChance();
	}

	@Override
	public double getMaxChance() {
		return proxy.get().getMaxChance();
	}

	@Override
	public int getBlocksLimit() {
		return proxy.get().getBlocksLimit();
	}

	@Override
	public Collection<Item> getItems() {
		return ImmutableSet.copyOf(proxy.get().getItems());
	}

}
