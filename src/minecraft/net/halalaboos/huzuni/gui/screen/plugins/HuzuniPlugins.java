package net.halalaboos.huzuni.gui.screen.plugins;

import java.io.IOException;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import net.halalaboos.huzuni.api.gui.components.tree.NodeTree;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTreeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.TreeComponentFactory;
import net.halalaboos.huzuni.gui.containers.SettingsContainer;
import net.halalaboos.huzuni.api.plugin.Plugin;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.gui.BasicTheme;
import net.halalaboos.huzuni.gui.screen.HuzuniScreen;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;

public class HuzuniPlugins extends HuzuniScreen {

	private final SettingsContainer container = new SettingsContainer(0, 0, 0, 0);
		
	private final GuiScreen parent;
	
	private PluginSelectionList selectionList;
			
	private GuiButton load;
	
	private NodeTree tree;
			
	public HuzuniPlugins(GuiScreen parent) {
		super();
		this.parent = parent;
		this.container.setTheme(new BasicTheme());
	}
	
	@Override
	public void initGui() {
		buttonList.clear();
		this.selectionList = new PluginSelectionList(this, this.mc, 200, this.height, 32, this.height - 64, 24);
		this.selectionList.setSlotXBoundsFromLeft(2);
		this.selectionList.setPluginDatas(huzuni.pluginManager.getPluginDatas());
    	
        buttonList.add(new GuiButton(2, this.width / 2 - 154, this.height - 28, 308, 20, "Done"));
        buttonList.add(load = new GuiButton(1, this.width / 2 + 2, this.height - 52, 152, 20, "Load"));
        buttonList.add(new GuiButton(0, this.width / 2 - 154, this.height - 52, 152, 20, "Folder.."));

        load.enabled = selectionList.hasSelected();
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (!button.enabled)
			return;
		switch (button.id) {
		case 0:
			OpenGlHelper.openFile(huzuni.pluginManager.getPluginFolder());
			break;
		case 1:
			Plugin plugin = huzuni.pluginManager.getPlugin(selectionList.getSelected().getPluginData());
			if (plugin != null) {
				if (plugin.isLoaded())
					plugin.unloadPlugin();
				else
                    plugin.loadPlugin();
            }
			break;
		case 2:
			mc.displayGuiScreen(parent);
			break;
		case 3:
			break;
		default:
			break;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		selectionList.drawScreen(mouseX, mouseY, partialTicks);
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.drawCenteredString(fontRenderer, "Plugins", width / 2, 10, 0xFFFFFFFF);
		load.enabled = selectionList.hasSelected();
		
		if (load.enabled) {
			Plugin plugin = huzuni.pluginManager.getPlugin(selectionList.getSelected().getPluginData());
			if (plugin != null)
				load.displayString = plugin.isLoaded() ? "Unload" : "Load";
			else
				load.displayString = "Unable to load!";
		}
		
        if (selectionList.hasSelected()) {
            this.fontRenderer.drawStringWithShadow("Version: " + selectionList.getSelected().getPluginData().getVersion(), 208, 40, 0xFFFFFF);
        	this.fontRenderer.drawStringWithShadow("Description: " + selectionList.getSelected().getPluginData().getDescription(), 208, 50, 0xFFFFFF);
        	renderContainer(208, 62, width - 208 - 12, height - 62 - 64);
        }
        
	}
	
	private void renderContainer(int x, int y, int width, int height) {
        container.setX(x);
		container.setY(y);
		container.setWidth(width);
		container.setHeight(height);
		if (tree != null) {
			tree.setOffsetX(1);
			tree.setOffsetY(1);
			tree.setWidth(width - 2);
			tree.setHeight(height - 2);
		}
		container.render();
	}
	
	@Override
	public void handleMouseInput() throws IOException {
        super.handleMouseInput();
		selectionList.handleMouseInput();
		if (Mouse.getEventButton() == -1) {
			int wheel = Mouse.getEventDWheel();
			if (wheel > 0)
				wheel = 1;
			else if (wheel < 0)
				wheel = -1;
			if (wheel != 0)
				container.mouseWheel(wheel);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		selectionList.mouseClicked(mouseX, mouseY, mouseButton);
		container.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
		if (keyCode != Keyboard.KEY_ESCAPE)
			container.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		selectionList.mouseReleased(mouseX, mouseY, state);
	}
	
	@Override
	public void updateScreen() {
		super.updateScreen();
		container.update();
	}
	
	public void updateContainer() {
		if (selectionList.hasSelected()) {
			Plugin plugin = huzuni.pluginManager.getPlugin(selectionList.getSelected().getPluginData());
			if (plugin != null) {
				container.clear();
				tree = new NodeTree(0, 0, 0, 0);
				for (Node node : plugin.getPluginData().getChildren()) {
					NodeTreeComponent<?> component = TreeComponentFactory.getComponent(node);
					if (component != null)
						tree.add(component);
				}
				container.add(tree);
			}
		}
	}
	
}
