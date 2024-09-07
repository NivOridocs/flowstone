package niv.flowstone.mixin;

import static niv.flowstone.config.Configuration.enableBasaltGeneration;
import static niv.flowstone.config.Configuration.enableNetherrackGeneration;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Shadow
    protected abstract void fizz(LevelAccessor world, BlockPos pos);

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void shouldSpreadLiquidProxy(Level level, BlockPos pos, BlockState state,
            CallbackInfoReturnable<Boolean> context) {
        context.setReturnValue(shouldSpreadLiquidLogic(level, pos, state));
        context.cancel();
    }

    @SuppressWarnings("deprecation")
    private boolean shouldSpreadLiquidLogic(Level level, BlockPos pos, BlockState state) {
        if (this.fluid.is(FluidTags.LAVA)) {
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
                    this.fizz(level, pos);
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
