package net.halalaboos.huzuni.gui.screen.account;

import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class AccountListEntry implements GuiListExtended.IGuiListEntry {

	private ResourceLocation resourceLocation;
		
	private final AccountSelectionList list;
	
	private final String account;
	
	public AccountListEntry(AccountSelectionList list, String account) {
		this.list = list;
		this.account = account;
	}
	
	@Override
	public void setSelected(int id, int x, int y) {
	}

	@Override
	public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean mouseOver) {
		try {
			String username = account.split(":")[0];
			String password = account.split(":")[1];
			if (resourceLocation == null) {
				this.resourceLocation = AbstractClientPlayer.getLocationSkin(username);
		        AbstractClientPlayer.getDownloadImageSkin(this.resourceLocation, username);
			}
			Minecraft.getMinecraft().fontRendererObj.drawString(username, x + 34, y + 2, mouseOver ? 16777120 : 0xFFFFFFFF);
			Minecraft.getMinecraft().fontRendererObj.drawString(password.replaceAll(".", "*"), x + 34, y + 12, mouseOver ? 16777120 : 0xFFFFFFFF);
			
			Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
			GlStateManager.enableTexture2D();
			RenderUtils.drawTextureRect(x, y, 32, 32, 8F / 64F, 8F / 64F, 16F / 64F, 16F / 64F);
		} catch (Exception e) {
			Minecraft.getMinecraft().fontRendererObj.drawString("Parsing Error", x + 2, y + 2, 0xFFFF0000);
		}
	}

	@Override
	public boolean mousePressed(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		list.setSelected(slotIndex);
		return false;
	}

	@Override
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
		
	}

	public String getAccount() {
		return account;
	}

	public String getUsername() {
		return account.split(":")[0];
	}
	
	public String getPassword() {
		return account.split(":")[1];
	}
	
}
