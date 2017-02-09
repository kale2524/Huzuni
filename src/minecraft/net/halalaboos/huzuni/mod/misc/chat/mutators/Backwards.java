package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Reverses strings using a StringBuilder.
 * */
public class Backwards extends Mutator {

	public Backwards() {
		super("Sdrawkcab", "doog sgniht sekam tI");
		
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
		return new StringBuilder(message).reverse().toString();
	}

}
