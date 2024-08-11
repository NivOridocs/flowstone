package niv.flowstone;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import niv.flowstone.api.Replacer;

public class Replacers {

    private Replacers() {
    }

    private static record AllowedBlocksEmptyingReplacer(ImmutableList<Block> allowed) implements Replacer {
        @Override
        public Optional<BlockState> apply(LevelAccessor level, BlockPos pos, BlockState state) {
            return Optional.of(state).filter(value -> allowed.stream().anyMatch(value::is));
        }
    }

    public static final Replacer allowedBlocksEmptyingReplacer(Collection<Block> blocks) {
        return new AllowedBlocksEmptyingReplacer(ImmutableList.copyOf(blocks));
    }

    public static final Replacer allowedBlocksEmptyingReplacer(Block... blocks) {
        return new AllowedBlocksEmptyingReplacer(ImmutableList.copyOf(blocks));
    }

    private static record DefaultedMultiReplacer(ImmutableList<Replacer> replacers) implements Replacer {
        @Override
        public Optional<BlockState> apply(LevelAccessor level, BlockPos pos, BlockState state) {
            var result = Optional.of(state);
            for (var replacer : replacers) {
                result = result.flatMap(value -> replacer.apply(level, pos, value));
            }
            return result.or(() -> Optional.of(state));
        }
    }

    public static final Replacer defaultedMultiReplacer(List<Replacer> replacers) {
        return new DefaultedMultiReplacer(ImmutableList.copyOf(replacers));
    }

    public static final Replacer defaultedMultiReplacer(Replacer... replacers) {
        return new DefaultedMultiReplacer(ImmutableList.copyOf(replacers));
    }
}
