package niv.flowstone.api;

import java.util.Optional;
import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface Generator extends BiFunction<LevelAccessor, BlockPos, Optional<BlockState>> {
}
