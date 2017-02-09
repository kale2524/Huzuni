package net.halalaboos.huzuni.api.settings.organize;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.halalaboos.huzuni.api.mod.Mod;

/**
 * Organizes mods into random order.
 * */
public class RandomOrganizer extends Organizer {

	public RandomOrganizer() {
		super("Random");

	}

	@Override
	public int compare(Mod o1, Mod o2) {
		return 0;
	}

	public void organize(List<Mod> mods) {
		Collections.shuffle(mods, new Random());
	}

}
