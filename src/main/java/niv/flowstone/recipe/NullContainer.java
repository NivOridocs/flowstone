package niv.flowstone.recipe;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface NullContainer extends Container {

    @Override
    default void clearContent() {
    }

    @Override
    default int getContainerSize() {
        return 0;
    }

    @Override
    default boolean isEmpty() {
        return true;
    }

    @Override
    default ItemStack getItem(int var1) {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItem(int var1, int var2) {
        return ItemStack.EMPTY;
    }

    @Override
    default ItemStack removeItemNoUpdate(int var1) {
        return ItemStack.EMPTY;
    }

    @Override
    default void setItem(int var1, ItemStack var2) {
    }

    @Override
    default void setChanged() {
    }

    @Override
    default boolean stillValid(Player var1) {
        return true;
    }
}
