package net.halalaboos.huzuni.mod.misc.chat;

import net.halalaboos.huzuni.api.settings.Toggleable;

/**
 * Modifies messages before sending them.
 * */
public abstract class Mutator extends Toggleable {

	public Mutator(String name, String description) {
		super(name, description);
	}

	/**
	 * @return True if the mutator should modify server commands ( '/' )
	 * */
	public abstract boolean modifyServerCommands();

	/**
	 * @return True if the mutator should modify client commands ( '.' )
	 * */
	public abstract boolean modifyClientCommands();

	/**
	 * @return The modified message.
	 * */
	public abstract String mutate(String message);
	
}
