package net.halalaboos.huzuni.mod.misc;

import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.server.SPacketUpdateHealth;

/**
 * Respawns the player once their health has reached below 0.
 * */
public class Respawn extends BasicMod {
	
	public Respawn() {
		super("Respawn", "Automagically respawns once you're sent to the respawn screen");
		this.setCategory(Category.MISC);
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
		if (event.type == EventPacket.Type.READ) {
			if (event.getPacket() instanceof SPacketUpdateHealth) {
				SPacketUpdateHealth packet = (SPacketUpdateHealth)event.getPacket();
				if (packet.getHealth() > 0.0F)
					return;
				mc.getConnection().sendPacket(new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
			}
		}
	}

}
