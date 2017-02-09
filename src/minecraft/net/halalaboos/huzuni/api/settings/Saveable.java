package net.halalaboos.huzuni.api.settings;

import java.io.IOException;

import com.google.gson.JsonObject;

/**
 * Simple interface allowing for easier saving/loading of information.
 * */
public interface Saveable {

	/**
	 * Attempts to save this {@link Saveable} object onto a {@link JsonObject}.
	 * @param object The {@link JsonObject} to be saved into.
	 * */
	void save(JsonObject object) throws IOException;
	
	/**
	 * Attempts to load this {@link Saveable} object from {@link JsonObject}.
	 * @param object The {@link JsonObject} to be loaded from.
	 * */
	void load(JsonObject object) throws IOException;
	
}
