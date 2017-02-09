package net.halalaboos.huzuni.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

/**
 * Tracks a given entity and increments the player's rotation at a given rate. Used for smoother aiming.
 * */
public class EntityTracker {

	private final Minecraft mc = Minecraft.getMinecraft();
	
	private EntityLivingBase entity;
	
	private float currentYaw, currentPitch, rotationRate = 5F;
	
	private boolean hasReached = false;
	
	public EntityTracker() {
		
	}
	
	/**
	 * Updates the fake rotations and calculates whether or not the rotations have fully rotated to meet the given entity.
	 * */
	public void updateRotations() {
		float[] rotations = MinecraftUtils.getRotationsNeeded(entity);
		float[] rotationCaps = MinecraftUtils.getEntityCaps(entity);
		float yawDifference = MinecraftUtils.getYawDifference(currentYaw, rotations[0]), pitchDifference = rotations[1] - currentPitch;
	    float absoluteYawDifference = Math.abs(yawDifference), absolutePitchDifference = Math.abs(pitchDifference);
		this.hasReached = absoluteYawDifference < rotationCaps[0] && absoluteYawDifference > -rotationCaps[0] && absolutePitchDifference < rotationCaps[1] && absolutePitchDifference > -rotationCaps[1];

		if (this.hasReached) {
			float realYawDifference = MinecraftUtils.getYawDifference(mc.player.rotationYaw % 360F, rotations[0]), realPitchDifference = rotations[1] - (mc.player.rotationPitch % 360F);
			if (realYawDifference < rotationCaps[0] && realYawDifference > -rotationCaps[0])
				currentYaw = mc.player.rotationYaw % 360F;
			if (realPitchDifference < rotationCaps[1] && realPitchDifference > -rotationCaps[1])
				currentPitch = mc.player.rotationPitch % 360F;
		} else {
			if (yawDifference > rotationCaps[0] || yawDifference < -rotationCaps[0]) {
				float yawAdjustment = clamp(yawDifference, rotationRate);
				if (yawAdjustment < 0)
					currentYaw += yawAdjustment;
        		else if (yawAdjustment > 0)
        			currentYaw += yawAdjustment;
			}
			if (pitchDifference > rotationCaps[1] || pitchDifference < -rotationCaps[1]) {
				float pitchAdjustment = clamp(pitchDifference, rotationRate);
				if (pitchAdjustment < 0)
					currentPitch += pitchAdjustment;
        		else if (pitchAdjustment > 0)
        			currentPitch += pitchAdjustment;
			}
		}
	}
	
	/**
	 * Clamps the given input by the max value.
	 * @param input The given input.
	 * @param max The given max.
	 * */
	private float clamp(float input, float max) {
		if (input > max)
			input = max;
		if (input < -max)
			input = -max;
		return input;
	}

	/**
	 * Resets the fake rotation.
	 * */
	public void reset() {
		hasReached = false;
		if (mc.player != null) {
			currentYaw = mc.player.rotationYaw % 360;
			currentPitch =  mc.player.rotationPitch % 360;
		}
	}
	
 	public void setEntity(EntityLivingBase entity) {
 		if (entity != this.entity) {
 			this.entity = entity;
 			reset();
 		}
 	}
 	
 	public boolean hasReached() {
 		return hasReached;
 	}

	public float getYaw() {
		return currentYaw;
	}
	
	public float getPitch() {
		return currentPitch;
	}

	public float getRotationRate() {
		return rotationRate;
	}

	public void setRotationRate(float rotationRate) {
		this.rotationRate = rotationRate;
	}
	
	public boolean hasEntity() {
		return entity != null;
	}
	
}
