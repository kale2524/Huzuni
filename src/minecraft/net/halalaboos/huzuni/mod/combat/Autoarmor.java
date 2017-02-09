package net.halalaboos.huzuni.mod.combat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.ItemList;
import net.halalaboos.huzuni.api.settings.Nameable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.ClickTask;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Automatically equips the preferred armor.
 * */
public class Autoarmor extends BasicMod {
	
	public final ItemList<EnchantmentItem> enchantmentOrder = new ItemList<EnchantmentItem>("Enchantment Priority", "Put each armor enchantment into an order of importance (first being most important).") {

		@Override
		public void load(JsonObject object) throws IOException {
			this.clearChildren();
			super.load(object);
		}
		@Override
		protected void saveItem(JsonObject object, EnchantmentItem enchantmentItem) {
			object.addProperty("enchantmentId", Enchantment.getEnchantmentID(enchantmentItem.getEnchantment()));
		}

		@Override
		protected EnchantmentItem loadItem(JsonObject object) {
			return new EnchantmentItem(Enchantment.getEnchantmentByID(object.get("enchantmentId").getAsInt()));
		}
	
	};
	
	public final Value delay = new Value("Attempt Delay", " ms", 0F, 200F, 2000F, 10F, "Delay in MS between each attempt.");
	
	private final Timer timer = new Timer();

	private final ClickTask clickTask = new ClickTask(this);

	private int[] bestSlot = new int[] {
			-1, -1, -1, -1
	};
	
	public Autoarmor() {
		super("Auto armor", "Automagically equip the best armor within your inventory");
		this.setCategory(Category.COMBAT);
		this.addChildren(delay, enchantmentOrder);
		enchantmentOrder.setOrdered(true);
		enchantmentOrder.add(new EnchantmentItem(Enchantments.PROTECTION));
		enchantmentOrder.add(new EnchantmentItem(Enchantments.PROJECTILE_PROTECTION));
		enchantmentOrder.add(new EnchantmentItem(Enchantments.FIRE_PROTECTION));
		enchantmentOrder.add(new EnchantmentItem(Enchantments.BLAST_PROTECTION));
		enchantmentOrder.add(new EnchantmentItem(Enchantments.THORNS));
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
		if (mc.currentScreen != null || event.type == Type.POST)
    		return;
        List<Integer> armors = getArmor();
        for (int i : armors) {
            Slot slot = mc.player.inventoryContainer.getSlot(i);
            ItemArmor armor = (ItemArmor) slot.getStack().getItem();
            if (bestSlot[armor.armorType.getIndex()] != -1) {
                Slot oldSlot = mc.player.inventoryContainer.getSlot(bestSlot[armor.armorType.getIndex()]);
                if (compare(armor, (ItemArmor) oldSlot.getStack().getItem(), slot.getStack(), oldSlot.getStack())) {
                	bestSlot[armor.armorType.getIndex()] = i;
                }
            } else {
                Slot wearingSlot = getWearingArmor(armor.armorType.getIndex());
                if (wearingSlot.getHasStack()) {
                    if (!(wearingSlot.getStack().getItem() instanceof ItemArmor)) {
                    	bestSlot[8 - wearingSlot.slotNumber] = i;
                    } else {
	                    ItemArmor wearingArmor = (ItemArmor) wearingSlot.getStack().getItem();
	                    if (compare(armor, wearingArmor, slot.getStack(), wearingSlot.getStack())) {
	                    	bestSlot[8 - wearingSlot.slotNumber] = i;
	                    }
                    }
                } else {
                	bestSlot[8 - wearingSlot.slotNumber] = i;
                }	
            }
        }
        for (int i = 0; i < bestSlot.length; i++) {
        	if (bestSlot[i] != -1) {
        		replace(bestSlot[i], 8 - i);
        	}
        	bestSlot[i] = -1;
        }
        if (clickTask.hasClicks())
        	huzuni.clickManager.requestTask(this, clickTask);
		else
			huzuni.clickManager.withdrawTask(clickTask);
	}

    /**
     * Compares two item stacks (and their associated item) in regards to enchantment value.
     */
	private boolean compare(ItemArmor newArmor, ItemArmor oldArmor, ItemStack newStack, ItemStack oldStack) {
		for (int i = 0; i < enchantmentOrder.size(); i++) {
			Enchantment enchantment = enchantmentOrder.get(i).getEnchantment();
			int oldEnchantment, newEnchantment;
        	if (enchantment == Enchantments.PROTECTION) {
        		oldEnchantment = oldArmor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(enchantment, oldStack);
                newEnchantment = newArmor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(enchantment, newStack);
        	} else {
        		oldEnchantment = EnchantmentHelper.getEnchantmentLevel(enchantment, oldStack);
                newEnchantment = EnchantmentHelper.getEnchantmentLevel(enchantment, newStack);
        	}
        	if (oldEnchantment == newEnchantment) {
        		continue;
        	} else {
        		return oldEnchantment < newEnchantment;
        	}
        }
		return false;
	}

	/**
     * Attempts to replace the old armor slot with the new armor item.
     * */
	private void replace(int newArmor, int oldArmor) {
		Slot oldSlot = mc.player.inventoryContainer.getSlot(oldArmor);
		if (clickTask.containsClick(newArmor, 0, 0))
			timer.reset();
		else if (timer.hasReach((int) delay.getValue())) {
			if (!oldSlot.getHasStack()) {
				clickTask.add(newArmor, 0, 0);
				clickTask.add(oldArmor, 0, 0);
			} else {
				clickTask.add(newArmor, 0, 0);
				clickTask.add(oldArmor, 0, 0);
				clickTask.add(newArmor, 0, 0);
			}
			huzuni.clickManager.requestTask(this, clickTask);
			huzuni.addNotification(NotificationType.INFO, this, 5000, "Equipping armor!");
		}
	}

    /**
     * Finds all pieces of armor within the inventory.
     */
    private List<Integer> getArmor() {
        List<Integer> list = new ArrayList<Integer>();
        for (int o = 9; o < 45; o++) {
            if (mc.player.inventoryContainer.getSlot(o).getHasStack()) {
                ItemStack item = mc.player.inventoryContainer.getSlot(o).getStack();
                if (item != null)
                    if (item.getItem() instanceof ItemArmor)
                    	list.add(o);
            }
        }
        return list;
    }

    /**
     * @return The slot which is associated with the given armor type.
     * */
	private Slot getWearingArmor(int armorType) {
		return mc.player.inventoryContainer.getSlot(8 - armorType);
	}

	/**
     * Nameable which holds an enchantment.
     * */
	public static class EnchantmentItem implements Nameable {
		
		private final Enchantment enchantment;
		
		public EnchantmentItem(Enchantment enchantment) {
			this.enchantment = enchantment;
		}

		@Override
		public String getName() {
			return enchantment.getTranslatedName(1);
		}
		
		@Override
		public String getDescription() {
			return getName() + " enchantment.";
		}
		
		public Enchantment getEnchantment() {
			return enchantment;
		}
		
	}
}
