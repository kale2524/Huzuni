package net.halalaboos.huzuni.mod.movement;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.minecraft.client.settings.KeyBinding;

/**
 * Forces the player to sneak.
 * */
public class Sneak extends BasicMod {
	
	public Sneak() {
		super("Sneak", "Forces you to sneak", Keyboard.KEY_Z);
		this.setCategory(Category.MOVEMENT);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
	}

}
