package niv.flowstone;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class FlowstoneGenerator implements Predicate<BlockState>, BiFunction<LevelAccessor, BlockPos, Optional<BlockState>> {

    public static final Codec<FlowstoneGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("replace").forGetter(r -> r.replace),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("with").forGetter(r -> r.with),
            Codec.doubleRange(0d, 1d).fieldOf("chance").forGetter(r -> r.chance))
            .apply(instance, FlowstoneGenerator::new));

    private final Block replace;

    private final Block with;

    private final double chance;

    public FlowstoneGenerator(Block replace, Block with, double chance) {
        this.replace = requireNonNull(replace);
        this.with = requireNonNull(with);
        this.chance = chance;
    }

    @Override
    public boolean test(BlockState state) {
        return state.is(replace);
    }

    @Override
    public Optional<BlockState> apply(LevelAccessor level, BlockPos pos) {
        return level.getRandom().nextDouble() <= this.chance ? Optional.of(with.defaultBlockState()) : Optional.empty();
    }

    public static final Set<FlowstoneGenerator> getFlowstoneStoneGenerators(LevelAccessor level) {
        return level.registryAccess().registry(Flowstone.GENERATOR).stream()
                .flatMap(Registry::stream)
                .filter(generator -> generator.test(Blocks.STONE.defaultBlockState()))
                .collect(toSet());
    }

    public static final Set<FlowstoneGenerator> getFlowstoneStoneGenerators(LevelAccessor level, BlockState state) {
        return level.registryAccess().registry(Flowstone.GENERATOR).stream()
                .flatMap(Registry::stream)
                .filter(generator -> generator.test(state))
                .collect(toSet());
    }
}
