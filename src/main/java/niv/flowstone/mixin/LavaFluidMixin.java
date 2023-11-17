package niv.flowstone.mixin;

import java.util.ArrayList;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.LavaFluid;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import niv.flowstone.config.FlowstoneConfig;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {

    @Redirect(method = "flow(" + "Lnet/minecraft/world/WorldAccess;"
            + "Lnet/minecraft/util/math/BlockPos;" + "Lnet/minecraft/block/BlockState;"
            + "Lnet/minecraft/util/math/Direction;" + "Lnet/minecraft/fluid/FluidState;"
            + ")V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/WorldAccess;" + "setBlockState("
                    + "Lnet/minecraft/util/math/BlockPos;"
                    + "Lnet/minecraft/block/BlockState;" + "I" + ")Z"))
    public boolean setBlockStateProxy(WorldAccess world, BlockPos pos, BlockState state, int flags) {
        if (FlowstoneConfig.getInstance().isEnabled() && state.getBlock().equals(Blocks.STONE)) {
            var recipes = FlowstoneConfig.getInstance().getRecipes();
            var states = new ArrayList<BlockState>(recipes.size());
            for (var recipe : recipes) {
                if (Registries.BLOCK.containsId(recipe.getBlock())
                        && world.getRandom().nextDouble() <= recipe.getChance()) {
                    states.add(Registries.BLOCK.get(recipe.getBlock()).getDefaultState());
                }
            }
            if (!states.isEmpty()) {
                state = states.get(world.getRandom().nextInt(states.size()));
            }
        }
        return world.setBlockState(pos, state, flags);
    }
}
