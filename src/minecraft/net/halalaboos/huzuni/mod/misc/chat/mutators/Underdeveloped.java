package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Oiriginally coined 'retard speak', this mod expands messages and capitalizes them.
 * */
public class Underdeveloped extends Mutator {

	public Underdeveloped() {
		super("Underdeveloped", "H E L P S Y O U S O U N D C O O L");
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
		return undevelop(message);
	}
	
	private String undevelop(String message) {
		char[] characters = message.replaceAll(" ", "").toCharArray();
		char[] newMessage = new char[characters.length * 2];
		
		for (int i = 0; i < characters.length; i++) {
			newMessage[i * 2] = Character.toUpperCase(characters[i]);
			newMessage[(i * 2) + 1] = ' ';
		}
		return new String(newMessage);
	}

}
