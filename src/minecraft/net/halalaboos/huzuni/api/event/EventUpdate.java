package net.halalaboos.huzuni.api.event;

/**
 * This event is fired before and after sending motion updates to the server.
 * <br>
 * To distinguish between the two, a {@link Type} enum is provided.
 * @author herlololerbers
 * @version 1.0
 * @since 3/25/14
 */
public class EventUpdate extends CancellableEvent {

	public final Type type;

	public EventUpdate(Type type) {
		this.type = type;
	}

	/**
	 * This enum is used to distinguish between different update events.
	 * */
	public enum Type {
		PRE,
		POST
	}
}
