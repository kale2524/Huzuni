package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Makes the user sound quite educated.
 * */
public class Educated extends Mutator {

	public Educated() {
		super("Educated", "Allows You To Speak In An Educated Fashion");
		
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
		String newMessage = "";
		if (message.contains(" ")) {
			String[] words = message.split(" ");
			for (String word : words) {
				newMessage += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
			}
			newMessage = newMessage.substring(0, newMessage.length() - 1);
		} else {
			newMessage = message.substring(0, 1).toUpperCase() + message.substring(1);
		}
		if (!(newMessage.endsWith(".") || newMessage.endsWith("!") || newMessage.endsWith("?")))
			newMessage = newMessage + ".";
		return newMessage;
	}

}
