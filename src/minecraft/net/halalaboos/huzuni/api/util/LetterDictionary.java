package net.halalaboos.huzuni.api.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Simple dictionary which maps character replacements.
 * */
public class LetterDictionary {
	
	protected final Random random = new Random();
	
	protected final Map<Character, Character[]> dictionary = new HashMap<Character, Character[]>();
	
	/**
	 * Replaces each character within the given string with it's mapped out counterparts.
	 * @param message The text to modify.
	 * @return The modified text.
	 * */
	public String replaceWithDictionary(String message) {
		char[] newMessage = message.toCharArray();
		for (int i = 0; i < newMessage.length; i++) {
			if (dictionary.containsKey(newMessage[i])) {
				newMessage[i] = getReplacement(newMessage[i]);
			}
		}
		return new String(newMessage);
	}

	/**
	 * Maps a character to a character.
	 * @param original The character being replaced.
	 * @param replacement The replacement for the given original character.
	 * */
	public LetterDictionary addDictionary(Character original, Character replacement) {
		if (dictionary.containsKey(original)) {
			Character[] replacements = dictionary.get(original);
			Character[] newReplacements = Arrays.copyOf(replacements, replacements.length + 1);
			newReplacements[replacements.length] = replacement;
			dictionary.put(original, newReplacements);
		} else {
			dictionary.put(original, new Character[] { replacement });
		}
		return this;
	}
	
	/**
	 * @return A character counterpart mapped to the given original character.
	 * @param original The character being replaced.
	 * */
	protected Character getReplacement(Character original) {
		Character[] replacements = dictionary.get(original);
		Character replacement = replacements[random.nextInt(replacements.length)];
		return replacement;
	}
}
