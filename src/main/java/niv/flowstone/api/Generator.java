package niv.flowstone.api;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface Generator extends
        BiPredicate<RandomSource, BlockState>,
        BiFunction<LevelAccessor, BlockPos, Stream<BlockState>> {
}
