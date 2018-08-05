package nivoridocs.flowstone.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
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
	public static Map<String, Integer> cobblestone = defaultForCobblestone();
	
	@Ignore private static Block[] cobblestoneBlock;
	@Ignore private static double[] cobblestoneDouble;
	
	@RequiresWorldRestart
	public static Map<String, Integer> stone = defaultForStone();
	
	@Ignore private static Block[] stoneBlock;
	@Ignore private static double[] stoneDouble;
	
	@RequiresWorldRestart
	public static Map<String, Integer> obsidian = defaultForObsidian();
	
	@Ignore private static Block[] obsidianBlock;
	@Ignore private static double[] obsidianDouble;
	
	private static Map<String, Integer> defaultFor(Block block) {
		Map<String, Integer> map = new HashMap<>();
		map.put(block.getRegistryName().toString(), 1);
		return map;
	}
	
	private static String getName(Block block) {
		return block.getRegistryName().toString();
	}
	
	private static Map<String, Integer> defaultForCobblestone() {
		Map<String, Integer> map = new HashMap<>();
		map.put(getName(Blocks.COBBLESTONE), 1);
		return map;
	}
	
	private static Map<String, Integer> defaultForStone() {
		Map<String, Integer> map = new HashMap<>();
		map.put(getName(Blocks.STONE), 256);
		map.put(getName(Blocks.IRON_ORE), 64);
		map.put(getName(Blocks.REDSTONE_ORE), 16);
		map.put(getName(Blocks.GOLD_ORE), 4);
		map.put(getName(Blocks.LAPIS_ORE), 1);
		map.put(getName(Blocks.DIAMOND_ORE), 1);
		return map;
	}
	
	private static Map<String, Integer> defaultForObsidian() {
		Map<String, Integer> map = new HashMap<>();
		map.put(getName(Blocks.OBSIDIAN), 1);
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
	
	private static Block[] getBlocks(Map<String, Integer> map) {
		return map.entrySet().stream().filter(e -> e.getValue()  > 0)
				.map(Entry::getKey).sorted().map(Block::getBlockFromName)
				.toArray(size -> new Block[size]);
	}
	
	private static int getIndex(double[] doubles, Random random) {
		int index = Arrays.binarySearch(doubles, random.nextDouble());
		return index < 0 ? - index - 1 : index;
	}
	
	private static Optional<Block> randomForCobblestone(Random random) {
		if (cobblestoneBlock == null)
			cobblestoneBlock = getBlocks(cobblestone);
		if (cobblestoneDouble == null)
			cobblestoneDouble = getDistribution(cobblestone);
		return Optional.of(cobblestoneBlock[getIndex(cobblestoneDouble, random)]);
	}
	
	private static Optional<Block> randomForStone(Random random) {
		if (stoneBlock == null)
			stoneBlock = getBlocks(stone);
		if (stoneDouble == null)
			stoneDouble = getDistribution(stone);
		return Optional.of(stoneBlock[getIndex(stoneDouble, random)]);
	}
	
	private static Optional<Block> randomForObsidian(Random random) {
		if (obsidianBlock == null)
			obsidianBlock = getBlocks(obsidian);
		if (obsidianDouble == null)
			obsidianDouble = getDistribution(obsidian);
		return Optional.of(obsidianBlock[getIndex(obsidianDouble, random)]);
	}
	
	public static void reset() {
		cobblestoneBlock = null;
		cobblestoneDouble = null;
		stoneBlock = null;
		stoneDouble = null;
		obsidianBlock = null;
		obsidianDouble = null;
	}
	
	public static Optional<Block> randomFor(Block block, Random random) {
		Optional<Block> result = Optional.empty();
		if (block == Blocks.COBBLESTONE)
			result = randomForCobblestone(random);
		if (block == Blocks.STONE)
			result = randomForStone(random);
		if (block == Blocks.OBSIDIAN)
			result = randomForStone(random);
		return result.filter(x -> x != block);
	}

}
