package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.ItemSelector;
import net.halalaboos.huzuni.api.settings.ItemSelector.ItemData;

public class ItemSelectorComponent extends NodeTreeComponent <ItemSelector<?>> {
	
	public static final int TEXT_HEIGHT = 12;
	
	public ItemSelectorComponent(ItemSelector<?> node) {
		super(node);
		calculateSize(0);
	}

	@Override
	public void render(Theme theme, boolean mouseOver, int x, int y, int width, int height) {
		calculateSize(width);
		super.render(theme, mouseOver, x, y, width, height);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		int originalX = x;
		y += 13;
		for (ItemData<?> itemData : node.getItemDatas()) {
			if (x + 21 >= originalX + width) {
				x = originalX;
				y += 21;
			}
			if (mouseX > x && mouseX < x + 21 && mouseY > y && mouseY < y + 21) {
				itemData.toggle();
				break;
			}
			x += 21;
		}
	}
	
	@Override
	public String getTooltip() {
		return null;
	}
	
	private void calculateSize(int width) {
		this.setHeight(TEXT_HEIGHT + (int) (Math.ceil(node.getItemDatas().size() / Math.floor(width / 21D)) * 21D + 1));
	}
}
