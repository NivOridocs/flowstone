package nivoridocs.flowstone;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Consumer;

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

	private static final double LOWEST_CHANCE = 0.01d;
	private static final double HIGHEST_CHANCE = 0.25d;
	private static final double BLOCKS_LIMIT = 98.0d;
	private static final double THRESHOLD = BLOCKS_LIMIT * (HIGHEST_CHANCE - LOWEST_CHANCE);

	private static final boolean SEARCH_ORES = true;

	private final Map<Block, Block> storageToOreMap = Maps.newHashMap();
	private final Random random = new Random();

	@SubscribeEvent
	public void onFluidPlaceBlockEvent(FluidPlaceBlockEvent event) {
		if (Blocks.STONE.equals(event.getNewState().getBlock())) {
			computeStorageToOreMap(event.getWorld().getWorld());

			List<Block> ores = Lists.newArrayList(Tags.Blocks.ORES.getAllElements());

			final int initialOres = ores.size();
			searchBlocks(event.getWorld(), event.getPos(), ores::add);
			final int blocksFound = initialOres - ores.size();

			double threshold = (blocksFound / THRESHOLD) + LOWEST_CHANCE;
			double chance = random.nextDouble();
			if (chance < threshold) {
				int index = (int) ((chance / threshold) * ores.size());
				event.setNewState(ores.get(index).getDefaultState());
			}
		}
	}

	private void computeStorageToOreMap(World world) {
		storageToOreMap.clear();
		Collection<Block> ores = Tags.Blocks.ORES.getAllElements();
		for (Block ore : ores) {
			Optional<Block> storage = getStoragesFromOres(getItemFromBlock(ore), world).map(this::getBlockFromItem)
					.filter(Tags.Blocks.STORAGE_BLOCKS::contains);
			if (storage.isPresent())
				storageToOreMap.putIfAbsent(storage.get(), ore);
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
			container.inventorySlots.forEach(slot -> slot.putStack(semltingResult.get().getDefaultInstance()));
			CraftingInventory inventory = new CraftingInventory(container, 3, 3);
			result = world.getRecipeManager().getRecipe(IRecipeType.CRAFTING, inventory, world)
					.map(ICraftingRecipe::getRecipeOutput).map(ItemStack::getItem);
		}
		return result;
	}

	private void searchBlocks(IWorld world, BlockPos pos, Consumer<Block> onBlockFound) {
		for (int x = -2; x <= 2; x++) {
			for (int y = -2; y <= 2; y++) {
				getOre(world.getBlockState(pos.add(x, y, +2)).getBlock()).ifPresent(onBlockFound);
				getOre(world.getBlockState(pos.add(x, y, -2)).getBlock()).ifPresent(onBlockFound);
				if (Math.abs(x) == 2 || Math.abs(y) == 2) {
					for (int z = -1; z <= 1; z++) {
						getOre(world.getBlockState(pos.add(x, y, z)).getBlock()).ifPresent(onBlockFound);
					}
				}
			}
		}
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
