package net.halalaboos.huzuni.api.settings.organize;

import net.halalaboos.huzuni.api.mod.Mod;

/**
 * Organizes mods into order of largest length name to the smallest length name.
 * */
public class UpwardOrganizer extends Organizer {

	public UpwardOrganizer() {
		super("Upward");

	}

	@Override
	public int compare(Mod o1, Mod o2) {
		return o2.getDisplayNameForRender().length() - o1.getDisplayNameForRender().length();
	}

}
