package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.api.util.BasicDictionary;
import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Attempts to do the complete opposite of spell checking.
 * */
public class SpellCheck extends Mutator {
	
	private BasicDictionary dictionary = new BasicDictionary();
	
	public SpellCheck() {
		super("Spell Check", "You're worst nightmare");
		dictionary.addFull("they're", "there", "their");
		dictionary.addFull("there", "they're", "their");
		dictionary.addFull("their", "they're", "there");
		dictionary.addFull("you're", "your");
		dictionary.addFull("your", "you're");
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
		return dictionary.replaceAll(message, 0);
	}

}
