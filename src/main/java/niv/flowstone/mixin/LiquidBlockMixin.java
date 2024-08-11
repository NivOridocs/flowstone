package niv.flowstone.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import niv.flowstone.Flowstone;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {

    @Shadow
    @Final
    protected FlowingFluid fluid;

    @Overwrite
    private boolean shouldSpreadLiquid(Level level, BlockPos pos, BlockState state) {
        if (this.fluid.is(FluidTags.LAVA)) {
            Block block = null;
            var overSoulSoil = level.getBlockState(pos.below()).is(Blocks.SOUL_SOIL);
            for (var direction : LiquidBlock.POSSIBLE_FLOW_DIRECTIONS) {
                var opposite = pos.relative(direction.getOpposite());
                var nearBlueIce = level.getBlockState(opposite).is(Blocks.BLUE_ICE);

                if (level.getFluidState(opposite).is(FluidTags.WATER)) {
                    block = level.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
                } else if (nearBlueIce && overSoulSoil) {
                    block = Blocks.BASALT;
                } else if (nearBlueIce && level.dimension() == Level.NETHER) {
                    block = Blocks.NETHERRACK;
                }

                if (block != null) {
                    level.setBlockAndUpdate(pos, Flowstone.replace(level, pos, block.defaultBlockState()));
                    this.fizz(level, pos);
                    return false;
                }
            }
        }
        return true;
    }

    @Shadow
    protected abstract void fizz(LevelAccessor levelAccessor, BlockPos blockPos);
}
