package niv.flowstone.util;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public class SimpleFlowstonGenerator implements FlowstoneGenerator {
	private final static int CHUNK = 16 * 16;

	private final BlockState state;
	private final int minY;
	private final int maxY;
	private final int volume;
	private final int density;

	public SimpleFlowstonGenerator(BlockState state, int minY, int maxY, int density) {
		this.state = state;
		this.minY = minY;
		this.maxY = maxY;
		this.density = density;

		this.volume = (this.maxY - this.minY) * CHUNK;
	}

	@Override
	public boolean isValidPos(WorldAccess world, BlockPos pos) {
		return pos.getY() >= minY && pos.getY() <= maxY;
	}

	@Override
	public Optional<BlockState> generateOre(WorldAccess world, int enhancer) {
		if (world.getRandom().nextInt(volume + (density * enhancer)) < (density * (1 + enhancer)))
			return Optional.of(state);
		else
			return Optional.empty();
	}

}
