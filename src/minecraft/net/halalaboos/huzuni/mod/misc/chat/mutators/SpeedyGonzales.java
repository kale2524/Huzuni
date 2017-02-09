package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Removes all spaces from within messages.
 * */
public class SpeedyGonzales extends Mutator {

	public SpeedyGonzales() {
		super("Speedy Gonzales", "GetsYourMessagesOutFaster");
	}

	@Override
	public boolean modifyServerCommands() {
		return true;
	}

	@Override
	public boolean modifyClientCommands() {
		return true;
	}

	@Override
	public String mutate(String message) {
		return message.replaceAll(" ", "");
	}

}
