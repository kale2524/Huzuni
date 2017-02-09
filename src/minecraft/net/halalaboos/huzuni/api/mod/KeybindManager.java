package net.halalaboos.huzuni.api.mod;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.event.EventKeyPress;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;

/**
 * Holds keybinds and listens for key press events to invoke the keybinds.
 * */
public final class KeybindManager {
	
	private final List<Keybind> keybinds = new ArrayList<Keybind>();
	
	public KeybindManager() {
	}
	
	public void init() {
		Huzuni.INSTANCE.eventManager.addListener(this);
	}
	
	@EventMethod
	public void onKeyPress(EventKeyPress event) {
		for (Keybind keybind : keybinds) {
			if (keybind.getKeycode() == event.keyCode)
				keybind.pressed();
		}
	}
	
	public void addKeybind(Keybind keybind) {
		Huzuni.INSTANCE.LOGGER.info("Keybind added.");
		this.keybinds.add(keybind);
	}
	
	public void removeKeybind(Keybind keybind) {
		this.keybinds.add(keybind);
	}
	
	public List<Keybind> getKeybinds() {
		return keybinds;
	}
}
