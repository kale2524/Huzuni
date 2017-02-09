/**
 * 
 */
package net.halalaboos.huzuni.api.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Zach Shelly
 */
public final class HWID {

    /**
     * @return a HWID generated based on the computer information.
     * */
	public static String getHwid() throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String s = "";
		final String main = (System.getenv("PROCESSOR_IDENTIFIER")
				+ System.getenv("COMPUTERNAME") + System
				.getProperty("user.name")).trim();
		final byte[] bytes = main.getBytes("UTF-8");
		final MessageDigest md = MessageDigest.getInstance("MD5");
		final byte[] md5 = md.digest(bytes);
		int i = 0;
		for (final byte b : md5) {
			s += Integer.toHexString((b & 0xFF) | 0x100).substring(0, 3);
			if (i != md5.length - 1) {
				s += "-";
			}
			i++;
		}
		return s;
	}
	
}
