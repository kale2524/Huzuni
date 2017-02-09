package net.halalaboos.huzuni.mod.movement;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;

public class Nofall extends BasicMod {
	
	public Nofall() {
		super("Nofall", "Prevents fall damage from occuring", Keyboard.KEY_N);
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
		if (event.type == Type.PRE) {
			if (mc.player.fallDistance > 3) {
				mc.player.onGround = true;
			}	
		} else {
			if (mc.player.fallDistance > 3) {
				mc.player.onGround = false;
			}
		}
	}
}
