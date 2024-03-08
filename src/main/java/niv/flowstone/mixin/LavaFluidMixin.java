package niv.flowstone.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;
import niv.flowstone.config.FlowstoneConfig;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

    private static final String LEVEL_ACCESSOR = "Lnet/minecraft/world/level/LevelAccessor;";
    private static final String BLOCK_POS = "Lnet/minecraft/core/BlockPos;";
    private static final String BLOCK_STATE = "Lnet/minecraft/world/level/block/state/BlockState;";
    private static final String DIRECTION = "Lnet/minecraft/core/Direction;";
    private static final String FLUID_STATE = "Lnet/minecraft/world/level/material/FluidState;";

    @Redirect( //
            method = "spreadTo(" + LEVEL_ACCESSOR + BLOCK_POS + BLOCK_STATE + DIRECTION + FLUID_STATE + ")V", //
            at = @At(value = "INVOKE", //
                    target = LEVEL_ACCESSOR + "setBlock(" + BLOCK_POS + BLOCK_STATE + "I" + ")Z"))
    public boolean setBlockStateProxy(LevelAccessor world, BlockPos pos, BlockState state, int flags) {
        if (FlowstoneConfig.getInstance().isEnabled() && state.getBlock().equals(Blocks.STONE)) {
            var recipes = FlowstoneConfig.getInstance().getRecipes();
            var states = new ArrayList<BlockState>(recipes.size());
            for (var recipe : recipes) {
                if (BuiltInRegistries.BLOCK.containsKey(recipe.getBlock())
                        && world.getRandom().nextDouble() <= recipe.getChance()) {
                    states.add(BuiltInRegistries.BLOCK.get(recipe.getBlock()).defaultBlockState());
                }
            }
            if (!states.isEmpty()) {
                state = states.get(world.getRandom().nextInt(states.size()));
            }
        }
        return world.setBlock(pos, state, flags);
    }
}
