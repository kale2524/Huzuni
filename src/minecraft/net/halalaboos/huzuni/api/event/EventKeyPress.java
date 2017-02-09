package net.halalaboos.huzuni.api.event;

/**
 * This event is fired when a key is pressed by the player.
 * */
public final class EventKeyPress {

	/**
	 * The key code which represents the key pressed by the player.
	 * */
	public final int keyCode;
	
	public EventKeyPress(int keyCode) {
		this.keyCode = keyCode;
	}
	
}
