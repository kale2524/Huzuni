package net.halalaboos.huzuni.api.event;

/**
 * This event is fired when the player clicks with their mouse.
 * */
public final class EventMouseClick {
	
	/**
	 * The ID representing the button clicked.
	 * */
	public final int buttonId;
	
	public EventMouseClick(int buttonId) {
		this.buttonId = buttonId;
	}
	
}
