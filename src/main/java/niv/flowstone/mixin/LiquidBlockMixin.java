package niv.flowstone.mixin;

import static niv.flowstone.FlowstoneGenerator.replace;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {

    private static final String LEVEL = "Lnet/minecraft/world/level/Level;";
    private static final String BLOCK_POS = "Lnet/minecraft/core/BlockPos;";
    private static final String BLOCK_STATE = "Lnet/minecraft/world/level/block/state/BlockState;";

    @Redirect( //
            method = "shouldSpreadLiquid(" + LEVEL + BLOCK_POS + BLOCK_STATE + ")Z", //
            at = @At(value = "INVOKE", //
                    target = LEVEL + "setBlockAndUpdate(" + BLOCK_POS + BLOCK_STATE + ")Z"))
    public boolean setBlockAndUpdateProxy(Level level, BlockPos pos, BlockState state) {
        return level.setBlockAndUpdate(pos, replace(level, pos, state));
    }
}
