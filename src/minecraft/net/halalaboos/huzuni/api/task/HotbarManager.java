package net.halalaboos.huzuni.api.task;

import net.minecraft.client.Minecraft;

/**
 * Task manager which handles only hotbar tasks.
 * */
public class HotbarManager extends TaskManager <HotbarTask> {

	private int oldSlot = -1;
	
	public HotbarManager() {
		super("Hotbar Manager", "Manage which mods will prioritize when modifying the hotbar.");
	}

	@Override
	public boolean requestTask(String taskHolder, HotbarTask task) {
		boolean request = super.requestTask(taskHolder, task);
		if (request && task != null && oldSlot == -1)
			this.oldSlot = Minecraft.getMinecraft().player.inventory.currentItem;
		return request;
	}
	
	@Override
	public void withdrawTask(HotbarTask task) {
		if (getCurrentTask() == task && oldSlot != -1) {
			Minecraft.getMinecraft().player.inventory.currentItem = oldSlot;
			oldSlot = -1;
		}
		super.withdrawTask(task);
	}
	
}
