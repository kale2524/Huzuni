package net.halalaboos.huzuni;

import java.net.MalformedURLException;
import java.net.URL;

import net.halalaboos.huzuni.api.util.FileUtils;

/**
 * Downloads the latest version from http:/halalaboos.net/huzuni/version.
 * */
public final class HuzuniUpdater extends Thread {

	private final Huzuni huzuni;
	
	public HuzuniUpdater(Huzuni huzuni) {
		this.huzuni = huzuni;
	}
	
	@Override
	public void run() {
		// TODO: Update this.
		/*String version = "";
		try {
			//version = FileUtils.readURL(new URL("http://halalaboos.net/huzuni/version"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		huzuni.settings.setNewestVersion(version);*/
	}
	
}
