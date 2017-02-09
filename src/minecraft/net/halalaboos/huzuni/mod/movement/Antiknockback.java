package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.util.Timer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

/**
 * Prevents the player from receiving knockback.
 * */
public class Antiknockback extends BasicMod {
	
	private final Timer timer = new Timer();
	
	public final Toggleable combat = new Toggleable("Combat mode", "Prevents knockback when only in combat");

    public final Value ratio = new Value("Ratio", "%", 0F, 80F, 100F, 5F, "Ratio of knockback that will be ignored.");

    public final Value combatTime = new Value("Combat time", " ms", 1000F, 3000F, 10000F, 10F, "Time required to pass until no longer considered in combat");

	public Antiknockback() {
		super("Anti knockback", "Removes a percentage from the knockback velocity");
		this.addChildren(combat, combatTime, ratio);
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
	public void onPacket(EventPacket event) {
		if (event.getPacket() instanceof CPacketUseEntity) {
			CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
			if (packet.getAction() == CPacketUseEntity.Action.ATTACK) {
				timer.reset();
			}
		} else if (event.getPacket() instanceof SPacketEntityVelocity) {
			SPacketEntityVelocity packet = (SPacketEntityVelocity) event.getPacket();
			if (packet.getEntityID() == mc.player.getEntityId()) {
				if (!combat.isEnabled() || !timer.hasReach((int) combatTime.getValue())) {
					if (ratio.getValue() == 1F) {
						event.setCancelled(true);
					} else {
						float percent = 1F - (ratio.getValue() / 100F);
						event.setPacket(new SPacketEntityVelocity(packet.getEntityID(), percent * (packet.getMotionX() / 8000.0D), percent * (packet.getMotionY() / 8000.0D), percent * (packet.getMotionZ() / 8000.0D)));
					}
				}
			}
		} else if (event.getPacket() instanceof SPacketExplosion) {
			event.setCancelled(true);
		}
	}

}
