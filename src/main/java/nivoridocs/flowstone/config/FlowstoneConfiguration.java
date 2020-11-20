package nivoridocs.flowstone.config;

import java.util.concurrent.atomic.AtomicBoolean;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.serializer.Toml4jConfigSerializer;

public class FlowstoneConfiguration {

	private static FlowstoneConfiguration instance = null;

	public static final FlowstoneConfiguration getInstance() {
		if (instance == null)
			instance = new FlowstoneConfiguration();
		return instance;
	}

	private final AtomicBoolean registered = new AtomicBoolean(false);

	private FlowstoneConfiguration() {
	}

	public Configuration getConfiguration() {
		if (registered.compareAndSet(false, true))
			AutoConfig.register(Configuration.class, Toml4jConfigSerializer::new);
		return AutoConfig.getConfigHolder(Configuration.class).getConfig();
	}

}
