package net.halalaboos.huzuni.mod.misc.chat.mutators;

import net.halalaboos.huzuni.api.util.LetterDictionary;
import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Terrible mutator which replaces letters with numbers and symbols.
 * */
public class LeetSpeak extends Mutator {

	private LetterDictionary dictionary = new LetterDictionary();
	
	public LeetSpeak() {
		super("L33t Speak", "5p34k 1!k3 7h1$");
		dictionary.addDictionary('a', '4').addDictionary('A', '4');
		dictionary.addDictionary('b', '8').addDictionary('B', '8');
		dictionary.addDictionary('e', '3').addDictionary('E', '3');
		dictionary.addDictionary('i', '!').addDictionary('I', '!');
		dictionary.addDictionary('l', '1').addDictionary('L', '1');
		dictionary.addDictionary('o', '0').addDictionary('O', '0');
		dictionary.addDictionary('s', '5').addDictionary('s', '$').addDictionary('S', '5').addDictionary('S', '$');
		dictionary.addDictionary('t', '7').addDictionary('T', '7');
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
		return dictionary.replaceWithDictionary(message);
	}

}
