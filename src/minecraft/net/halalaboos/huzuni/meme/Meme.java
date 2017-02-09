package net.halalaboos.huzuni.meme;

import org.lwjgl.opengl.GL11;

import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.GL_NEAREST;
import static org.lwjgl.opengl.GL11.GL_REPEAT;

/**
 * Used to hold meme information.
 * */
public class Meme {

    private final String url;

    private final BufferedImage image;

	private int texId = -1;
		
	public Meme(String url, BufferedImage image) {
        this.url = url;
		this.image = image;
	}

    /**
     * Binds the meme texture.
     * */
    public void bindTexture() {
        if (!hasTexture())
            generateTexture();
        GlStateManager.bindTexture(texId);
    }

	/**
     * Generates a texture id and applies the loaded buffered image to the texture.
     * */
	private void generateTexture() {
        texId = GLManager.applyTexture(GLManager.genTexture(), image, GL_NEAREST, GL_REPEAT);
    }

    /**
     * @return True if the meme has a texture.
     * */
    public boolean hasTexture() {
        return texId != -1;
    }

    /**
     * Deletes the texture from memory.
     * */
	public void releaseTexture() {
		GL11.glDeleteTextures(texId);
        texId = -1;
	}


    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

    public int getTexId() {
        return texId;
    }

    public String getUrl() {
        return url;
    }
}
