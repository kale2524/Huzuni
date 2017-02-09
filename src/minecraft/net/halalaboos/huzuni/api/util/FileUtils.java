package net.halalaboos.huzuni.api.util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Simple utility class to allow for simpler file reading/writing.
 * */
public final class FileUtils {

	private FileUtils() {
		
	}
	
	/**
	 * Reads from a given {@link File}.
	 * @param file The {@link File} to read from.
	 * @return A {@link List} of {@link String}s from the {@link File}.
	 * */
    public static List<String> readFile(File file) {
        List<String> tempList = new ArrayList<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            for (String s; (s = reader.readLine()) != null; ) {
                tempList.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return tempList;
    }
    
    /**
     * Reads from the given {@link URL}.
     * @param url The {@link URL} to read from.
     * @return A {@link String} of the contents from the {@link URL}. 
     * */
    public static String readURL(URL url) {
        String temp = "";

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            for (String s; (s = reader.readLine()) != null; ) {
            	temp += s;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return temp;
    }
    
    /**
     * Reads from the given {@link URL}.
     * @param url The {@link URL} to read from.
     * @return A {@link List} of {@link String}s of the contents from the {@link URL}. 
     * */
    public static List<String> readURLList(URL url) {
        List<String> temp = new ArrayList<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()));
            for (String s; (s = reader.readLine()) != null; ) {
            	temp.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return temp;
    }
    
    /**
     * Writes the given {@link Collection} into the {@link File} given.
     * @param file The given {@link File}.
     * @param text The {@link Collection} to be written into the {@link File}.
     * */
    public static void writeFile(File file, Collection<String> text) {
    	writeFile(file, text.toArray(new String[text.size()]));
    }

    /**
     * Writes the given array of {@link String}s into the {@link File} given.
     * @param file The given {@link File}.
     * @param text The array of {@link String}s to be written into the {@link File}.
     * */
    public static void writeFile(File file, String[] text) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(file));
            for (String s : text) {
                writer.println(s);
                writer.flush();
            }
        } catch (Exception localException) {
        } finally {
            if (writer != null)
                writer.close();
        }
    }
}
