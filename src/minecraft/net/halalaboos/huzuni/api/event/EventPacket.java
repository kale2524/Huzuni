package net.halalaboos.huzuni.api.event;

import net.minecraft.network.Packet;

/**
 * This event is fired when a packet is either sent or received. It is possible to cancel this event.
 * */
public class EventPacket extends CancellableEvent {

	/**
	 * The {@link Type} associated with this packet event.
	 * */
	public final Type type;
	
	private Packet<?> packet;

	public EventPacket(Type type, Packet<?> packet) {
		this.packet = packet;
		this.type = type;
	}
	
	public Packet getPacket() {
		return this.packet;
	}

	/**
	 * 
	 * */
	public void setPacket(Packet<?> packet) {
		this.packet = packet;
	}

	/**
	 * This enum is used to distinguish between different event types.
	 * */
	public enum Type {
		READ,
		SENT
	}
}
