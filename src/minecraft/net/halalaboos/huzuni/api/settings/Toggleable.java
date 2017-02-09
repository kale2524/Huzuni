package net.halalaboos.huzuni.api.settings;

import java.io.IOException;

import com.google.gson.JsonObject;

/**
 * A {@link Node} object that can be set enabled/disabled (true/false).
 * */
public class Toggleable extends Node {
		
	private boolean enabled;
	
	public Toggleable(String name, String description) {
		super(name, description);
	}
	
	@Override
	public void save(JsonObject object) throws IOException {
		super.save(object);
		object.addProperty(getName(), enabled);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		super.load(object);
		if (isObject(object)) {
			enabled = object.get(getName()).getAsBoolean();
		}
	}
	
	/**
	 * Toggles the value of this {@link Node} object.
	 * */
	public void toggle() {
		setEnabled(!enabled);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
