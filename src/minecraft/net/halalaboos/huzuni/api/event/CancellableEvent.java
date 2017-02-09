package net.halalaboos.huzuni.api.event;

/**
 * Provides the cancellable capabilities that can be applied to events.
 * */
public class CancellableEvent {

	private boolean cancelled = false;

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	
}
