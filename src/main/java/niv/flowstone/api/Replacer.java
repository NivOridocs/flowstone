package niv.flowstone.api;

import java.util.Optional;

import org.apache.commons.lang3.function.TriFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface Replacer extends TriFunction<LevelAccessor, BlockPos, BlockState, Optional<BlockState>> {
}
