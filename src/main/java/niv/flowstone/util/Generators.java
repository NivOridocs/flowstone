package niv.flowstone.util;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.biome.Biome;

public class Generators {

    private static final Set<Generator> COMMON_GENERATORS = new HashSet<>();

    private static final Map<Biome, Set<Generator>> BIOME_SPECIFIC_GENERATORS = new HashMap<>();

    private Generators() {
    }

    public static final synchronized void put(Generator generator) {
        COMMON_GENERATORS.add(requireNonNull(generator));
    }

    public static final synchronized void put(Biome biome, Generator generator) {
        BIOME_SPECIFIC_GENERATORS
                .computeIfAbsent(requireNonNull(biome), key -> new HashSet<>())
                .add(requireNonNull(generator));
    }

    public static final synchronized Set<Generator> get(Biome biome) {
        return Sets.union(COMMON_GENERATORS, BIOME_SPECIFIC_GENERATORS.getOrDefault(biome, Set.of()));
    }

    public static final synchronized void clear() {
        COMMON_GENERATORS.clear();
        BIOME_SPECIFIC_GENERATORS.clear();
    }

}
