package niv.flowstone.api;

import java.util.Collection;
import java.util.Optional;
import java.util.function.BiFunction;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

public interface Generator extends BiFunction<LevelAccessor, BlockPos, Optional<BlockState>> {
    public static Optional<BlockState> applyAll(
            Collection<? extends Generator> generators, LevelAccessor level, BlockPos pos) {
        var states = generators.stream()
                .map(generator -> generator.apply(level, pos))
                .flatMap(Optional::stream).toList();
        return states.isEmpty() ? Optional.empty()
                : Optional.of(states.get(level.getRandom().nextInt(states.size())));
    }
}
