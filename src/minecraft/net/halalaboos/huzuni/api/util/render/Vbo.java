package net.halalaboos.huzuni.api.util.render;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

/**
 * Basic vertex buffer object.
 * */
public class Vbo {

	protected int id;
	
	protected FloatBuffer vertices = null;
	
	protected int vertexCount = 0, vertexLength = 3;
	
	public Vbo() {
		genId();
	}

	/**
     * Generates the vbo id.
     * */
	public void genId() {
		id = GLManager.genVbo();
	}

	/**
     * Binds the vertex informatino provided into the vbo. Expect vertex length of three (x, y, z).
     * */
	public Vbo create(float[] vertices) {
		return create(vertices, 3);
	}

	/**
     * Binds the vertex information provided into the vbo.
     * */
	public Vbo create(float[] vertices, int vertexLength) {
		this.vertices = BufferUtils.createFloatBuffer(vertices.length);
		for (float vertexPoint : vertices) {
			this.vertices.put(vertexPoint);
		}
		this.vertices.flip();
		this.vertexLength = vertexLength;
		this.vertexCount = vertices.length / vertexLength;
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glBufferData(GL_ARRAY_BUFFER, this.vertices, GL_STATIC_DRAW);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		return this;
	}

	/**
     * Renders the vbo with the mode provided.
     * */
	public void render(int mode) {
		glBindBuffer(GL_ARRAY_BUFFER, id);
		glVertexPointer(vertexLength, GL_FLOAT, 0, 0L);
				
		glEnableClientState(GL_VERTEX_ARRAY);
		glDrawArrays(mode, 0, vertexCount);
        glDisableClientState(GL_VERTEX_ARRAY);
		
        glBindBuffer(GL_ARRAY_BUFFER, 0);
	}

	public int getId() {
		return id;
	}

	public FloatBuffer getVertices() {
		return vertices;
	}

	public void setVertices(FloatBuffer vertices) {
		this.vertices = vertices;
	}

	public void setVertexCount(int vertexCount) {
		this.vertexCount = vertexCount;
	}

	public void setVertexLength(int vertexLength) {
		this.vertexLength = vertexLength;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public int getVertexLength() {
		return vertexLength;
	}
	
}
