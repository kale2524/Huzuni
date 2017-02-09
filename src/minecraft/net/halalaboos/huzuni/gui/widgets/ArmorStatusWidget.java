package net.halalaboos.huzuni.gui.widgets;

import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

/**
 * Widget which renders the player's equipped armor.
 * */
public class ArmorStatusWidget extends BackgroundWidget {

	public ArmorStatusWidget(WidgetManager menuManager) {
		super("Armor Status", "Renders equipped armor", menuManager);
		if (glue.isLeft() || glue.isRight()) {
			this.setWidth(21);
			this.setHeight(84);
		} else {
			this.setWidth(84);
			this.setHeight(21);
		}
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
		float incrementX, incrementY;
		if (glue.isLeft() || glue.isRight()) {
			incrementX = 0;
			incrementY = 21;
		} else {
			incrementX = 21;
			incrementY = 0;
		}
		renderItemStack(getWearingArmor(0).getStack(), x + 2, y + 2);
		x += incrementX; y += incrementY;
		renderItemStack(getWearingArmor(1).getStack(), x + 2, y + 2);
		x += incrementX; y += incrementY;
		renderItemStack(getWearingArmor(2).getStack(), x + 2, y + 2);
		x += incrementX; y += incrementY;
		renderItemStack(getWearingArmor(3).getStack(), x + 2, y + 2);
	}
	
	@Override
	protected void updateGlue(Glue oldGlue, Glue newGlue) {
		if (newGlue.isRight() && !oldGlue.isRight()) {
			this.setX(this.getX() + 63);
		} else if (!newGlue.isRight() && oldGlue.isRight()) {
			this.setWidth(84);
			this.setHeight(21);
		}
	}
	
	@Override
	public void useGlue() {
		if (glue.isLeft() || glue.isRight()) {
			this.setWidth(21);
			this.setHeight(84);
		} else {
			this.setWidth(84);
			this.setHeight(21);
		}
		super.useGlue();
	}
	
	private Slot getWearingArmor(int armorType) {
		return mc.player.inventoryContainer.getSlot(5 + armorType);
	}

	/**
	 * Renders the item stack at the given position.
	 * */
	private void renderItemStack(ItemStack itemStack, int x, int y) {
		if (itemStack == null)
			return;
		GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        try {
        	GlStateManager.translate(0.0F, 0.0F, 32.0F);
        	mc.getRenderItem().zLevel = 200F;
			mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
        	mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, itemStack, x, y, "");
        	mc.getRenderItem().zLevel = 0F;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
	}
}
