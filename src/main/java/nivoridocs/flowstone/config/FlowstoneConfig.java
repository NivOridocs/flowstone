package nivoridocs.flowstone.config;

import static net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import nivoridocs.flowstone.Flowstone;

@Mod.EventBusSubscriber(modid = Flowstone.MODID, bus = MOD)
public class FlowstoneConfig {

	private static String prefix = null;

	private static final String local(String path) {
		if (prefix == null)
			prefix = "config." + Flowstone.MODID + ".";
		return prefix + path;
	}

	private static final Common COMMON;
	private static final ForgeConfigSpec COMMON_SPEC;

	public static final double DEFAULT_LOWEST_CHANCE = 0.05D;
	public static final double DEFAULT_HIGHEST_CHANCE = 0.5D;
	public static final int DEFAULT_BLOCKS_LIMIT = 45;

	static {
		Pair<Common, ForgeConfigSpec> pair = new Builder().configure(Common::new);
		COMMON = pair.getKey();
		COMMON_SPEC = pair.getValue();
	}

	public static final void setup() {
		ModLoadingContext context = ModLoadingContext.get();
		context.registerConfig(Type.COMMON, COMMON_SPEC);
	}

	public static final double lowestChance() {
		return COMMON.lowestChance.get();
	}

	public static final double highestChance() {
		return COMMON.highestChance.get();
	}

	public static final int blocksLimit() {
		return COMMON.blocksLimit.get();
	}

	private FlowstoneConfig() {
		//
	}

	public static class Common {
		private final DoubleValue lowestChance; // [0.0, highestChance]
		private final DoubleValue highestChance; // [lowestChance, 1.0]
		private final IntValue blocksLimit; // [0, 98]

		Common(Builder builder) {
			builder.push("customization");

			lowestChance = builder.translation(local("lowest_chance"))
					.comment("Minimum chance of generating a ore block")
					.defineInRange("lowestChance", DEFAULT_LOWEST_CHANCE, 0.0D, 1.0D);

			highestChance = builder.translation(local("highest_chance"))
					.comment("Maximum chance of generating a ore block")
					.defineInRange("highestChance", DEFAULT_HIGHEST_CHANCE, 0.0D, 1.0D);

			blocksLimit = builder.translation(local("blocks_limit"))
					.comment("Maximum number of blocks influencing the genration")
					.defineInRange("blocksLimit", DEFAULT_BLOCKS_LIMIT, 0, 98);

			builder.pop();
		}
	}

}
