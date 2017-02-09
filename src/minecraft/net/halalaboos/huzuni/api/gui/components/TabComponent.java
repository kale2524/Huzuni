package net.halalaboos.huzuni.api.gui.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a list of {@link Tab}s and renders and handles them.
 * */
public class TabComponent extends ScrollableComponent {
	
	private final List<Tab> tabs = new ArrayList<Tab>();
	
	private int selected = 0;
	
	private int padding = 1;
	
	public TabComponent(int offsetX, int offsetY, int width, int height) {
		super(offsetX, offsetY, width, height);
		this.setVerticalScrollbar(false);
	}

	@Override
	protected int calculateTotalAreaLength() {
		int width = 0;
		for (Tab tab : tabs) {
			width += getTheme().getStringWidth(tab.getName()) + 4 + padding;
		}
		return width;
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int buttonId) {
		super.mouseClicked(mouseX, mouseY, buttonId);
		if (isPointInsideRenderArea(mouseX, mouseY)) {
			int xIncrement = -getScrollOffset();
			for (int i = 0; i < tabs.size(); i++) {
				Tab tab = tabs.get(i);
				int tabWidth = getTabWidth(tab);
				if (isPointInsideTab(mouseX, mouseY, xIncrement, tabWidth)) {
					selected = i;
					onTabSelected(tab, i);
					break;
				}
				xIncrement += tabWidth;
			}
		}
	}
	
	/*public void renderTabs() {
		int xIncrement = -getScrollOffset();
		for (int i = 0; i < tabs.size(); i++) {
			Tab tab = tabs.get(i);
			int tabWidth = getTabWidth(tab);
			int[] tabArea = getTabArea(xIncrement, tabWidth);
			//GLManager.glColor(selected == i ? Huzuni.INSTANCE.settings.menuColor.getColor() : defaultColor);
			//renderRectangle(tabArea);
			//drawString(tab.getName(), component.getX() + tabWidth / 2 + xIncrement - getStringWidth(tab.getName()) / 2, component.getY() + tabArea[3] / 2 - 2 - getStringHeight(tab.getName()) / 2, 0xFFFFFFFF);
			xIncrement += tabWidth + padding;
		}
	}*/
	
	/**
	 * @return True if the given mouse coordinates are within the {@link Tab} associated with the x coordinate and width.
	 * */
	public boolean isPointInsideTab(int mouseX, int mouseY, int xIncrement, int tabWidth) {
		return mouseX > getX() + xIncrement && mouseY > getY() && mouseX < getX() + xIncrement + tabWidth && mouseY < getY() + getRenderArea()[3] - (hasScrollbar() ? padding : 0);
	}
	
	/**
	 * @return An array of coordinate information associated with the x coordinate and width.
	 * */
	public int[] getTabArea(int xIncrement, int tabWidth) {
		return new int[] {
				getX() + xIncrement,
				getY(),
				tabWidth,
				getRenderArea()[3] - (hasScrollbar() ? padding : 0)
		};
	}
	
	/**
	 * @return The width associated with the given {@link Tab}
	 * */
	public int getTabWidth(Tab tab) {
		return hasScrollbar() ? getTheme().getStringWidth(tab.getName()) + 4 : ((getWidth() - ((tabs.size() - 2) * padding)) / tabs.size());
	}
	
	/**
	 * Invoked when a {@link Tab} is selected.
	 * */
	public void onTabSelected(Tab tab, int index) {}
	
	public int getSelected() {
		return selected;
	}
	
	public Tab getSelectedTab() {
		return tabs.get(selected);
	}
	
	public void add(Tab tab) {
		tabs.add(tab);
	}
	
	public void remove(Tab tab) {
		tabs.remove(tab);
	}

	public List<Tab> getTabs() {
		return tabs;
	}

	/**
	 * @return True if a {@link Tab} has been selected.
	 * */
	public boolean hasSelectedTab() {
		return selected >= 0 && selected < tabs.size();
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}
}
