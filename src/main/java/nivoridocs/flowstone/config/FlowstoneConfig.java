package nivoridocs.flowstone.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Ignore;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;
import net.minecraftforge.fml.common.registry.GameRegistry;
import nivoridocs.flowstone.Flowstone;

@Config(modid = Flowstone.MODID)
@Config.LangKey("flowstone.config.title")
public class FlowstoneConfig {

	@RequiresWorldRestart
	public static Map<String, Integer> insteadOfCobblestone = defaultForCobblestone();
	
	@Ignore private static Block[] cobblestoneBlock;
	@Ignore private static double[] cobblestoneDouble;
	
	@RequiresWorldRestart
	public static Map<String, Integer> insteadOfStone = defaultForStone();
	
	@Ignore private static Block[] stoneBlock;
	@Ignore private static double[] stoneDouble;
	
	@RequiresWorldRestart
	public static Map<String, Integer> insteadOfObsidian = defaultForObsidian();
	
	@Ignore private static Block[] obsidianBlock;
	@Ignore private static double[] obsidianDouble;

	private static Map<String, Integer> defaultForCobblestone() {
		Map<String, Integer> map = new HashMap<>();
		map.put(Blocks.COBBLESTONE.getRegistryName().toString(), 2);
		map.put(Blocks.GRAVEL.getRegistryName().toString(), 1);
		map.put(Blocks.SAND.getRegistryName().toString(), 1);
		return map;
	}

	private static Map<String, Integer> defaultForStone() {
		Map<String, Integer> map = new HashMap<>();
		map.put(Blocks.STONE.getRegistryName().toString(), 8);
		map.put(Blocks.IRON_ORE.getRegistryName().toString(), 4);
		map.put(Blocks.GOLD_ORE.getRegistryName().toString(), 2);
		map.put(Blocks.LAPIS_ORE.getRegistryName().toString(), 1);
		map.put(Blocks.REDSTONE_ORE.getRegistryName().toString(), 1);
		return map;
	}

	private static Map<String, Integer> defaultForObsidian() {
		Map<String, Integer> map = new HashMap<>();
		map.put(Blocks.OBSIDIAN.getRegistryName().toString(), 9);
		map.put(Blocks.DIAMOND_ORE.getRegistryName().toString(), 1);
		return map;
	}

	private static double[] getDistribution(Map<String, Integer> map) {
		double max = map.values().stream().mapToDouble(Integer::doubleValue).sum();
		double[] array = map.keySet().stream().sorted().mapToDouble(map::get)
				.filter(x -> x > 0).map(x -> x / max).toArray();
		for (int i = 1; i < array.length; i++)
			array[i] += array[i-1];
		return array;
	}
	
	private static Block[] getBlock(Map<String, Integer> map) {
		return map.keySet().stream().sorted().map(Block::getBlockFromName)
				.toArray(size -> new Block[size]);
	}
	
	private static int getIndex(double[] doubles, Random random) {
		int index = Arrays.binarySearch(doubles, random.nextDouble());
		return index < 0 ? - index - 1 : index;
	}
	
	public static Block randomForCobblestone(Random random) {
		if (cobblestoneBlock == null)
			cobblestoneBlock = getBlock(insteadOfCobblestone);
		if (cobblestoneDouble == null)
			cobblestoneDouble = getDistribution(insteadOfCobblestone);
		return cobblestoneBlock[getIndex(cobblestoneDouble, random)];
	}
	
	public static Block randomForStone(Random random) {
		if (stoneBlock == null)
			stoneBlock = getBlock(insteadOfStone);
		if (stoneDouble == null)
			stoneDouble = getDistribution(insteadOfStone);
		return stoneBlock[getIndex(stoneDouble, random)];
	}
	
	public static Block randomForObsidian(Random random) {
		if (obsidianBlock == null)
			obsidianBlock = getBlock(insteadOfObsidian);
		if (obsidianDouble == null)
			obsidianDouble = getDistribution(insteadOfObsidian);
		return obsidianBlock[getIndex(obsidianDouble, random)];
	}

}
