package net.halalaboos.huzuni.mc;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.gui.screen.HuzuniSettingsMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;

public class HuzuniIngameGui extends GuiIngame {

	private final Huzuni huzuni = Huzuni.INSTANCE;
	
	private final Minecraft mc;
		
	public HuzuniIngameGui(Minecraft mc) {
		super(mc);
		this.mc = mc;
		this.persistantChatGUI = new GuiHuzuniChat(mc);
	}

	public void renderGameOverlay(float partialTicks) {
		super.renderGameOverlay(partialTicks);
		if (!mc.gameSettings.showDebugInfo) {
			if (!(mc.currentScreen instanceof HuzuniSettingsMenu)) {
				huzuni.guiManager.widgetManager.render();
		        GlStateManager.disableBlend();
			}
			huzuni.renderManager.renderOverlay(partialTicks);
		}
	}

}
