package net.halalaboos.huzuni.api.util;

/**
 * String utilities class
 * */
public final class StringUtils {

	private StringUtils() {
		
	}
	
	/**
	 * Gives everything after {@code index} words in a sentence.
	 * <br>
	 * EX: {@code getAfter("this is an interesting sentence", 2)}
	 * <br>
	 * Returns 'an interesting sentence'
	 * */
	public static String getAfter(String text, int index) {
		String[] words = text.split(" ");
		if (words.length < index)
			return null;
		int splitIndex = 0;
		for (int i = 0; i < index; i++)
			splitIndex += words[i].length() + 1;
		return text.substring(splitIndex);
	}
	
	/**
	 * Gives everything before {@code index} words in a sentence.
	 * <br>
	 * EX: {@code getBefore("this is an interesting sentence", 2)}
	 * <br>
	 * Returns 'this is an'
	 * */
	public static String getBefore(String text, int index) {
		String[] words = text.split(" ");
		if (words.length < index)
			return null;
		int splitIndex = 0;
		for (int i = 0; i < index; i++)
			splitIndex += words[i].length() + 1;
		return text.substring(0, splitIndex);
	}
	
	/**
	 * Replaces {@code regex} within {@code input} with {@code replacement} while ignoring casing
	 * */
	public static String replaceIgnoreCase(String input, String regex, String replacement) {
        return input.replaceAll("(?i)" + regex, replacement);
    }
	
	/**
	 * @return true if the text could be parsed as an integer.
	 * */
	public static boolean isInteger(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @return true if the text could be parsed as a double.
	 * */
	public static boolean isDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * @return true if the text could be parsed as a double.
	 * */
	public static boolean isFloat(String text) {
		try {
			Float.parseFloat(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
}
