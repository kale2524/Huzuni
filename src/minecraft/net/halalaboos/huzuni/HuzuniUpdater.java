package net.halalaboos.huzuni;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import net.halalaboos.huzuni.api.util.FileUtils;

/**
 * Get's the latest version of the client from https://github.com/MatthewSH/minecraft-Huzuni/releases.
 * */
public final class HuzuniUpdater extends Thread {

	private final Huzuni huzuni;
	private String version;
	
	public HuzuniUpdater(Huzuni huzuni) {
		this.huzuni = huzuni;
		version = Huzuni.VERSION;
	}
	
	@Override
	public void run() {
		huzuni.settings.setNewestVersion(Huzuni.VERSION);
		
		if(version.contains("beta") || version.contains("alpha")) {
			Huzuni.LOGGER.info("You're using a beta version of Huzuni, we won't display updates.");
			return;
		}
		
		
        try {
        	URL url = new URL("https://api.github.com/repos/MatthewSH/minecraft-Huzuni/releases/latest");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
	        String line;
	        StringBuffer response = new StringBuffer();

	        while ((line = in.readLine()) != null) {
	            response.append(line);
	        }

	        in.close();
	        
	        JsonObject page = new JsonParser().parse(response.toString()).getAsJsonObject();	        
	       
	        String newVersion = page.get("tag_name").toString().replaceAll("v", "").replaceAll("\"", "");
	        
	        if(version.equalsIgnoreCase(newVersion)) {
	        	return;
	        }

	        String[] vArr = version.split("\\.");
	        String[] nwArr = newVersion.split("\\.");

	        if((Integer.parseInt(vArr[0]) >= Integer.parseInt(nwArr[0])) &&
	        	(Integer.parseInt(vArr[1]) >= Integer.parseInt(nwArr[1])) &&
	        	(Integer.parseInt(vArr[2]) >= Integer.parseInt(nwArr[2]))) {
	        	return;
	        } else {
	        	huzuni.settings.setNewestVersion("Huzuni v" + newVersion + " is available for download.");
	        	return;
	        }
	        
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        huzuni.settings.setNewestVersion(Huzuni.VERSION);
	}
}
