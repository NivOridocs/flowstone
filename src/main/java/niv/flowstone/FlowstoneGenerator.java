package niv.flowstone;

import static java.util.Objects.requireNonNull;

import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class FlowstoneGenerator {

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

    public boolean matches(Block block) {
        return replace.equals(block);
    }

    private Stream<Block> compute(RandomSource random) {
        return random.nextDouble() <= this.chance ? Stream.of(with) : Stream.empty();
    }

    public static Optional<Block> findReplace(Block target, Level level) {
        var blocks = level.registryAccess().registryOrThrow(Flowstone.GENERATOR).stream()
                .filter(generator -> generator.matches(target))
                .flatMap(generator -> generator.compute(level.getRandom()))
                .toList();
        return blocks.isEmpty()
                ? Optional.empty()
                : Optional.of(blocks.get(level.getRandom().nextInt(blocks.size())));
    }
}
