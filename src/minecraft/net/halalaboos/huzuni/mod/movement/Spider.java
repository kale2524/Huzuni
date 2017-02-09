package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;

/**
 * Allows the player to scale up blocks like a spider.
 * */
public class Spider extends BasicMod {
	
	public Spider() {
		super("Spider", "Climb up walls like a lil spider");
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
		if (mc.player.isCollidedHorizontally) {
			mc.player.motionY = 0.2F;
			mc.player.onGround = true;
		}
	}
}
