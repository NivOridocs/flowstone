package nivoridocs.flowstone.config;

import java.util.concurrent.atomic.AtomicBoolean;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;

public class FlowstoneConfiguration {

	private static FlowstoneConfiguration instance = null;

	public static final FlowstoneConfiguration getInstance() {
		if (instance == null)
			instance = new FlowstoneConfiguration();
		return instance;
	}

	private final AtomicBoolean registered = new AtomicBoolean(false);
	private ConfigHolder<ConfigurationImpl> holder = null;

	private FlowstoneConfiguration() {
	}

	public Configuration getConfiguration() {
		return new ConfigurationProxy(this::proxyConfiguration);
	}

	public Configuration proxyConfiguration() {
		registerConfig();
		if (holder == null)
			holder = AutoConfig.getConfigHolder(ConfigurationImpl.class);
		return holder.getConfig();
	}

	public void registerConfig() {
		if (registered.compareAndSet(false, true))
			AutoConfig.register(ConfigurationImpl.class, Toml4jConfigSerializer::new);
	}

}
