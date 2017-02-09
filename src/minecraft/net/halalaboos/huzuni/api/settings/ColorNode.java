package net.halalaboos.huzuni.api.settings;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.JsonObject;

/**
 * Node which contains a color object.
 * */
public class ColorNode extends Node {
	
	private Color color;
	
	public ColorNode(String name, Color color, String description) {
		super(name, description);
		this.color = color;
	}
	
	@Override
	public void save(JsonObject object) throws IOException {
		super.save(object);
		JsonObject color = new JsonObject();
		color.addProperty("r", this.color.getRed());
		color.addProperty("g", this.color.getGreen());
		color.addProperty("b", this.color.getBlue());
		color.addProperty("a", this.color.getAlpha());
		object.add(getName(), color);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		super.load(object);
		if (isObject(object)) {
			JsonObject colorObject = object.getAsJsonObject(getName());
			if (colorObject != null) {
				int red = colorObject.get("r").getAsInt(), green = colorObject.get("g").getAsInt(), blue = colorObject.get("b").getAsInt(), alpha = colorObject.get("a").getAsInt();
				color = new Color(red, green, blue, alpha);
			}
		}
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
