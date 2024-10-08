package niv.flowstone.impl;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;
import static niv.flowstone.config.Configuration.debugMode;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import niv.flowstone.Flowstone;
import niv.flowstone.api.Generator;
import niv.flowstone.api.Replacer;

public class CustomGenerator implements Predicate<BlockState>, Generator {

    public static final Codec<CustomGenerator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("replace").forGetter(r -> r.replace),
            BuiltInRegistries.BLOCK.byNameCodec().fieldOf("with").forGetter(r -> r.with),
            Codec.doubleRange(0d, 1d).fieldOf("chance").forGetter(r -> r.chance))
            .apply(instance, CustomGenerator::new));

    public static final ResourceKey<Registry<CustomGenerator>> REGISTRY = ResourceKey
            .createRegistryKey(ResourceLocation.tryBuild(Flowstone.MOD_ID, "generators"));

    private final Block replace;

    private final Block with;

    private final double chance;

    public CustomGenerator(Block replace, Block with, double chance) {
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
        return Optional.of(this.with.defaultBlockState()).filter(value -> test(level.getRandom()));
    }

    private boolean test(RandomSource random) {
        return debugMode() ||  random.nextDouble() <= this.chance;
    }

    private static final BlockState applyAll(LevelAccessor level, BlockPos pos, BlockState state) {
        return Generator.applyAll(getGenerators(level, state), level, pos).orElse(state);
    }

    private static final Set<Generator> getGenerators(LevelAccessor level, BlockState state) {
        return level.registryAccess().registry(REGISTRY).stream()
                .flatMap(Registry::stream)
                .filter(generator -> generator.test(state))
                .collect(toSet());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("replace", this.replace)
                .add("with", this.with)
                .add("chance", this.chance)
                .toString();
    }

    public static final Replacer getReplacer() {
        return CustomGenerator::applyAll;
    }
}
