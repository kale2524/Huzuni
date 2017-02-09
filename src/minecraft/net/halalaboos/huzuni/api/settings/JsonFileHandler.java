package net.halalaboos.huzuni.api.settings;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import net.halalaboos.huzuni.Huzuni;

/**
 * Allows for simpler and easier file saving using json. This class can utilize the {@link Saveable} objects.
 * */
public abstract class JsonFileHandler {

	protected final Huzuni huzuni;
	
	protected File file;
	
	public JsonFileHandler(Huzuni huzuni, File file) {
		this.huzuni = huzuni;
		this.file = file;
	}
	
	/**
	 * Loads a {@link List} of {@link JsonObject}s from the {@link File} specified.
	 * */
	public void load() {
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				Gson gson = new GsonBuilder().create();
				Type type = new TypeToken<ArrayList<JsonObject>>() {}.getType();
				List<JsonObject> list = gson.fromJson(reader, type);
				if (list != null) {
					for (JsonObject object : list) {
						load(object);
					}
				} else {
					Huzuni.LOGGER.log(Level.WARN, String.format("Unable to load data from file '%s'", file.getAbsolutePath()));
				}
			} catch (Exception e) {
				Huzuni.LOGGER.log(Level.ERROR, String.format("Error reading data from file '%s', e: %s", file.getAbsolutePath(), e.getMessage()));
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {}
			}
		} else
			try {
				file.createNewFile();
			} catch (IOException e) {
				Huzuni.LOGGER.log(Level.ERROR, String.format("Error creating file '%s', e: %s", file.getAbsolutePath(), e.getMessage()));
			}
	}
	
	/**
	 * Saves a {@link List} of {@link JsonObject}s into the {@link File} specified. Invokes the {@link save(List<JsonObject>)} function to allow objects to be added into the list.
	 * */
	public void save() {
		FileWriter writer = null;
		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				Huzuni.LOGGER.log(Level.ERROR, String.format("Error creating file '%s', e: %s", file.getAbsolutePath(), e.getMessage()));
				return;
			}
		try {
			writer = new FileWriter(file);
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			List<JsonObject> list = new ArrayList<JsonObject>();
			save(list);
			gson.toJson(list, writer);
			writer.close();
		} catch (Exception e) {
			Huzuni.LOGGER.log(Level.ERROR, String.format("Error saving to file '%s', e: %s", file.getAbsolutePath(), e.getMessage()));
		} finally {
			if (writer != null)
				try {
					writer.close();
				} catch (IOException e) {}
		}
	}
	
	/**
	 * Invoked to allow this class to initialize itself.
	 * */
	public abstract void init();
	
	/**
	 * Allows for objects to be added into a list for saving into a {@link File}.
	 * @param objects the {@link List} of all {@link JsonObject}s to be saved.
	 * */
	protected abstract void save(List<JsonObject> objects) throws IOException;
	
	/**
	 * Invoked when a {@link JsonObject} is loaded from the {@link File}.
	 * @param object the {@link JsonObject} that is loaded.
	 * */
	protected abstract void load(JsonObject object) throws IOException;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
}
