package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;

/**
 * Climbs up ladders at a faster rate.
 * */
public class Fastladder extends BasicMod {

	public Fastladder() {
		super("Fast ladder", "Allows you to climb ladders faster");
		this.setCategory(Category.MOVEMENT);
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
	public void onUpdate(EventUpdate event) {
        float multiplier = 0.25F;
        if (mc.player.isOnLadder() && mc.player.movementInput.moveForward != 0) {
            mc.player.motionY = multiplier;
        }
    }
}
