package net.halalaboos.huzuni.api.mod;

/**
 * Mod which contains a basic keybind that toggles it.
 * */
public class BasicMod extends Mod {
	
	protected BasicKeybind keybind;
	
	public BasicMod(String name, String description) {
		this(name, description, -1);
	}
	
	public BasicMod(String name, String description, int keyCode) {
		super(name, description);
		keybind = new BasicKeybind("Keybind", "Keybind for " + name, keyCode) {
			@Override
			public void pressed() {
				toggle();
			}
		};
		settings.addChildren(keybind);
		this.settings.setDisplayable(true);
	}

	public BasicKeybind getKeybind() {
		return keybind;
	}

	public void setKeybind(BasicKeybind keybind) {
		this.keybind = keybind;
	}
	
}
