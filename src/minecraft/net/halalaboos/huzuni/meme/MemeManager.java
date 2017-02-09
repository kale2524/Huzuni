package net.halalaboos.huzuni.meme;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.mod.CommandPointer;
import net.halalaboos.huzuni.api.util.render.RenderUtils;

/**
 * Manages all memes within the client. <br/>
 * @see <a href="http://version1.api.memegenerator.net/">Meme Generator API</a> for more details on it's use.
 * */
public final class MemeManager {

	private final Huzuni huzuni;

	// meme load/generate/clear
	public MemeManager(Huzuni huzuni) {
		this.huzuni = huzuni;
	}
	
	public void init() {
    }

    /**
     * Launches a thread contacting the meme generator api.
     * @param memeTypeIndex Index of meme types to begin at.
     * @param memeTypeAmount Amount of meme types.
     * @param memeIndex Index of memes to begin at.
     * @param memeAmount Amount of memes.
     * */
	public MemeGenerator getMeme(int memeTypeIndex, int memeTypeAmount, int memeIndex, int memeAmount, MemeListener memeListener) {
        MemeGenerator memeGenerator = new MemeGenerator(huzuni, memeListener, memeTypeIndex, memeTypeAmount, memeIndex, memeAmount);
        memeGenerator.start();
        return memeGenerator;
	}

	/**
     * Launches a thread downloading the given memes (image format) which notifies the meme listener.
     * */
	public MemeDownloader getMeme(MemeListener memeListener, String... urls) {
        MemeDownloader memeDownloader = new MemeDownloader(memeListener, urls);
        memeDownloader.start();
        return memeDownloader;
    }

    /**
     * Handles memes which are downloaded.
     * */
    public interface MemeListener {
        void onRecieveMeme(Meme meme);
    }
}
