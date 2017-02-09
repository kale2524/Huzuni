package net.halalaboos.huzuni.meme;

import net.halalaboos.huzuni.Huzuni;
import org.apache.logging.log4j.Level;

import java.net.URL;

import javax.imageio.ImageIO;

/**
 * Downloads a meme and notifies meme listeners.
 * */
public class MemeDownloader extends Thread {
	
	private final MemeManager.MemeListener memeListener;
	
	private final String[] memeURLs;
	
	//private float progress = 0F;
	
	public MemeDownloader(MemeManager.MemeListener memeListener, String... memeURLs) {
		this.memeURLs = memeURLs;
		this.memeListener = memeListener;
	}
	
	@Override
	public void run() {
		/*try {
			URL url = new URL(memeURL);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(Proxy.NO_PROXY);
			urlConnection.setUseCaches(false);
			urlConnection.setDoInput(true);
			int contentLength = urlConnection.getContentLength(), downloaded = 0;
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			
			InputStream inputStream = urlConnection.getInputStream();
			byte[] buffer = new byte[4096];
			int len = -1;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
				downloaded += len;
				progress = (float) downloaded / (float) contentLength;
			}

			inputStream.close();
			updateTexture(outputStream.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		for (String memeURL : memeURLs) {
			try {
				memeListener.onRecieveMeme(new Meme(memeURL, ImageIO.read(new URL(memeURL))));
			} catch (Exception e) {
				Huzuni.LOGGER.log(Level.ERROR, String.format("Unable to read meme! URL: %s ERROR: %s", memeURL, e.getMessage()));
			}
		}
	}
}
