package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.LavaFluid;
import niv.flowstone.FlowstoneGenerator;

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
    public boolean setBlockStateProxy(LevelAccessor levelAccessor, BlockPos pos, BlockState state, int flags) {
        if (levelAccessor instanceof Level level) {
            state = FlowstoneGenerator
                    .findReplace(state.getBlock(), level)
                    .map(Block::defaultBlockState)
                    .orElse(state);
        }
        return levelAccessor.setBlock(pos, state, flags);
    }
}
