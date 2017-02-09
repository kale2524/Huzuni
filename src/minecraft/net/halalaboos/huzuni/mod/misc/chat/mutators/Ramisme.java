package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * This mod only returns 'imgay'. Originally found within the client Flare.
 * */
public class Ramisme extends Mutator {
		
	public Ramisme() {
		super("Ramisme", "imgay");
	}

	@Override
	public boolean modifyServerCommands() {
		return false;
	}

	@Override
	public boolean modifyClientCommands() {
		return false;
	}

	@Override
	public String mutate(String message) {
		return "imgay";
	}

}
