package net.halalaboos.huzuni.mod.misc.chat.mutators;

import java.util.Random;

import net.halalaboos.huzuni.mod.misc.chat.Mutator;

/**
 * Throws random emoticons within the message and slaps an exclamation mark at the end if possible. <br/>
 * Note: These are not emojis. They are emoticons since they are created out of text.
 * */
public class Emoticon extends Mutator {

	private final Random random = new Random();
	
	private final String[] emoticons = new String[] {
			":^)", "(:", ":)", ":(", "):", ">:)", "(:<", ":D", "D:", "xD", "Dx", ">:D", "D:<", ":o", "o:", ":O", "O:", ":0", "0:", ">:o", "o:<", ">:O", "O:<", ">:0", "0:<", "=^.^=", ":'(", ")':",  ">:'(", ")':<", "-_-", "=)", "(=", "=D", "D=", ":P", "=P", "$_$", "^_^", ":>", "<:", ";)", "(;"
	};
	
	public Emoticon() {
		super("Emoticon", "Helps you (: talk more nicely! :D");
		this.setEnabled(true);
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
		return addPunctuation(insertRandomly(message, 8), "!");
	}

	/**
     * Inserts emoticons at random within the message with the given frequency.
     * */
	private String insertRandomly(String message, int frequency) {
		if (message.contains(" ")) {
			String result = "";
			int count = 0, randomFrequency = 1 + random.nextInt(frequency - 1);
			String[] words = message.split(" ");
			for (String word : words) {
				result += word + " ";
				count++;
				if (count >= randomFrequency) {
					result += getEmoticon() + " ";
					randomFrequency = 1 + random.nextInt(frequency - 1);
					count = 0;
				}
			}
			return result.substring(0, result.length() - 1);
		}
		return message;
	}

	/**
     * Adds the given punctuation to the end of a string if no punctuation is present.
     * */
	private String addPunctuation(String message, String punctuation) {
		if (!(message.endsWith(".") || message.endsWith("?") || message.endsWith("!"))) {
			return message.concat(punctuation);
		}
		return message;
	}

	/**
     * @return A random emoticon.
     * */
	private String getEmoticon() {
		String replacement = emoticons[random.nextInt(emoticons.length)];
		return replacement;
	}

}
