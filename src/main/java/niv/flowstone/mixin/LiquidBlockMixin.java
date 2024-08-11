package niv.flowstone.mixin;

import static niv.flowstone.config.Configuration.enableBasaltGeneration;
import static niv.flowstone.config.Configuration.enableNetherrackGeneration;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import niv.flowstone.Flowstone;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {

    /**
     * @author
     * @reason
     */
    @Overwrite
    @SuppressWarnings("deprecation")
    public boolean shouldSpreadLiquid(Level level, BlockPos pos, BlockState state) {
        var self = (LiquidBlock) (Object) this;
        if (self.fluid.is(FluidTags.LAVA)) {
            var overSoulSoil = level.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);
            for (var direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
                var opposite = pos.relative(direction.getOpposite());
                var block = getBlock(
                        level.getFluidState(opposite).is(FluidTags.WATER),
                        level.getFluidState(pos).isSource(),
                        overSoulSoil,
                        level.getBlockState(opposite).is(Blocks.BLUE_ICE),
                        level.dimension() == Level.NETHER);
                if (block != null) {
                    level.setBlockAndUpdate(pos, Flowstone.replace(level, pos, block.defaultBlockState()));
                    self.fizz(level, pos);
                    return false;
                }
            }
        }
        return true;
    }

    private Block getBlock(boolean isWater, boolean isSource,
            boolean overSoulSoil, boolean nearBlueIce, boolean inTheNether) {
        if (isWater && isSource) {
            return Blocks.OBSIDIAN;
        } else if (isWater) {
            return Blocks.COBBLESTONE;
        } else if (enableBasaltGeneration() && nearBlueIce && overSoulSoil) {
            return Blocks.BASALT;
        } else if (enableNetherrackGeneration() && nearBlueIce && inTheNether) {
            return Blocks.NETHERRACK;
        } else {
            return null;
        }
    }
}
