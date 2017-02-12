package net.halalaboos.huzuni;

import java.net.MalformedURLException;
import java.net.URL;

import net.halalaboos.huzuni.api.util.FileUtils;

/**
 * Downloads the latest version from http:/halalaboos.net/huzuni/version.
 * */
public final class HuzuniUpdater extends Thread {

	private final Huzuni huzuni;
	private String version;
	
	public HuzuniUpdater(Huzuni huzuni) {
		this.huzuni = huzuni;
		version = Huzuni.VERSION.split(" ")[1];
	}
	
	@Override
	public void run() {
		if(version.contains("beta") || version.contains("alpha")) {
			huzuni.settings.setNewestVersion(Huzuni.VERSION);
			Huzuni.LOGGER.info("You're using a beta version, we won't display updates.");
			return;
		}
	}
}
