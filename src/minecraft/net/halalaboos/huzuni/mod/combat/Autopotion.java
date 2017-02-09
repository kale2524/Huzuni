package net.halalaboos.huzuni.mod.combat;

import java.util.List;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.ClickTask;
import net.halalaboos.huzuni.api.task.HotbarTask;
import net.halalaboos.huzuni.api.task.LookTask;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;

/**
 * Automatically uses potions when the health reaches below a threshold. <br/>
 * Moves potions from the inventory to the hotbar.
 * */
public class Autopotion extends BasicMod {
		
	public final Value swapDelay = new Value("Swap Delay", " ms", 0F, 200F, 2000F, 10F, "Delay in MS between each inventory move attempt");
	
	public final Value useDelay = new Value("Use Delay", " ms", 0F, 400F, 2000F, 10F, "Delay in MS between each item use attempt");
	
	public final Value healthAmount = new Value("Health", "", 1F, 12F, 19F, 1F, "Minimum health required before attempting to use potions");

	public final Toggleable usePotions = new Toggleable("Use Potions", "Automatically attempt to use potions");
	
	private final Timer timer = new Timer(), useTimer = new Timer();
	
	private final LookTask lookTask = new LookTask(this);
	
	private final HotbarTask hotbarTask = new HotbarTask(this) {

		@Override
		protected boolean isValid(ItemStack itemStack) {
			return itemStack != null && isPotion(itemStack);
		}
		
	};

	private final ClickTask clickTask = new ClickTask(this);

	private final Potion health;
	
	public Autopotion() {
		super("Auto potion", "Automagically move health potions into the hotbar and use potions when the health reaches below a threshold");
		setCategory(Category.COMBAT);
		addChildren(swapDelay, useDelay, healthAmount, usePotions);
		health = Potion.getPotionFromResourceLocation("instant_health");
		huzuni.lookManager.registerTaskHolder(this);
		huzuni.hotbarManager.registerTaskHolder(this);
		huzuni.clickManager.registerTaskHolder(this);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.lookManager.withdrawTask(lookTask);
		huzuni.hotbarManager.withdrawTask(hotbarTask);
		huzuni.clickManager.withdrawTask(clickTask);
	}
	
	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (mc.currentScreen != null)
			return;
		switch (event.type) {
		case PRE:
			 if (huzuni.hotbarManager.hasPriority(this) && huzuni.lookManager.hasPriority(this) && huzuni.clickManager.hasPriority(this) && usePotions.isEnabled() && needUsePotion()) {
				int hotbarPotion = findHotbarPotion();
				if (hotbarPotion != -1 && useTimer.hasReach((int) useDelay.getValue())) {
					lookTask.setRotations(mc.player.rotationYaw, 90);
					huzuni.lookManager.requestTask(this, lookTask);
					huzuni.hotbarManager.requestTask(this, hotbarTask);
			    	useTimer.reset();
				} else {
					huzuni.lookManager.withdrawTask(lookTask);
					huzuni.hotbarManager.withdrawTask(hotbarTask);
					movePotions();
				}
			} else {
				huzuni.lookManager.withdrawTask(lookTask);
				huzuni.hotbarManager.withdrawTask(hotbarTask);
				movePotions();
			}
			if (clickTask.hasClicks())
				huzuni.clickManager.requestTask(this, clickTask);
			else
				huzuni.clickManager.withdrawTask(clickTask);
			break;
		case POST:
			if (lookTask.isRunning() && hotbarTask.isRunning()) {
				usePotion();
			}
			break;
		}
	}

	/**
     * Moves items from the inventory to the hot bar.
     * */
	private void movePotions() {
		int emptySlot = findEmptyPotion();
		if (emptySlot != -1) {
			int newPotion = getUseablePotion();
			if (newPotion != -1) {
				ItemStack itemStack = mc.player.inventoryContainer.getSlot(newPotion).getStack();
				if (isShiftable(itemStack)) {
					if (clickTask.containsClick(newPotion, 0, 1))
						timer.reset();
					else if (timer.hasReach((int) swapDelay.getValue())) {
						clickTask.add(newPotion, 0, 1);
						huzuni.addNotification(NotificationType.INFO, this, 5000, "Shift-clicking potion!");
					}
				} else {
					if (clickTask.containsClick(newPotion, 0, 0))
						timer.reset();
					else if (timer.hasReach((int) swapDelay.getValue())) {
						huzuni.addNotification(NotificationType.INFO, this, 5000, "Moving potion!");
						clickTask.add(newPotion, 0, 0);
						clickTask.add(emptySlot, 0, 0);
						clickTask.add(newPotion, 0, 0);
					}
			    }
			}
		}
	}
	
	/**
     * @return The index for the first usable potion found within the inventory.
     */
    private int getUseablePotion() {
        for (int o = 9; o < 36; o++) {
            if (mc.player.inventoryContainer.getSlot(o).getHasStack()) {
                ItemStack item = mc.player.inventoryContainer.getSlot(o).getStack();
                if (isPotion(item))
                	return o;
            }
        }
        return -1;
    }

    /**
     * @return The first index within the hotbar which either contains no item or an empty glass bottle.
     * */
    private int findEmptyPotion() {
        for (int o = 36; o < 45; o++) {
            ItemStack item = mc.player.inventoryContainer.getSlot(o).getStack();
            if (item == null)
            	return o;
            else if (item.getItem() instanceof ItemGlassBottle)
            	return o;
        }
        return -1;
	}

	/**
     * @return The index of the first potion within the hotbar.
     * */
    private int findHotbarPotion() {
        for (int o = 0; o < 9; o++) {
            ItemStack item = mc.player.inventory.getStackInSlot(o);
            if (item != null && isPotion(item))
            	return o;
        }
        return -1;
	}
    
    /**
     * @return True if the item is shift clickable.
     */
    private boolean isShiftable(ItemStack preferedItem) {
        if (preferedItem == null)
            return true;
        for (int o = 36; o < 45; o++) {
            if (mc.player.inventoryContainer.getSlot(o).getHasStack()) {
                ItemStack item = mc.player.inventoryContainer.getSlot(o).getStack();
                if (item == null)
                    return true;
                else if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(preferedItem.getItem())) {
                	// TODO: Fix this.
                	// if (item.stackSize + preferedItem.stackSize <= preferedItem.getMaxStackSize())
                    if (item.getMaxStackSize() + preferedItem.getMaxStackSize() <= preferedItem.getMaxStackSize())
                        return true;
                }
            } else
                return true;
        }
        return false;
    }

    /**
     * @return True if the item stack is a health potion.
     * */
	private boolean isPotion(ItemStack itemStack) {
		if (itemStack.getItem() instanceof ItemSplashPotion) {
			for (PotionEffect effect : (List<PotionEffect>) PotionUtils.getEffectsFromStack(itemStack)) {
				if (effect.getPotion() == health) {
					return true;
				}
			}
		}
		return false;
	}

	/**
     * @return True if the player's health reaches below the threshold.
     * */
	private boolean needUsePotion() {
		return mc.player.getHealth() <= healthAmount.getValue();
	}

	/**
     * Attempts to use a potion.
     * */
	private void usePotion() {
		EnumHand hand = EnumHand.MAIN_HAND;
		ItemStack item = mc.player.getHeldItem(EnumHand.MAIN_HAND);
		// TODO: Fix this.
        //if (item != null && mc.playerController.processRightClick(mc.player, mc.world, item, hand) == EnumActionResult.SUCCESS) {
		if (item != null && mc.playerController.processRightClick(mc.player, mc.world, hand) == EnumActionResult.SUCCESS) {
			huzuni.addNotification(NotificationType.CONFIRM, this, 5000, "Using potion!");
        }
	}
	
}
