package net.halalaboos.huzuni.api.mod;

import java.awt.Color;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.settings.Node;
import net.minecraft.client.Minecraft;

/**
 * Node which is used for most features within the client. <br/>
 * Mods are toggleable nodes that can be categorized and have their own settings. <br/>
 * Each mod has an id that is assigned upon it's instantiation, meaning it is not always consistent.
 * */
public class Mod extends Node {
	
	protected static int idCount = 60;

	protected final Minecraft mc = Minecraft.getMinecraft();
	
	protected final Huzuni huzuni = Huzuni.INSTANCE;
	
	protected boolean enabled = false;

	protected Category category = Category.NONE;
	
	public final ModSettings settings = new ModSettings(this);

	public final int id;
			
	public Mod(String name, String description) {
		super(name, description);
		this.addChildren(settings);
		this.id = idCount;
		idCount++;
	}

	/**
     * Invoked when the mod is toggled.
     * */
	protected void onToggle() {}

    /**
     * Invoked when the mod is set enabled.
     * */
	protected void onEnable() {}

    /**
     * Invoked when the mod is set disabled.
     * */
	protected void onDisable() {}

    /**
     * Toggles the mod.
     * */
	public void toggle() {
		this.setEnabled(!enabled);
        onToggle();
    }
	
	public Category getCategory() {
		return category;
	}

	protected void setCategory(Category category) {
		this.category = category;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		if (this.enabled != enabled) {
            this.enabled = enabled;
			if (enabled)
				onEnable();
			else
				onDisable();
		}
	}

	public String getDisplayNameForRender() {
		return settings.getDisplayName();
	}

	@Override
	public boolean isObject(JsonObject object) {
		JsonElement name = object.get("name");
		return name == null ? false : name.getAsString().equals(getName());
	}

	@Override
	public void load(JsonObject object) throws IOException {
		super.load(object);
		if (isObject(object)) {
			setEnabled(object.get("enabled").getAsBoolean());
		}
	}

	@Override
	public void save(JsonObject object) throws IOException {
		super.save(object);
		object.addProperty("name", getName());
		object.addProperty("enabled", isEnabled());
	}
	
}
