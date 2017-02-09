package net.halalaboos.huzuni.api.mod;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.api.settings.ColorNode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.util.render.GLManager;

/**
 * Settings node which is applied to all mods. <br/>
 * Each mod settings node contains a color node and a toggleable node for the displayable option. <br/>
 * The display name of each mod is also held within the mod settings.
 * */
public class ModSettings extends Node {

	private final Mod mod;
	
	private final ColorNode displayColor = new ColorNode("Display Color", Color.WHITE, "The color the mod will be displayed with when enabled");
	
	private final Toggleable displayable = new Toggleable("Displayable", "Will allow the mod to be rendered in-game when enabled");
	
	private String displayName;
	
	public ModSettings(Mod mod) {
		super("settings", "Modify the settings of " + mod.getName());
		this.addChildren(displayable, displayColor);
		displayColor.setColor(GLManager.getRandomColor());
		displayable.setEnabled(true);
		this.mod = mod;
		this.displayName = mod.getName();
	}

	@Override
	public boolean isObject(JsonObject object) {
		return mod.isObject(object);
	}

	@Override
	public void save(JsonObject object) throws IOException {
		object.addProperty("displayName", displayName);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		if (isObject(object)) {
			displayName = object.get("displayName").getAsString();
		}
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public Color getDisplayColor() {
		return displayColor.getColor();
	}
	
	public void setDisplayColor(Color color) {
		this.displayColor.setColor(color);
	}
	
	public boolean isDisplayable() {
		return displayable.isEnabled();
	}

	public void setDisplayable(boolean displayable) {
		this.displayable.setEnabled(displayable);
	}


}
