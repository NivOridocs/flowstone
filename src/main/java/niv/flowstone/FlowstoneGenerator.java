package niv.flowstone;

import static java.util.Objects.requireNonNull;

import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import niv.flowstone.api.Generator;

public class FlowstoneGenerator implements Generator {

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
    public boolean test(RandomSource random, BlockState state) {
        return state.is(replace);
    }

    @Override
    public Stream<BlockState> apply(LevelAccessor level, BlockPos pos) {
        return level.getRandom().nextDouble() <= this.chance ? Stream.of(with.defaultBlockState()) : Stream.empty();
    }

    public static Stream<FlowstoneGenerator> all(LevelAccessor level) {
        return level.registryAccess().registry(Flowstone.GENERATOR).stream()
                .flatMap(Registry::stream);
    }
}
