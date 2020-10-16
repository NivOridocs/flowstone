package nivoridocs.flowstone;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.world.BlockEvent.FluidPlaceBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FlowstoneEventHandler {

	private static final Logger log = LogManager.getLogger();

	// TODO Move to configuration
	private static final double LOWEST = 0.05d; // in [0.0, HIGHEST_CHANCE]
	private static final double HIGHEST = 0.5d; // in [LOWEST_CHANCE, 1.0]
	private static final int LIMIT = 45; // in [0, 98]
	private static final boolean SEARCH_ORES = false;

	private static final double EPSILON = 1e-6;

	private final Map<Block, Block> storageToOreMap = Maps.newHashMap();
	private final Random random = new Random();

	@SubscribeEvent
	public void onFluidPlaceBlockEvent(FluidPlaceBlockEvent event) {
		if (Blocks.STONE.equals(event.getNewState().getBlock())) {
			computeStorageToOreMap(event.getWorld().getWorld());

			List<Block> ores = Lists.newArrayList(Tags.Blocks.ORES.getAllElements());
			final int initialOres = ores.size();

			double threshold = HIGHEST;
			if (HIGHEST - LOWEST > EPSILON && LIMIT > 0) { // -> targetThreshold > 0
				ores.addAll(searchBlocks(event.getWorld(), event.getPos()));
				final int blocksFound = Math.min(ores.size() - initialOres, LIMIT);
				threshold = (blocksFound * (HIGHEST - LOWEST) / LIMIT) + LOWEST; // may be 0
			}

			double chance = random.nextDouble(); // -> chance >= 0.0
			if (chance < threshold) { // -> threshold > 0.0
				int index = (int) ((chance / threshold) * ores.size());
				event.setNewState(ores.get(index).getDefaultState());
			}
			log.debug("Generate {}", event.getNewState().getBlock().getRegistryName());
		}
	}

	private void computeStorageToOreMap(World world) {
		storageToOreMap.clear();
		Collection<Block> ores = Tags.Blocks.ORES.getAllElements();
		for (Block ore : ores) {
			Optional<Block> storage = getStoragesFromOres(getItemFromBlock(ore), world).map(this::getBlockFromItem)
					.filter(Tags.Blocks.STORAGE_BLOCKS::contains);
			if (storage.isPresent()) {
				log.debug("Map {} to {}", storage.get().getRegistryName(), ore.getRegistryName());
				storageToOreMap.putIfAbsent(storage.get(), ore);
			}
		}
	}

	private Block getBlockFromItem(Item item) {
		return Block.getBlockFromItem(item);
	}

	@SuppressWarnings("deprecation")
	private Item getItemFromBlock(Block block) {
		return Item.getItemFromBlock(block);
	}

	private Optional<Item> getStoragesFromOres(Item ore, World world) {
		Optional<Item> semltingResult = world.getRecipeManager()
				.getRecipe(IRecipeType.SMELTING, new Inventory(ore.getDefaultInstance()), world)
				.map(FurnaceRecipe::getRecipeOutput).map(ItemStack::getItem);
		Optional<Item> result = Optional.empty();
		if (semltingResult.isPresent()) {
			Container container = new Container(null, 0) {
				@Override
				public boolean canInteractWith(PlayerEntity playerIn) {
					return false;
				}
			};
			CraftingInventory inventory = new CraftingInventory(container, 3, 3);
			for (int i = 0; i < inventory.getSizeInventory(); i++)
				inventory.setInventorySlotContents(i, semltingResult.get().getDefaultInstance());
			result = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, world)
					.map(ICraftingRecipe::getRecipeOutput).map(ItemStack::getItem);
		}
		return result;
	}

	private Collection<Block> searchBlocks(IWorld world, BlockPos pos) {
		Collection<Block> result = Lists.newArrayListWithCapacity(LIMIT);
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				for (int z = -2; z <= 2; z++) {
					if (Math.abs(x) == 2 || Math.abs(y) == 2 || Math.abs(z) == 2) {
						if (result.size() < LIMIT)
							getOre(world.getBlockState(pos.add(x, y, z)).getBlock()).ifPresent(result::add);
						else return result;
					}
				}
			}
		}
		return result;
	}

	private Optional<Block> getOre(Block block) {
		if (SEARCH_ORES)
			return generateFromOres(block);
		else
			return generateFromStorages(block);
	}

	private Optional<Block> generateFromOres(Block block) {
		return Tags.Blocks.ORES.contains(block) ? Optional.of(block) : Optional.empty();
	}

	private Optional<Block> generateFromStorages(Block block) {
		if (Tags.Blocks.STORAGE_BLOCKS.contains(block)) {
			return Optional.ofNullable(getStorageToOreMap().get(block));
		} else {
			return Optional.empty();
		}
	}

	private Map<Block, Block> getStorageToOreMap() {
		if (storageToOreMap == null)
			throw new NullPointerException();
		return storageToOreMap;
	}

}
