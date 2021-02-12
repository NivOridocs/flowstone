package niv.flowstone.util;

import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;

public interface FlowstoneGenerator {

	boolean isValidPos(WorldAccess world, BlockPos pos);

	Optional<BlockState> generateOre(WorldAccess world, int enhancer);

}
