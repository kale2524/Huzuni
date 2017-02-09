package net.halalaboos.huzuni.api.event;

import net.minecraft.client.multiplayer.WorldClient;

/**
 * This event is fired when Minecraft loads a new world.
 * */
public final class EventLoadWorld {

	/**
	 * The {@link WorldClient} instance loaded.
	 * */
	public final WorldClient world;
	
	public EventLoadWorld(WorldClient world) {
		this.world = world;
	}
	
}
