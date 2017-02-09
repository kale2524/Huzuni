package net.halalaboos.huzuni.api.mod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.event.EventKeyPress;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.settings.JsonFileHandler;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.api.settings.organize.CategoryOrganizer;

/**
 * Manager class provided to load, save, and provide easy access to {@link Mod}s within the application.
 * */
public final class ModManager extends JsonFileHandler {

	private final List<Mod> mods = new ArrayList<Mod>();
	
	public ModManager(Huzuni huzuni) {
		super(huzuni, null);
	}
	

	@Override
	public void init() {
		huzuni.eventManager.addListener(this);
	}

	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		for (Mod mod : mods) {
			JsonObject object = new JsonObject();
			mod.save(object);
			objects.add(object);
		}
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		for (Mod mod : mods) {
			if (mod.isObject(object)) {
				mod.load(object);
				return;
			}
		}
	}
	
	@Override
	public void load() {
		super.load();
		new CategoryOrganizer().organize(mods);
	}
	
	/**
	 * @return The {@link BasicKeybind} located within the given mod.
	 * */
	public BasicKeybind getKeybind(Mod mod) {
		for (Node child : mod.settings.getChildren()) {
			if (child instanceof BasicKeybind) {
				return (BasicKeybind) child;
			}
		}
		return null;
	}
	
	public void addMod(Mod mod) {
		if (!mods.contains(mod))
			mods.add(mod);
	}
	
	public Mod getMod(String name) {
		for (Mod mod : mods)
			if (mod.getName().equals(name))
				return mod;
		return null;
	}
	
	/**
	 * @return The {@link Mod} loaded which is assignable from the {@code Class} provided. Returns null if the {@code Class} has not been loaded.
	 * @param clazz The {@link Mod} class to be retrieved.
	 * */
	public <T extends Mod> T getMod(Class<T> clazz) {
		for (Mod mod : mods)
			if (mod.getClass().isAssignableFrom(clazz))
				return (T) mod;
		return null;
	}
	
	/**
	 * @return A {@link Mod} associated with the {@link String} provided, ignores casing and spaces.
	 * @param name The name of the mod.
	 * */
	public Mod getModIgnoreCase(String name) {
		name = name.replaceAll(" ", "");
		for (Mod mod : mods)
			if (mod.getName().replaceAll(" ", "").equalsIgnoreCase(name))
				return mod;
		return null;
	}

	/**
     * Replaces the first instance of a given mod type with the mod provided.
     * */
	public <T extends Mod> boolean replaceMod(Class<T> type, T mod) {
        T replaced = getMod(type);
        if (replaced != null) {
            replaced.setEnabled(false);
            mods.set(mods.indexOf(replaced), mod);
            return true;
        }
        return false;
    }
	
	public List<Mod> getMods() {
		return mods;
	}
	
	@EventMethod
	public void onKeyPress(EventKeyPress event) {
		for (Mod mod : mods) {
			if (mod instanceof BasicMod) {
				BasicMod basicMod = (BasicMod) mod;
				if (basicMod.getKeybind().getKeycode() == event.keyCode) {
					basicMod.getKeybind().pressed();
				}
			}
		}
	}

}
