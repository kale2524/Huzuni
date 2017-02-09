package net.halalaboos.huzuni.mod.misc;

import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Value;
import net.minecraft.network.play.server.SPacketUpdateHealth;

/**
 * Automatically disconnects once the health of the player reaches a threshold.
 * */
public class Autodisconnect extends BasicMod {
	
	public final Value health = new Value("Health", "", 0.5F, 6F, 20F, 0.5F, "Ratio of knockback that will be ignored.");
	
	public Autodisconnect() {
		super("Auto disconnect", "Automagically disconnects once the player health reaches below a threshold.");
		this.setCategory(Category.MISC);
		this.addChildren(health);
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
		if(event.type == EventPacket.Type.READ) {
			if(event.getPacket() instanceof SPacketUpdateHealth) {
				SPacketUpdateHealth packetUpdateHealth = (SPacketUpdateHealth)event.getPacket();
				if(packetUpdateHealth.getHealth() <= health.getValue()) {
					mc.player.getEntityBoundingBox().offset(0, 42000, 0);
					setEnabled(false);
				}
			}
		}
	}

}
