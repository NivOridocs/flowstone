package niv.flowstone.util;

import static java.util.Objects.requireNonNull;
import static niv.flowstone.FlowstoneMod.MOD_NAME;
import static niv.flowstone.FlowstoneMod.log;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.include.com.google.common.base.Objects;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

public class Generator {
    private static final Set<Generator> ALL = new HashSet<>();

    private static final int CHUNK = 16 * 16;

    private final int zero;
    private final int low;
    private final int medium;
    private final int high;
    private final int bound;

    private final BlockState state;
    private final int volume;
    private final int density;

    private Generator(BlockState state, int minY, int maxY, int density) {
        this.state = state;
        this.zero = minY;
        this.high = maxY - minY;

        this.low = 0;
        this.medium = high;
        this.bound = 1;

        this.volume = this.medium * CHUNK;
        this.density = density;
    }

    private Generator(BlockState state, int minY, int maxY, int plateau, int density) {
        this.state = state;
        this.zero = minY;
        this.high = maxY - minY;

        plateau = Math.min(plateau, high);
        plateau = Math.max(0, plateau);

        this.low = (high - plateau) / 2;
        this.medium = high - low;
        this.bound = low + 1;

        this.volume = this.medium * CHUNK;
        this.density = density;
    }

    public boolean isValidPos(WorldAccess world, BlockPos pos) {
        int y = pos.getY() - zero;
        if (y >= 0 && y < low)
            return world.getRandom().nextInt(bound) < (y + 1);
        else if (y >= low && y <= medium)
            return true;
        else if (y > medium && y <= high)
            return world.getRandom().nextInt(bound) < (high - y + 1);
        else
            return false;
    }

    public Optional<BlockState> generateOre(WorldAccess world, int enhancer) {
        if (world.getRandom().nextInt(volume + (density * enhancer)) < (density * (1 + enhancer)))
            return Optional.of(state);
        else
            return Optional.empty();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + bound;
        result = prime * result + density;
        result = prime * result + high;
        result = prime * result + low;
        result = prime * result + medium;
        result = prime * result + ((state == null) ? 0 : state.hashCode());
        result = prime * result + volume;
        result = prime * result + zero;
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;

        if (object != null && object instanceof Generator that)
            return Objects.equal(this.state, that.state)
                && this.bound == that.bound
                && this.density == that.density
                && this.high == that.high
                && this.low == that.low
                && this.state == that.state
                && this.volume == that.volume
                && this.zero == that.zero;

        return false;
    }

    public static final Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BlockState state;
        private Integer minY;
        private Integer maxY;
        private Integer plateau;
        private Integer size;
        private Integer count;

        private Builder() {
        }

        public Builder state(BlockState state) {
            this.state = requireNonNull(state);
            return this;
        }

        public Builder minY(int minY) {
            this.minY = minY;
            return this;
        }

        public Builder maxY(int maxY) {
            this.maxY = maxY;
            return this;
        }

        public Builder plateau(int plateau) {
            this.plateau = plateau;
            return this;
        }

        public Builder size(int size) {
            this.size = size;
            return this;
        }

        public Builder count(int count) {
            this.count = count;
            return this;
        }

        public Generator build() {
            Generator generator;

            if (state == null || minY == null || maxY == null || count == null || size == null) {
                return null;
            } else if (plateau == null) {
                generator = new Generator(state, minY, maxY, count * size);
            } else {
                generator = new Generator(state, minY, maxY, plateau, count * size);
            }

            if (ALL.add(generator)) {
                log.info("[{}] Build new {} generator", () -> MOD_NAME, () -> Registry.BLOCK.getId(state.getBlock()));
                return generator;
            } else {
                return ALL.stream().filter(generator::equals).findAny().orElseThrow();
            }
        }

    }

}
