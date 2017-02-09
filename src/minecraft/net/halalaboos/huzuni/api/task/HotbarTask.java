package net.halalaboos.huzuni.api.task;

import net.halalaboos.huzuni.api.mod.Mod;
import net.minecraft.item.ItemStack;

public abstract class HotbarTask implements Task {
	
	protected final Mod mod;
	
	protected boolean running = false;
	
	protected int slot = -1;
	
	public HotbarTask(Mod mod) {
		this.mod = mod;
	}
	
	@Override
	public void onPreUpdate() {
		ItemStack current = null;
		int currentSlot = -1;
		for (int i = 0; i < 9; i++) {
			ItemStack item = mc.player.inventory.getStackInSlot(i);
			if (current != null) {
				if (compare(current, item)) {
					current = item;
					currentSlot = i;
				}
			} else {
				if (isValid(item)) {
					current = item;
					currentSlot = i;
				}
			}
		}
		slot = currentSlot;
		if (slot != -1) {
			if (slot != mc.player.inventory.currentItem) {
				mc.player.inventory.currentItem = slot;
				mc.playerController.updateController();
			} else
				mc.player.inventory.currentItem = slot;
		}
	}
	
	@Override
	public void onPostUpdate() {
	}

	@Override
	public void onTaskCancelled() {
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void setRunning(boolean running) {
		this.running = running;
	}

	@Override
	public Mod getMod() {
		return mod;
	}
	
	protected abstract boolean isValid(ItemStack itemStack);
	
	protected boolean compare(ItemStack currentItem, ItemStack newItem) {
		// TODO: Fix this.
		//return newItem != null && currentItem.stackSize > newItem.stackSize;
		return newItem != null && currentItem.getMaxStackSize() > newItem.getMaxStackSize();
	}
	
	public boolean hasSlot() {
		return slot != -1 ? isValid(mc.player.inventory.getStackInSlot(slot)) : false;
	}
	
}
