package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;

/**
 * Prevents entities from pushing the player.
 * TODO: finish this
 * */
public class NoPush extends BasicMod {

	public NoPush() {
		super("No push", "Prevents other entities from pushing you.");
		setCategory(Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
	}

	@EventMethod
	public void onPacket(EventPacket event) {
	}
	
}
