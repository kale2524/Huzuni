package net.halalaboos.huzuni.mod.mining.templates;

import java.util.List;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Cylindrical template which uses the ellipse formula to gaurantee a cylindrical shape.
 * */
public class CylinderTemplate implements Template {

	@Override
	public String getName() {
		return "Cylinder";
	}

	@Override
	public String getDescription() {
		return "Build a cylinder";
	}

	@Override
	public int getMaxPoints() {
		return 3;
	}

	@Override
	public String getPointName(int point) {
		return "Point " + (point + 1);
	}
	
	@Override
	public boolean insideBlock(BlockPos position) {
		return false;
	}

	@Override
	public void generate(List<BlockPos> outputPositions, EnumFacing face, BlockPos... positions) {
        BlockPos yPosition = positions.length < 3 ? positions[0] : positions[2];
		if (positions[0].equals(positions[1]))
			return;
		boolean xLeft = positions[0].getX() <= positions[1].getX(), zLeft = positions[0].getZ() <= positions[1].getZ();
		double originX = xLeft ? positions[0].getX() : positions[1].getX();
		double originZ = zLeft ? positions[0].getZ() : positions[1].getZ();
		double centerX = Math.ceil((xLeft ? ((double) positions[1].getX() - originX) : (double) positions[0].getX() - originX) / 2D);
		double centerZ = Math.ceil((zLeft ? ((double) positions[1].getZ() - originZ) : (double) positions[0].getZ() - originZ) / 2D);

		int incrementX = positions[0].getX() <= positions[1].getX() ? 1 : -1;
		int incrementY = positions[0].getY() <= yPosition.getY() ? 1 : -1;
		int incrementZ = positions[0].getZ() <= positions[1].getZ() ? 1 : -1;
		for (int i = positions[0].getX(); check(i, positions[1].getX(), positions[0].getX() > positions[1].getX()); i += incrementX) {
			for (int j = positions[0].getY(); check(j, yPosition.getY(), positions[0].getY() > yPosition.getY()); j += incrementY) {
				for (int k = positions[0].getZ(); check(k, positions[1].getZ(), positions[0].getZ() > positions[1].getZ()); k += incrementZ) {
					if (isEllipse((i - originX) - centerX,  (k - originZ) - centerZ, (int) centerX, (int) centerZ))
						outputPositions.add(new BlockPos(i, j, k));
				}
			}
		}
	}

	/**
     * @return True if the position passes the 'ellipse test'.
     * @see <a href="https://en.wikipedia.org/wiki/Ellipse#Equations">Ellipse formula</a>
     * */
	private boolean isEllipse(double x, double y, double width, double height) {
		double result = ((x * x) / (width * width)) + ((y * y) / (height * height));
		return (int) Math.ceil(result) <= 1;
	}

	@Override
	public String toString() {
		return "Cylinder";
	}

	/**
     * Compares the min vs the max and flips the comparison when necessary.
     * */
	private boolean check(int min, int max, boolean flip) {
		return flip ? min >= max : min <= max;
	}
}
