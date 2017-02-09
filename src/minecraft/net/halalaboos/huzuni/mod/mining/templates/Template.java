package net.halalaboos.huzuni.mod.mining.templates;

import java.util.List;

import net.halalaboos.huzuni.api.settings.Nameable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Used to automate the placement of blocks. Stores a template of block placements.
 * */
public interface Template extends Nameable {

    /**
     * @return The maximum amount of points this template needs to be able to generate it's shape/structure.
     * */
	int getMaxPoints();

    /**
     * @return The name of this point.
     * */
	String getPointName(int point);

    /**
     * @return True if this position does not need to be offset by the face value (i.e. the block placement packet will use the actual block position rather than what is expected from the client.)
     * */
	boolean insideBlock(BlockPos position);
	
//	boolean isValidPlacement(EnumFacing face, BlockPos position, int point);

    /**
     * Generates and appends block positions to the given output.
     * */
	void generate(List<BlockPos> outputPositions, EnumFacing face, BlockPos... positions);
	
}
