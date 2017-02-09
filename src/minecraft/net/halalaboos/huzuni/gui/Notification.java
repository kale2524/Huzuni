package net.halalaboos.huzuni.gui;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.util.IncrementalPosition;
import net.halalaboos.huzuni.api.util.Timer;

/**
 * Used to provide useful information for the player in-game.
 * */
public class Notification {

	public static final int ICON_SIZE = 12;
	
	private final IncrementalPosition position = new IncrementalPosition();

	private final NotificationType type;
	
	private final String source;
	
	private final long time;
		
	private String[] message;
	
	private int duration = 0;
			
	public Notification(NotificationType type, String source, int duration, String... message) {
		this.type = type;
		this.source = source;
		this.time = Timer.getSystemTime();
		this.duration = duration;
		this.message = message;
	}

	/**
	 * @return The width of the notification based on the theme provided.
	 * */
	public int getWidth(Theme theme) {
		int width = ICON_SIZE + 2;
		for (String message : this.message) {
			int messageWidth = theme.getStringWidth(message) + ICON_SIZE + 2;
			if (messageWidth + 5 >= width)
				width = messageWidth + 5;
		}
		return width;
	}
	
	public int getHeight() {
		return this.message.length * 12;
	}
	
	public String getSource() {
		return source;
	}
	
	public long getTime() {
		return time;
	}
	
	public boolean hasExpired() {
		return Timer.getSystemTime() - getTime() >= duration;
	}
	
	public NotificationType getType() {
		return type;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public IncrementalPosition getPosition() {
		return position;
	}

	public String[] getMessage() {
		return message;
	}

	/**
	 * Used to organize notifications and allow for user...
	 * */
	public enum NotificationType {
		ERROR("X", 0xFF0000, 3, 0) /* RED X */, INFO("!", 0xFFFF00, 4, 0) /* YELLOW ! */, CONFIRM("C", 0x00FF00, 2, 0) /* GREEN C */, INQUIRE("?", 0x0000FF, 3, 0) /* BLUE ? */;
		
		public final String text;

		public final int color, offsetX, offsetY;
		
		NotificationType(String text, int color, int offsetX, int offsetY) {
			this.text = text;
			this.color = color;
			this.offsetX = offsetX;
			this.offsetY = offsetY;
		}
	}
}
