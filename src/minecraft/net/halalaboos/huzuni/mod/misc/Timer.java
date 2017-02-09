package net.halalaboos.huzuni.mod.misc;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Value;

/**
 * Modifies the timer speed of the game.
 * 
 * TODO: Fix this mod.
 * */
public class Timer extends BasicMod {
	
	public final Value speed = new Value("Multiplier", "", 0.1F, 1F, 5F, 0.1F, "Timer speed multiplier");
	
	public Timer() {
		super("Timer", "Allows you to adjust the in-game clock speed");
		this.setCategory(Category.MISC);
		addChildren(speed);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
		//mc.timer.timerSpeed = speed.getValue();
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		//mc.timer.timerSpeed = 1;
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (mc.currentScreen == null) {
			//mc.timer.timerSpeed = speed.getValue();
		} else {
			//mc.timer.timerSpeed = 1;
		}
	}
}
