package net.halalaboos.huzuni.gui.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.halalaboos.huzuni.gui.SettingsMenu;
import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.renderer.GlStateManager;

public class HuzuniSettingsMenu extends HuzuniScreen {

	private final WidgetManager menuManager;
	
	private final SettingsMenu settingsMenu;
	
	public HuzuniSettingsMenu(WidgetManager menuManager, SettingsMenu settingsMenu) {
		super();
		this.menuManager = menuManager;
		this.settingsMenu = settingsMenu;
		this.settingsMenu.resetExpansion();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		menuManager.render();
		if (!settingsMenu.isExpanded()) {
			menuManager.renderWidgetOutlines();
			menuManager.renderTooltip(mouseX, mouseY);
		}
		settingsMenu.renderMenu(width, height, 30, 1);
		GLManager.update();
		GlStateManager.disableBlend();
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		settingsMenu.mouseClicked(mouseX, mouseY, mouseButton);
		if (!settingsMenu.isExpanded())
			menuManager.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (keyCode != Keyboard.KEY_ESCAPE)
			settingsMenu.keyTyped(keyCode, typedChar);
		if (!settingsMenu.isExpanded() && keyCode != Keyboard.KEY_ESCAPE)
			menuManager.keyTyped(keyCode);
	}
	
	@Override
	public void updateScreen() {
		settingsMenu.update();
		menuManager.update();
	}
	
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		if (Mouse.getEventButton() == -1) {
			int wheel = Mouse.getEventDWheel();
			if (wheel > 0)
				wheel = 1;
			else if (wheel < 0)
				wheel = -1;
			if (wheel != 0)
				settingsMenu.mouseWheel(wheel);
		}
	}
	
	@Override
    public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		huzuni.guiManager.save();
		huzuni.modManager.save();
		huzuni.settings.save();
    }
	
}
