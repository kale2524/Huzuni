package net.halalaboos.huzuni.api.plugin;

import java.io.IOException;
import java.util.List;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.settings.JsonFileHandler;

/**
 * Plugins are objects that can be used to add more functionality and features to the mod. <br/>
 * They are loaded and handled within the plugin manager and the plugin data is loaded from a json file located within the plugin jar file.
 * */
public abstract class Plugin extends JsonFileHandler {
	
	protected final PluginData pluginData;
	
	private boolean loaded = false;
	
	public Plugin(Huzuni huzuni, PluginData pluginData) {
		super(huzuni, pluginData.getSaveFile());
		this.pluginData = pluginData;
	}
	
	public abstract void init();
	
	public void loadPlugin() {
		loaded = true;
	}
	
	public void unloadPlugin() {
		loaded = false;
	}
	
	public void reloadPlugin() {
		unloadPlugin();
		loadPlugin();
	}

	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		JsonObject object = new JsonObject();
		pluginData.save(object);
		objects.add(object);
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		pluginData.load(object);
	}

	public PluginData getPluginData() {
		return pluginData;
	}

	public boolean isLoaded() {
		return loaded;
	}

}
