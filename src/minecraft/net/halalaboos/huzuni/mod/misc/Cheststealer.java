package net.halalaboos.huzuni.mod.misc;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.task.ClickTask;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/**
 * Steals items from within a chest once the chest was opened.
 * */
public class Cheststealer extends BasicMod {
		
	private GuiChest guiChest;
	
	private IInventory chest;
	
	private int windowId, index;

	private final ClickTask clickTask = new ClickTask(this);
	
	public Cheststealer() {
		super("Chest stealer", "Automagically steal every item from a chest inventory");
		this.setCategory(Category.MISC);
		huzuni.clickManager.registerTaskHolder(this);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.clickManager.withdrawTask(clickTask);
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (event.type == Type.PRE && huzuni.clickManager.hasPriority(this)) {
			if (mc.currentScreen instanceof GuiChest) {
				if (chest != null && guiChest != null) {
					if (!clickTask.hasClicks()) {
						mc.player.closeScreen();
						chest = null;
						guiChest = null;
						huzuni.clickManager.withdrawTask(clickTask);
					}
				} else {
					guiChest = (GuiChest) mc.currentScreen;
					chest = ((ContainerChest) guiChest.inventorySlots).getLowerChestInventory();
					index = 0;
					windowId = guiChest.inventorySlots.windowId;
					for (; index < chest.getSizeInventory(); index++) {
						ItemStack item = chest.getStackInSlot(index);
						if (item == null)
							continue;
						clickTask.add(windowId, index, 0, 1);
					}
					huzuni.clickManager.requestTask(this, clickTask);
				}
			}
		}
	}


}
