package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Value;

/**
 * Allows the player to step up blocks.
 * */
public class Step extends BasicMod {
	
	public final Value height = new Value("Height", " blocks", 1F, 1F, 10F, 0.5F, "Max step height");
	
	public Step() {
		super("Step", "Step over taller blocks");
		this.setCategory(Category.MOVEMENT);
		addChildren(height);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		if (mc.player != null)
			mc.player.stepHeight = 0.5F;
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		mc.player.stepHeight = height.getValue();
	}

}
