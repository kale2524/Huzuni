package net.halalaboos.huzuni.mod.visual;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

/**
 * Applies the night vision potion effect with a duration of 1,000,000 to the player.
 * */
public class Brightness extends BasicMod {
		
	private final Potion nightVision;
	
	public Brightness() {
		super("Brightness", "Light up your world as you recieve the night vision potion effect", Keyboard.KEY_C);
		this.setCategory(Category.VISUAL);
		nightVision = Potion.getPotionFromResourceLocation("night_vision");
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		mc.player.removePotionEffect(nightVision);
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		int duration = 1000000;
		PotionEffect nightVision = new PotionEffect(this.nightVision, duration, 1);
		nightVision.setPotionDurationMax(true);
		mc.player.addPotionEffect(nightVision);
	}

}
