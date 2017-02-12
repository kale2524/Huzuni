package net.halalaboos.huzuni.gui.screen;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import com.matthewhatcher.huzuni.PanoramaRenderer;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.util.render.Texture;
import net.halalaboos.huzuni.gui.screen.account.HuzuniAccounts;
import net.halalaboos.huzuni.gui.screen.plugins.HuzuniPlugins;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class HuzuniMainMenu extends HuzuniScreen {
	
    private final Texture TITLE = new Texture("huzuni/title.png");
    
    private final PanoramaRenderer panoramaRenderer = new PanoramaRenderer(width, height);
    
	public HuzuniMainMenu() {
		super();
	}

	@Override
	public void initGui() {
        panoramaRenderer.init();
        panoramaRenderer.updateSize(width, height);
		int y = this.height / 4 + 48;
		this.buttonList.clear();
		this.buttonList.add(new GuiButton(1, this.width / 2 - 100, y, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, y + 24 * 1, I18n.format("menu.multiplayer", new Object[0])));
        GuiButton pluginButton = new GuiButton(7, this.width / 2 + 2, y + 24 * 2, 98, 20, "Plugins");
        pluginButton.enabled = false;
        this.buttonList.add(pluginButton);
        this.buttonList.add(new GuiButton(3, this.width / 2 - 100, y + 24 * 2, 98, 20, "Accounts"));

		this.buttonList.add(new GuiButton(0, this.width / 2 - 100, y + 72, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, y + 72, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 124, y + 72));
        if (huzuni.settings.hasUpdate()) {
        	this.buttonList.add(new HuzuniLink(6, this.width / 2 - mc.fontRendererObj.getStringWidth(huzuni.settings.getNewestVersion()) / 2, y + 102, mc.fontRendererObj.getStringWidth(huzuni.settings.getNewestVersion()), 15, huzuni.settings.getNewestVersion()));
        }
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
		}

		if (button.id == 5) {
			this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiWorldSelection(this));
		}

		if (button.id == 2) {
			this.mc.displayGuiScreen(new GuiMultiplayer(this));
		}
		
		if (button.id == 3) {
			this.mc.displayGuiScreen(new HuzuniAccounts(this));
		}

		if (button.id == 4) {
			this.mc.shutdown();
		}
		if (button.id == 6) {
	        try {
				Desktop.getDesktop().browse(new URL("https://github.com/MatthewSH/minecraft-Huzuni/releases").toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		
		if (button.id == 7) {
			this.mc.displayGuiScreen(new HuzuniPlugins(this));

		}
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
        panoramaRenderer.panoramaTick();
        if (huzuni.settings.hasUpdate()) {
        	for (GuiButton button : buttonList) {
        		if (button.id == 6) {
        			return;
        		}
        	}
        	this.initGui();
        }
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		GlStateManager.disableAlpha();
		panoramaRenderer.renderSkybox(mouseX, mouseY, partialTicks);
		GlStateManager.enableAlpha();
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		float titleX = width / 2 - 150, titleY = 20;
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		TITLE.render(titleX, titleY + 10, 300, 100);
        this.drawString(fontRendererObj, Huzuni.VERSION, width - fontRendererObj.getStringWidth(Huzuni.VERSION) - 2, height - 12, 0xFFFFFF);
        if (huzuni.settings.hasUpdate())
            this.drawCenteredString(fontRendererObj, "New version available!", width / 2, height / 4 + 142, 0xFFFFFF);
	}
}
