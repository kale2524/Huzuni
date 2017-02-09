package net.halalaboos.huzuni.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Finds the closest block within a radius from the player position, starting with the blocks the player is facing.
 * */
public abstract class BlockLocator {

	private final Minecraft mc = Minecraft.getMinecraft();
	
	private boolean distanceCheck = true;
	
	private BlockPos position;
	
	private EnumFacing face;
	
	/**
	 * @return True if a block could be located within the radius.
	 * @param radius The radius around the player to check for blocks.
	 * @param ignoreFace When true, the {@link EnumFacing} given from the {@link getFace(EnumFacing)} function will be not be checked if null.
	 * */
	public boolean locateClosest(float radius, boolean ignoreFace) {
		BlockPos closestPosition = null;
		EnumFacing closestFace = null;
		double closestDistance = 0;
		Vec3i directionVector = mc.player.getHorizontalFacing().getDirectionVec();
		int xIncrement = directionVector.getX() != 0 ? directionVector.getX() : 1;
		int zIncrement = directionVector.getZ() != 0 ? directionVector.getZ() : 1;
		
		for (double i = -(radius * xIncrement); check(i, (radius * xIncrement), directionVector.getX() < 0); i += xIncrement) {
			for (double j = -radius; j < radius; j++) {
				for (double k = -(radius * zIncrement); check(k, (radius * zIncrement), directionVector.getZ() < 0); k += zIncrement) {
					if (i == 0 && j >= -1 && j <= mc.player.getEyeHeight() && k == 0)
						continue;
					BlockPos position = new BlockPos(mc.player.posX + i, mc.player.posY + j, mc.player.posZ + k);
					double distance = MathUtils.getDistance(position);
					if (isWithinDistance(distance) && isValidBlock(position)) {
						EnumFacing face = getFace(position);
						if (face != null || ignoreFace) {
							if (closestPosition != null) {
								if (distance < closestDistance) {
									closestPosition = position;
									closestFace = face;
									closestDistance = distance;
								}
							} else {
								closestPosition = position;
								closestFace = face;
								closestDistance = distance;
							}
						}
					}
				}
			}
		}
		this.position = closestPosition;
		this.face = closestFace;
		return closestPosition != null;
	}
	
	/**
	 * @return True if the absolute value of {@code index} is within the absolute value of {@code max}.
	 * @param index The double
	 * @param max
	 * @param flip
	 * */
	private boolean check(double index, double max, boolean flip) {
		return flip ? index > max : index < max;
	}
	
	/**
	 * @return True if the {@link Block} located at the {@link BlockPos} is considered 'valid'.
	 * */
	protected abstract boolean isValidBlock(BlockPos position);
	
	/**
	 * @return The {@link EnumFacing} needed for the {@link BlockPos}
	 * */
	protected abstract EnumFacing getFace(BlockPos position);
	
	/**
	 * @return True if the {@code distance} is less than the block reach distance.
	 * */
	protected boolean isWithinDistance(double distance) {
		return distanceCheck ? distance < mc.playerController.getBlockReachDistance() : true;
	}
	
	/**
	 * Resets the position and face found with the block locator
	 * */
	public void reset() {
		position = null;
		face = null;
	}
	
	public boolean isDistanceCheck() {
		return distanceCheck;
	}

	public void setDistanceCheck(boolean distanceCheck) {
		this.distanceCheck = distanceCheck;
	}

	public BlockPos getPosition() {
		return position;
	}

	public EnumFacing getFace() {
		return face;
	}

	/**
	 * @return True if the locator has a position and face
	 * */
	public boolean hasPosition() {
		return position != null && face != null;
	}
}
