package net.halalaboos.huzuni.gui.screen.plugins;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.plugin.PluginData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.text.TextFormatting;

public class PluginSelectionList extends GuiListExtended {

	private final HuzuniPlugins pluginsScreen;
	
	private final List<PluginListEntry> pluginDatas = new ArrayList<PluginListEntry>();
	
	private int selected = -1;
	
	public PluginSelectionList(HuzuniPlugins pluginsScreen, Minecraft mc, int width, int height, int top, int bottom, int itemSize) {
		super(mc, width, height, top, bottom, itemSize);
		this.pluginsScreen = pluginsScreen;
		this.centerListVertically = false;
        this.setHasListHeader(true, (int)((float) mc.fontRenderer.FONT_HEIGHT * 1.5F));
	}

	@Override
	public PluginListEntry getListEntry(int index) {
		return pluginDatas.get(index);
	}

	@Override
	protected int getSize() {
		return pluginDatas.size();
	}
	
	@Override
    protected void drawListHeader(int insideLeft, int insideTop, Tessellator tessellatorIn) {
        String text = TextFormatting.UNDERLINE + "Plugins";
        this.mc.fontRenderer.drawString(text, this.width / 2 - this.mc.fontRenderer.getStringWidth(text) / 2, Math.min(this.top + 3, insideTop), 16777215);
    }

	public void setPluginDatas(List<PluginData> pluginDatas) {
		this.selected = -1;
		this.pluginDatas.clear();
		for (PluginData pluginData : pluginDatas) {
			this.pluginDatas.add(new PluginListEntry(this, pluginData));
		}
	}

	@Override
	protected boolean isSelected(int index) {
		return index == selected;
	}

	public void setSelected(int index) {
		this.selected = index;
		pluginsScreen.updateContainer();
	}
	
	public PluginListEntry getSelected() {
		return hasSelected() ? this.pluginDatas.get(selected) : null;
	}
	
	public boolean hasSelected() {
		return selected >= 0 && selected < pluginDatas.size();
	}
	
	/**
     * Gets the width of the list
     */
	@Override
    public int getListWidth() {
        return this.width;
    }

	@Override
    protected int getScrollBarX() {
        return this.right - 6;
    }
}
