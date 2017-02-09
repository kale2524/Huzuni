package net.halalaboos.huzuni.api.util.render;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Utility class to assist in rendering. Uses the {@link Tessellator} and {@link GlStateManager} classes provided by Minecraft.
 * */
public final class RenderUtils {
	
	private static final Tessellator tessellator = Tessellator.getInstance();
	
	private RenderUtils() {
		
	}

	public static void drawTextureRect(float x, float y, float width, float height, float u, float v, float t, float s) {
		VertexBuffer renderer = tessellator.getBuffer();
		renderer.begin(GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX);
		renderer.pos(x + width, y, 0F).tex(t, v).endVertex();
		renderer.pos(x, y, 0F).tex(u, v).endVertex();
		renderer.pos(x, y + height, 0F).tex(u, s).endVertex();
		renderer.pos(x, y + height, 0F).tex(u, s).endVertex();
		renderer.pos(x + width, y + height, 0F).tex(t, s).endVertex();
		renderer.pos(x + width, y, 0F).tex(t, v).endVertex();
		tessellator.draw();
	}

	/**
	 * Renders a simple lined border around the given x, y, x1, and y1 coordinates. 
	 * */
	public static void drawBorder(float size, float x, float y, float x1, float y1) {
		glLineWidth(size);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
    	VertexBuffer vertexBuffer = tessellator.getBuffer();
    	vertexBuffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);
    	vertexBuffer.pos(x, y, 0F).endVertex();
    	vertexBuffer.pos(x, y1, 0F).endVertex();
    	vertexBuffer.pos(x1, y1, 0F).endVertex();
    	vertexBuffer.pos(x1, y, 0F).endVertex();
    	tessellator.draw();
		GlStateManager.enableTexture2D();
	}
	
	/**
	 * Renders a line from the given x, y positions to the second x1, y1 positions.
	 * */
	public static void drawLine(float size, float x, float y, float x1, float y1) {
		glLineWidth(size);
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
    	VertexBuffer vertexBuffer = tessellator.getBuffer();
    	vertexBuffer.begin(GL_LINES, DefaultVertexFormats.POSITION);
    	vertexBuffer.pos(x, y, 0F).endVertex();
    	vertexBuffer.pos(x1, y1, 0F).endVertex();
    	tessellator.draw();
    	GlStateManager.enableTexture2D();
	}
	
	/**
	 * Renders a simple rectangle around the given x, y, x1, and y1 coordinates.
	 * */
	public static void drawRect(float x, float y, float x1, float y1) {
		GlStateManager.disableTexture2D();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
    	VertexBuffer vertexBuffer = tessellator.getBuffer();
    	vertexBuffer.begin(GL_QUADS, DefaultVertexFormats.POSITION);
    	vertexBuffer.pos(x, y1, 0F).endVertex();
    	vertexBuffer.pos(x1, y1, 0F).endVertex();
    	vertexBuffer.pos(x1, y, 0F).endVertex();
    	vertexBuffer.pos(x, y, 0F).endVertex();
    	tessellator.draw();
    	GlStateManager.enableTexture2D();
	}
	
	/**
	 * Renders a simple rectangle and line border around the given x, y, x1, and y1 coordinates.
	 * */
	public static void drawBorderRect(float x, float y, float x1, float y1, float borderSize) {
		drawBorder(borderSize, x, y, x1, y1);
		drawRect(x, y, x1, y1);
	}
	
	/**
	 * Translates and scales to allow for billboarding.
	 * <br>
	 * @see <a href= http://www.opengl-tutorial.org/intermediate-tutorials/billboards-particles/billboards/>Billboards</a>
	 * 
	 * */
	public static void prepareBillboarding(float x, float y, float z, boolean modifyPitch) {
		float scale = 0.016666668F * 1.6F;
		glTranslatef((float) x, (float) y, (float) z);
		glNormal3f(0.0F, 1.0F, 0.0F);
		glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
		if (modifyPitch) {
			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
				glRotatef(-Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			} else {
				glRotatef(Minecraft.getMinecraft().getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
			}
		}
		glScalef(-scale, -scale, scale);
	}
	
	/**
	 * @return A {@link Color} object with effects applied to it based on the boolean values given.
	 * */
	public static Color getColorWithAffects(Color color, boolean mouseOver, boolean mouseDown) {
    	return mouseOver ? (mouseDown ? color.darker() : color.brighter()) : color;
    }

	/**
	 * @return A hexadecimal color with effects applied to it based on the boolean values given.
	 * */
	public static int getColorWithAffects(int color, boolean mouseOver, boolean mouseDown) {
    	return mouseOver ? (mouseDown ? darker(color, 0.2F) : brighter(color, 0.2F)) : color;
    }

    /**
     * @return A hexadecimal color that is darkened by the scale amount provided.
     */
	public static int darker(int color, float scale) {
		int red = (color >> 16 & 255), green =  (color >> 8 & 255), blue = (color & 255), alpha = (color >> 24 & 0xff);
		red = (int) (red - red * scale);
		red = Math.min(red, 255);
		green = (int) (green - green * scale);
		green = Math.min(green, 255);
		blue = (int) (blue - blue * scale);
		blue = Math.min(blue, 255);
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
     * @return A hexadecimal color that is brightened by the scale amount provided.
     * */
	public static int brighter(int color, float scale) {
		int red = (color >> 16 & 255), green =  (color >> 8 & 255), blue = (color & 255), alpha = (color >> 24 & 0xff);
		red = (int) (red + red * scale);
		red = Math.min(red, 255);
		green = (int) (green + green * scale);
		green = Math.min(green, 255);
		blue = (int) (blue + blue * scale);
		blue = Math.min(blue, 255);
		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
     * @return A hexadecimal representation of the rgba color values specified (between 0 - 1)
     * */
	public static int toColor(float r, float g, float b, float a) {
		return ((int) (a * 255F) << 24) | ((int) (r * 255F) << 16) | ((int) (g * 255F) << 8) | (int) (b * 255F);
	}
}
