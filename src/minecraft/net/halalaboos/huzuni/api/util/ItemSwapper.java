package net.halalaboos.huzuni.api.util;

import net.minecraft.client.Minecraft;

/**
 * This class allows for momentarily switching the player's selected item in the hot bar.
 * <br>
 * The original item is saved and when the new slot is reset, the player's hotbar resets.
 * @deprecated This class has been replaced with the hotbar tasks.
 * */
@Deprecated
public class ItemSwapper {

	private final Minecraft mc = Minecraft.getMinecraft();
	
	private boolean running = false;
	
	private int oldSlot, newSlot;
	
	/**
	 * Updates the players hotbar item slot to the selected item slot.
	 * <br>
	 * This prevents the player from switching items while the item switcher is 'engaged'.
	 * */
	public void updateSlot() {
		if (running) {
			if (mc.player.inventory.currentItem != newSlot) {
				mc.player.inventory.currentItem = this.newSlot;
				mc.playerController.updateController();
			} else
				mc.player.inventory.currentItem = this.newSlot;
		}
	}
	
	/**
	 * Selects a hotbar item slot for the player to switch to.
	 * */
	public void swapSlot(int newSlot) {
		this.newSlot = newSlot;
		if (!running && newSlot != -1) {
			running = true;
			oldSlot = mc.player.inventory.currentItem;
		} else if (oldSlot != -1 && newSlot == -1)
			revertSlot();
	}
	
	/**
	 * Resets the item slot and reverts back to the original item slot used by the player.
	 * */
	public void revertSlot() {
		running = false;
		if (oldSlot != -1) {
			mc.player.inventory.currentItem = oldSlot;
			oldSlot = -1;
			mc.playerController.updateController();
		}
	}
	
	/**
	 * @return True if the item swapper has a slot selected.
	 * */
	public boolean hasSlot() {
		return running;
	}
}
