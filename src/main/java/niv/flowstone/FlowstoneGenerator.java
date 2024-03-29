package niv.flowstone;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FlowstoneGenerator implements Predicate<Block> {

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
    public boolean test(Block block) {
        return replace.equals(block);
    }

    private Stream<Block> compute(RandomSource random) {
        return random.nextDouble() <= this.chance ? Stream.of(with) : Stream.empty();
    }

    public static BlockState replace(LevelAccessor accessor, BlockState state) {
        if (accessor instanceof Level level) {
            var blocks = level.registryAccess().registry(Flowstone.GENERATOR)
                    .map(Registry::stream).orElseGet(Stream::empty)
                    .filter(gen -> gen.test(state.getBlock()))
                    .flatMap(gen -> gen.compute(level.getRandom()))
                    .toList();
            return blocks.isEmpty() ? state
                    : blocks.get(level.getRandom().nextInt(blocks.size())).defaultBlockState();
        }
        return state;
    }
}
