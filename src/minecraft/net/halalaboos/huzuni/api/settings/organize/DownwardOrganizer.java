package net.halalaboos.huzuni.api.settings.organize;

import net.halalaboos.huzuni.api.mod.Mod;

/**
 * Organizes mods into order of smallest length name to the largest length name.
 * */
public class DownwardOrganizer extends Organizer {

	public DownwardOrganizer() {
		super("Downward");

	}

	@Override
	public int compare(Mod o1, Mod o2) {
		return o1.getDisplayNameForRender().length() - o2.getDisplayNameForRender().length();
	}

}
