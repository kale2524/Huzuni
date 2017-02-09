package net.halalaboos.huzuni.mod.movement;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventPlayerMove;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Value;

public class Flight extends BasicMod {
	
	public static final Flight INSTANCE = new Flight();
	
	private final Value speed = new Value("Speed", "", 0.1F, 1F, 10F, "movement speed");
	
	private Flight() {
		super("Flight", "Allows an individual to fly", Keyboard.KEY_F);
		this.setCategory(Category.MOVEMENT);
		addChildren(speed);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		if (mc.player != null)
            mc.player.capabilities.isFlying = false;
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		switch (event.type) {
		case PRE:
	        mc.player.capabilities.isFlying = true;
			if (mc.player.fallDistance > 3) {
				mc.player.onGround = true;
			}
			break;
		case POST:
			if (mc.player.fallDistance > 3) {
				mc.player.onGround = false;
			}
			break;
		}
    }

	@EventMethod
	public void onPlayerMove(EventPlayerMove event) {
		event.setMotionX(event.getMotionX() * speed.getValue());
		event.setMotionY(event.getMotionY() * speed.getValue());
		event.setMotionZ(event.getMotionZ() * speed.getValue());
	}

}
