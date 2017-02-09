package net.halalaboos.huzuni.mod.mining.templates;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Basic template which can be used to build static shapes from an origin.
 * */
public class BasicTemplate implements Template {

	private final String name;

	private final int[] vertices;

	public BasicTemplate(String name, int[] vertices) {
		this.name = name;
		this.vertices = vertices;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return "Build a " + name;
	}

	@Override
	public int getMaxPoints() {
		return 1;
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
		int xOffset = face.rotateY().getDirectionVec().getX();
		int zOffset = face.rotateY().getDirectionVec().getZ();
		BlockPos origin = positions[0];
		for (int i = 0; i < vertices.length; i += 2) {
			outputPositions.add(origin.add(xOffset * vertices[i], vertices[i + 1], zOffset * vertices[i]));
		}
	}

	@Override
	public String toString() {
		return name;
	}

	/**
     * Loads a basic template from file.
     * */
	public static BasicTemplate readTemplate(File file) throws Exception {
		Gson gson = new GsonBuilder().create();
		JsonObject object = gson.fromJson(new FileReader(file), JsonObject.class);
		String name = object.get("name").getAsString();
		JsonArray jsonArray = object.get("vertices").getAsJsonArray();
		int[] vertices = new int[jsonArray.size()];
		int index = 0;
		for (JsonElement element : jsonArray) {
			vertices[index] = element.getAsInt();
			index++;
		}
		return new BasicTemplate(name, vertices);
	}
}