package net.halalaboos.huzuni.meme;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.minecraft.client.Minecraft;
import net.minecraft.util.HttpUtil;
import org.apache.logging.log4j.Level;

/**
 * Gathers urls for memes using the meme generator api.
 * */
public class MemeGenerator extends Thread {

	private final Huzuni huzuni;

	private MemeManager.MemeListener memeListener;
	
	private String memeType = "http://version1.api.memegenerator.net/Generators_Select_ByPopular?pageIndex=%d&pageSize=%d&days=7";
	
	private final String actualMeme = "http://version1.api.memegenerator.net/Instances_Select_ByPopular?languageCode=en&pageIndex=%d&pageSize=%d&urlName=%s";

	private final int memeTypeIndex, memeTypeAmount, memeIndex, memeAmount;

	/**
	 * @param memeTypeIndex Index of meme types to begin at.
     * @param memeTypeAmount Amount of meme types.
     * @param memeIndex Index of memes to begin at.
     * @param memeAmount Amount of memes.
	 * */
	public MemeGenerator(Huzuni huzuni, MemeManager.MemeListener memeListener, int memeTypeIndex, int memeTypeAmount, int memeIndex, int memeAmount) {
		this.huzuni = huzuni;
		this.memeListener = memeListener;
		this.memeTypeIndex = memeTypeIndex;
		this.memeTypeAmount = memeTypeAmount;
		this.memeIndex = memeIndex;
		this.memeAmount = memeAmount;
	}

	@Override
	public void run() {
		try {
			Gson gson = new GsonBuilder().create();
			JsonObject object = gson.fromJson(HttpUtil.postMap(new URL(String.format(memeType, memeTypeIndex, memeTypeAmount)), new HashMap<String, Object>(), false, Minecraft.getMinecraft().getProxy()).trim(), JsonObject.class);
			
			JsonArray array = object.get("result").getAsJsonArray();
			List<String> memes = new ArrayList<String>();
			for (JsonElement element : array) {
				JsonElement urlElement = element.getAsJsonObject().get("urlName");
				if (urlElement != null) {
					memes.add(urlElement.getAsString());
				}
			}

			List<String> memeURLS = new ArrayList<String>();
			for (String meme : memes) {
				JsonObject memeObject = gson.fromJson(HttpUtil.postMap(new URL(String.format(actualMeme, memeIndex, memeAmount, meme)), new HashMap<String, Object>(), false, Minecraft.getMinecraft().getProxy()).trim(), JsonObject.class);
				JsonArray resultArray = memeObject.get("result").getAsJsonArray();
				for (JsonElement element : resultArray) {
					JsonElement urlElement = element.getAsJsonObject().get("instanceImageUrl");
					if (urlElement != null)
                        memeURLS.add(urlElement.getAsString());
				}
				Thread.sleep(50);
			}
			if (!memeURLS.isEmpty()) {
                MemeDownloader downloader = new MemeDownloader(memeListener, memeURLS.toArray(new String[memeURLS.size()]));
                downloader.start();
            }
		} catch (Exception e) {
			huzuni.LOGGER.log(Level.ERROR, String.format("Unable to connect to meme generator API! ERROR: %s", e.getMessage()));
		}
	}
	
}
