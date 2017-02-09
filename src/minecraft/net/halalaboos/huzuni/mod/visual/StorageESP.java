package net.halalaboos.huzuni.mod.visual;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.ItemSelector;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.ItemSelector.ItemData;
import net.halalaboos.huzuni.api.util.render.Box;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.AxisAlignedBB;

import org.lwjgl.input.Keyboard;

/**
 * Renders meshes over tile entities within the world.
 * */
public class StorageESP extends BasicMod implements Renderer {

	private final Box normal, left, right;
	
	public final Toggleable boxes = new Toggleable("Boxes", "Render boxes around the blocks"),
			lines = new Toggleable("Lines", "Render lines towards blocks"), 
			fade = new Toggleable("Fade", "Make nearby boxes fade out"), 
			border = new Toggleable("Border", "Apply borders around each block");

	public final ItemSelector<Class<?>> itemSelector = new ItemSelector<Class<?>>("Enabled blocks", "OOGA BOOGA");
	
	public StorageESP() {
		super("Storage ESP", "Render boxes/lines to and around storage blocks within the world", Keyboard.KEY_Y);
		setCategory(Category.VISUAL);
		normal = new Box(new AxisAlignedBB(0, 0, 0, 1, 1, 1));
		left = new Box(new AxisAlignedBB(0, 0, 0, 2, 1, 1));
		right = new Box(new AxisAlignedBB(0, 0, 0, 1, 1, 2));
		addChildren(boxes, border, lines, fade, itemSelector);
		boxes.setEnabled(true);
		border.setEnabled(true);
		lines.setEnabled(false);
		itemSelector.addItem(new ItemStack(Blocks.CHEST), TileEntityChest.class).setEnabled(true);
		itemSelector.addItem(new ItemStack(Blocks.ENDER_CHEST), TileEntityEnderChest.class);
		itemSelector.addItem(new ItemStack(Blocks.HOPPER), TileEntityHopper.class);
		itemSelector.addItem(new ItemStack(Blocks.DISPENSER), TileEntityDropper.class, TileEntityDispenser.class);
		itemSelector.addItem(new ItemStack(Blocks.FURNACE), TileEntityFurnace.class);
		itemSelector.addItem(new ItemStack(Blocks.ENCHANTING_TABLE), TileEntityEnchantmentTable.class);
		itemSelector.addItem(new ItemStack(Items.BREWING_STAND), TileEntityBrewingStand.class);
	}

	@Override
	protected void onEnable() {
		huzuni.renderManager.addWorldRenderer(this);
	}

	@Override
	protected void onDisable() {
		huzuni.renderManager.removeWorldRenderer(this);
	}
	
	private boolean isInstance(ItemData<Class<?>> itemData, Object object) {
		for (Class<?> clazz : itemData.getTypes()) {
			if (clazz.isInstance(object)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return True if the object is an instance of the item data's sub classes and if the item data is enabled.
	 * */
	private boolean isEnabledInstance(Object object) {
		for (ItemData<Class<?>> itemData : itemSelector.getItemDatas()) {
			if (itemData.isEnabled() && isInstance(itemData, object)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void render(float partialTicks) {
		for (Object o : mc.world.loadedTileEntityList) {
			TileEntity tileEntity = (TileEntity) o;
			float renderX = (float) (tileEntity.getPos().getX() - mc.getRenderManager().viewerPosX);
			float renderY = (float) (tileEntity.getPos().getY() - mc.getRenderManager().viewerPosY);
			float renderZ = (float) (tileEntity.getPos().getZ() - mc.getRenderManager().viewerPosZ);
			float dist = (float) mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) / 128F;
			float alpha = dist > 0.25F ? 0.25F : dist;
			if (!isEnabledInstance(tileEntity))
				continue;
			if (o instanceof TileEntityChest) {
				final TileEntityChest chest = (TileEntityChest) o;
				if (this.lines.isEnabled()) {
					huzuni.renderManager.addLine(renderX + 0.5F, renderY, renderZ + 0.5F, 1F, chest.getBlockType() == Blocks.TRAPPED_CHEST ? 0F : 1F, 0F, alpha);
				}
				GlStateManager.pushMatrix();
				GlStateManager.translate(renderX, renderY, renderZ);
				if (chest.adjacentChestXPos != null) {
					if (boxes.isEnabled()) {
						colorChest(chest, alpha);
						left.setOpaque(true);
						left.render();
					}
					if (border.isEnabled()) {
						colorChest(chest, alpha);
						left.setOpaque(false);
						left.render();
					}
				} else if (chest.adjacentChestZPos != null) {
					if (boxes.isEnabled()) {
						colorChest(chest, alpha);
						right.setOpaque(true);
						right.render();
					}
					if (border.isEnabled()) {
						colorChest(chest, alpha);
						right.setOpaque(false);
						right.render();
					}
				} else if (chest.adjacentChestXNeg == null && chest.adjacentChestZNeg == null) {
					if (boxes.isEnabled()) {
						colorChest(chest, alpha);
						normal.setOpaque(true);
						normal.render();
					}
					if (border.isEnabled()) {
						colorChest(chest, alpha);
						normal.setOpaque(false);
						normal.render();
					}
				}
				GlStateManager.popMatrix();
			}
			if (tileEntity instanceof TileEntityEnderChest || tileEntity instanceof TileEntityEnchantmentTable) renderBox(tileEntity, 1F, 0.1F, 1F);
			if (tileEntity instanceof TileEntityFurnace) renderBox(tileEntity, 0.25F, 0.25F, 0.25F);
			if (tileEntity instanceof TileEntityDropper || tileEntity instanceof TileEntityDispenser) renderBox(tileEntity, 0.5F, 0.5F, 0.5F);
			if (tileEntity instanceof TileEntityHopper || tileEntity instanceof TileEntityBrewingStand) renderBox(tileEntity, 0.25F, 0.25F, 0.25F);
		}
	}

	private void colorChest(TileEntityChest tileEntity, float alpha) {
		if (tileEntity.getBlockType() == Blocks.TRAPPED_CHEST)
			GLManager.glColor(1F, 0F, 0F, fade.isEnabled() ? alpha : 0.25F);
		else
			GLManager.glColor(1F, 1F, 0F, fade.isEnabled() ? alpha : 0.25F);
	}

	/**
     * Renders a box with the given r, g, b values over the given tile entity.
     * */
	private void renderBox(TileEntity tileEntity, float r, float g, float b) {
		float dist = (float) mc.player.getDistance(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ()) / 128F;
		float alpha = dist > 0.25F ? 0.25F : dist;
		float renderX = (float) (tileEntity.getPos().getX() - mc.getRenderManager().viewerPosX);
		float renderY = (float) (tileEntity.getPos().getY() - mc.getRenderManager().viewerPosY);
		float renderZ = (float) (tileEntity.getPos().getZ() - mc.getRenderManager().viewerPosZ);
		if (this.lines.isEnabled()) {
			huzuni.renderManager.addLine(renderX + 0.5F, renderY, renderZ + 0.5F, r, g, b, fade.isEnabled() ? alpha : 0.25F);
		}
		GlStateManager.pushMatrix();
		GlStateManager.translate(renderX, renderY, renderZ);
		if (boxes.isEnabled()) {
			GLManager.glColor(r, g, b, fade.isEnabled() ? alpha : 0.25F);
			normal.setOpaque(true);
			normal.render();
		}
		if (border.isEnabled()) {
			GLManager.glColor(r, g, b, fade.isEnabled() ? alpha : 0.25F);
			normal.setOpaque(false);
			normal.render();
		}
		GlStateManager.popMatrix();
	}
}
