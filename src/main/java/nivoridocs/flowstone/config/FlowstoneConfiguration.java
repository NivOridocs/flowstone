package nivoridocs.flowstone.config;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigHolder;

public class FlowstoneConfiguration {

	private static FlowstoneConfiguration instance = null;

	public static final FlowstoneConfiguration getInstance() {
		if (instance == null)
			instance = new FlowstoneConfiguration();
		return instance;
	}

	private ConfigHolder<ConfigurationImpl> holder = null;

	private FlowstoneConfiguration() {
	}

	public Configuration getConfiguration() {
		return new ConfigurationProxy(this::proxyConfiguration);
	}

	public Configuration proxyConfiguration() {
		if (holder == null)
			holder = AutoConfig.getConfigHolder(ConfigurationImpl.class);
		return holder.getConfig();
	}

}
