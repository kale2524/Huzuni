package net.halalaboos.huzuni.api.util.render;

import static org.lwjgl.opengl.GL11.*;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Brudin took my simple box vbo and added a bunch of lines to it. I hate it, but it's okay.
 * @author brudin
 * @version 1.0
 * @since 4/10/14
 */
public class Box extends Vbo {

	private boolean opaque = false;

	public Box(AxisAlignedBB boundingBox, boolean opaque) {
		super();
		this.opaque = opaque;
		setup(boundingBox);
	}

	public Box(AxisAlignedBB boundingBox) {
		this(boundingBox, true);
	}

	public void setup(AxisAlignedBB boundingBox) {
		float minX = (float) boundingBox.minX, minY = (float) boundingBox.minY, minZ = (float) boundingBox.minZ;
		float maxX = (float) boundingBox.maxX, maxY = (float) boundingBox.maxY, maxZ = (float) boundingBox.maxZ;
		float[] vertices = new float[] { minX, minY, maxZ, maxX, minY, maxZ,
				maxX, maxY, maxZ, minX, maxY, maxZ, maxX, minY, maxZ, minX,
				minY, maxZ, minX, maxY, maxZ, maxX, maxY, maxZ, minX, minY,
				minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ,
				minX, minY, maxZ, minX, minY, minZ, minX, maxY, minZ, minX,
				maxY, maxZ, maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY,
				minZ, maxX, maxY, maxZ, maxX, minY, minZ, maxX, minY, maxZ,
				maxX, maxY, maxZ, maxX, maxY, minZ, minX, minY, minZ, maxX,
				minY, minZ, maxX, maxY, minZ, minX, maxY, minZ, maxX, minY,
				minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ,
				minX, maxY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ, minX,
				maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ, minX, maxY,
				maxZ, maxX, maxY, maxZ, minX, minY, minZ, maxX, minY, minZ,
				maxX, minY, maxZ, minX, minY, maxZ, maxX, minY, minZ, minX,
				minY, minZ, minX, minY, maxZ, maxX, minY, maxZ };
		this.create(vertices);
	}

	public void render() {
		this.render(opaque ? GL_QUADS : GL_LINE_STRIP);
	}

	public boolean isOpaque() {
		return opaque;
	}

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}
}
