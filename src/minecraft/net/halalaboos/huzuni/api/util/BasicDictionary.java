package net.halalaboos.huzuni.api.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Simple dictionary class which allows a user to replace parts of input strings with randomly selected replacements. 
 * */
public class BasicDictionary {
	
	protected final Random random = new Random();

	protected final Map<String, List<DictionaryData>> dictionary = new HashMap<String, List<DictionaryData>>();
	
	/**
	 * Replaces all parts of speech that have been mapped out with this dictionary.
	 * @param text The text to replace parts of speech.
	 * @param randomizedRate The rate at which to randomly replace parts of speech.
	 * @return The modified text.
	 * */
	public String replaceAll(String text, int randomizedRate) {
		int originalRandomizedRate = randomizedRate;
		String result = "";
		String[] split = text.split(" ");
		if (split.length <= 0) {
			for (String original : dictionary.keySet()) {
				List<DictionaryData> dictionaryDatas = dictionary.get(original);
				for (DictionaryData dictionaryData : dictionaryDatas) {
					result = dictionaryData.format(random, original, text);
				}
			}
		} else {
			int count = 0;
			for (int i = 0; i < split.length; i++) {
				String word = split[i];
				if (count >= randomizedRate) {
					for (String original : dictionary.keySet()) {
						List<DictionaryData> dictionaryDatas = dictionary.get(original);
						for (DictionaryData dictionaryData : dictionaryDatas) {
							word = dictionaryData.format(random, original, word);
						}
					}
					randomizedRate = originalRandomizedRate == 0 ? 0 : random.nextInt(originalRandomizedRate);
					count = 0;
				}
				count++;
				result += word + " ";
			}
			result = result.substring(0, result.length() - 1);
		}
		return result;
	}
	
	/**
	 * Adds replacements for the specified suffix.
	 * @param suffix The suffix to replace.
	 * @param replacements The replacements for the given suffix.
	 * */
	public BasicDictionary addSuffix(String suffix, String... replacements) {
		addDictionary(Style.SUFFIX, suffix, replacements);
		return this;
	}
	
	/**
	 * Adds replacements for the specified prefix.
	 * @param prefix The prefix to replace.
	 * @param replacements The replacements for the given prefix.
	 * */
	public BasicDictionary addPrefix(String prefix, String... replacements) {
		addDictionary(Style.PREFIX, prefix, replacements);
		return this;
	}
	
	/**
	 * Adds replacements for the specified surrounded text.
	 * @param text The text to replace.
	 * @param replacements The replacements for the given text.
	 * */
	public BasicDictionary addSurrounded(String text, String... replacements) {
		addDictionary(Style.SURROUNDED, text, replacements);
		return this;
	}
	
	/**
	 * Adds replacements for the specified full word.
	 * @param word The word to replace.
	 * @param replacements The replacements for the given word.
	 * */
	public BasicDictionary addFull(String word, String... replacements) {
		addDictionary(Style.FULL, word, replacements);
		return this;
	}
	
	/**
	 * Adds replacements for the specified text. Replaces using {@link String.replaceAll(String, String)}.
	 * @param original The text to replace.
	 * @param replacements The replacements for the given text.
	 * */
	public BasicDictionary addReplace(String original, String... replacements) {
		addDictionary(Style.REPLACE, original, replacements);
		return this;
	}

	/**
     * Adds replacements with the given style to the original string.
     * */
	protected BasicDictionary addDictionary(Style style, String original, String... replacement) {
		if (dictionary.containsKey(original)) {
			List<DictionaryData> list = dictionary.get(original);
			for (DictionaryData dictionaryData : list) {
				if (dictionaryData.getStyle() == style) {
					dictionaryData.addReplacement(replacement);
					return this;
				}
			}
			DictionaryData dictionaryData = new DictionaryData(style);
			dictionaryData.addReplacement(replacement);
			list.add(dictionaryData);
		} else {
			List<DictionaryData> list = new ArrayList<DictionaryData>();
			DictionaryData dictionaryData = new DictionaryData(style);
			dictionaryData.addReplacement(replacement);
			list.add(dictionaryData);
			dictionary.put(original, list);
		}
		return this;
	}

	/**
	 * Holds string replacements and a style of replacement.
	 * */
	public class DictionaryData {
		
		private final Style style;
		
		private String[] replacements;
			
		public DictionaryData(Style style) {
			this.style = style;
		}

		/**
         * Adds the replacement strings to this dictionary data.
         * */
		public void addReplacement(String... replacement) {
			if (replacements != null) {
				String[] newReplacements = Arrays.copyOf(replacements, replacements.length + replacement.length);
				for (int i = 0; i < replacement.length; i++)
					newReplacements[replacements.length + i] = replacement[i];
				replacements = newReplacements;
			} else
				replacements = replacement;	
		}

		/**
         * Replaces the original string with a randomly selected replacement using a specific style of replacement.
         * */
		public String format(Random random, String original, String message) {
			switch (style) {
			case SUFFIX:
				if (message.toLowerCase().endsWith(original.toLowerCase()))
					return message.substring(0, message.lastIndexOf(original)) + getReplacement(random);
				else
					return message;
			case PREFIX:
				if (message.toLowerCase().startsWith(original.toLowerCase()))
					return getReplacement(random) + message.substring(original.length());
				else
					return message;
			case SURROUNDED:
				if (!message.toLowerCase().startsWith(original.toLowerCase()) && !message.toLowerCase().endsWith(original.toLowerCase()) && message.toLowerCase().contains(original.toLowerCase()))
					return message.replaceAll(original, getReplacement(random));
				else
					return message;
			case FULL:
				if (message.toLowerCase().equals(original.toLowerCase()))
					return getReplacement(random);
				else
					return message;
			case REPLACE:
				return message.replaceAll("(?i)" + original, getReplacement(random));
			default:
				return message;
			}
		}

		/**
         * @return A randomly selected replacement.
         * */
		public String getReplacement(Random random) {
			return replacements[random.nextInt(replacements.length)];
		}
		
		public Style getStyle() {
			return style;
		}
	}

	/**
     * Denotes the style used by a dictionary data.
     * */
	public enum Style {
		SUFFIX, PREFIX, SURROUNDED, FULL, REPLACE;
	}
}
