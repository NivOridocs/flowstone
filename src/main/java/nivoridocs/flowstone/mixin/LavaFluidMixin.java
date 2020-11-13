package nivoridocs.flowstone.mixin;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import nivoridocs.flowstone.Flowstone;
import nivoridocs.flowstone.config.Configuration;
import nivoridocs.flowstone.config.Configuration.Item;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

	private static final double EPSILON = 1e-6;

	private final Configuration configuration = Flowstone.getConfiguration();

	private final List<Block> oreBlocksCache = Lists.newArrayList();
	private long oreBlocksCacheVersion = 0L;

	private final Map<Block, Block> blockToOreMapCache = Maps.newHashMap();
	private long blockToOreMapCacheVersion = 0L;

	@Redirect(method = "flow(Lnet/minecraft/world/IWorld;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/Direction;Lnet/minecraft/fluid/FluidState;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/IWorld;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"))
	public boolean setBlockStateProxy(IWorld world, BlockPos pos, BlockState state, int flags) {
		double lowest = configuration.getMinChance();
		double highest = configuration.getMaxChance();
		int limit = configuration.getBlocksLimit();

		List<Block> ores = Lists.newArrayList(getOreBlocks());
		final int initialOres = ores.size();

		double threshold = highest;
		if (highest - lowest > EPSILON && limit > 0) { // -> targetThreshold > 0
			ores.addAll(getNearSotrageBlocks(world, pos));
			final int blocksFound = Math.min(ores.size() - initialOres, limit);
			threshold = (blocksFound * (highest - lowest) / limit) + lowest; // may be 0
		}

		double chance = world.getRandom().nextDouble(); // -> chance >= 0.0
		if (chance < threshold) { // -> threshold > 0.0
			int index = (int) ((chance / threshold) * ores.size());
			return world.setBlockState(pos, ores.get(index).getDefaultState(), 3);
		} else {
			return world.setBlockState(pos, Blocks.STONE.getDefaultState(), 3);
		}
	}

	private Collection<Block> getNearSotrageBlocks(IWorld world, BlockPos pos) {
		int limit = configuration.getBlocksLimit();
		Collection<Block> result = Lists.newArrayListWithCapacity(limit);
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -2; z <= 2; z++) {
					if (Math.abs(x) == 2 || Math.abs(y) == 2 || Math.abs(z) == 2) {
						if (result.size() < limit)
							Optional.ofNullable(
									getBlockToOreMap().get(world.getBlockState(pos.add(x, y, z)).getBlock()))
									.ifPresent(result::add);
						else
							return result;
					}
				}
			}
		}
		return result;
	}

	private List<Block> getOreBlocks() {
		if (oreBlocksCacheVersion != configuration.getVersion()) {
			oreBlocksCacheVersion = configuration.getVersion();
			oreBlocksCache.clear();
			configuration.getItems().stream().map(Item::getOre).map(Registry.BLOCK::get).forEach(oreBlocksCache::add);
		}
		return oreBlocksCache;
	}

	private Map<Block, Block> getBlockToOreMap() {
		if (blockToOreMapCacheVersion != configuration.getVersion()) {
			blockToOreMapCacheVersion = configuration.getVersion();
			blockToOreMapCache.clear();
			configuration.getItems().forEach(this::putShortcut);
		}
		return blockToOreMapCache;
	}

	private void putShortcut(Item item) {
		Optional<Block> block = item.getBlock().flatMap(Registry.BLOCK::getOrEmpty);
		Optional<Block> ore = Registry.BLOCK.getOrEmpty(item.getOre());
		if (block.isPresent() && ore.isPresent())
			blockToOreMapCache.put(block.get(), ore.get());
	}

}
