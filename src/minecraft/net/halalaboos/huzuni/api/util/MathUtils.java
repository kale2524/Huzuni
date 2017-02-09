package net.halalaboos.huzuni.api.util;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

/**
 * Math utilities
 * */
public final class MathUtils {

	private MathUtils() {
		
	}
	
	public static double getDistance(BlockPos position) {
        return getDistance(position.getX(), position.getY(), position.getZ());
	}
	
	public static double getInterpolatedDistance(EntityPlayer entityPlayer, double delta) {
        return getDistance(interpolate(entityPlayer.prevPosX, entityPlayer.posX, delta), interpolate(entityPlayer.prevPosY, entityPlayer.posY, delta), interpolate(entityPlayer.prevPosZ, entityPlayer.posZ, delta));
	}
	
	public static double getDistance(double x, double y, double z) {
		double distX = Minecraft.getMinecraft().getRenderManager().viewerPosX - x;
		double distY = Minecraft.getMinecraft().getRenderManager().viewerPosY - y;
		double distZ = Minecraft.getMinecraft().getRenderManager().viewerPosZ - z;
        return MathHelper.sqrt(distX * distX + distY * distY + distZ * distZ);
	}
	
	public static double interpolate(double prev, double cur, double delta) {
		return prev + ((cur - prev) * delta);
	}

    /**
     * Clamps the given input between the maximum and -maximum
     */
	public static float clamp(float input, float max) {
        return clamp(input, max, -max);
    }

    /**
     * Clamps the input between the maximum and minimum values.
     * */
    public static float clamp(float input, float max, float min) {
        if (input > max)
            input = max;
        if (input < min)
            input = min;
        return input;
    }

    /**
     * Clamps the input between the maximum and minimum values.
     * */
    public static int clamp(int input, int max, int min) {
        if (input > max)
            input = max;
        if (input < min)
            input = min;
        return input;
    }

    /**
     * Clamps the input between the maximum and minimum values.
     * */
    public static double clamp(double input, double max, double min) {
        if (input > max)
            input = max;
        if (input < min)
            input = min;
        return input;
    }
}
