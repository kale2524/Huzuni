package net.halalaboos.huzuni.api.settings.organize;

import net.halalaboos.huzuni.api.mod.Mod;

/**
 * Organizes mods into alphabetical order.
 * */
public class AlphabeticalOrganizer extends Organizer {

	public AlphabeticalOrganizer() {
		super("Alphabetical");

	}

	@Override
	public int compare(Mod o1, Mod o2) {
		return o1.getDisplayNameForRender().compareTo(o2.getDisplayNameForRender());
	}
}
