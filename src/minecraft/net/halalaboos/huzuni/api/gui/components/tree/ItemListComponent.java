package net.halalaboos.huzuni.api.gui.components.tree;

import org.lwjgl.input.Mouse;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.ItemList;
import net.halalaboos.huzuni.api.settings.Nameable;
import net.halalaboos.huzuni.api.util.render.GLManager;

public class ItemListComponent extends NodeTreeComponent <ItemList> {

	public static final int TEXT_HEIGHT = 12;
	
	private int movingIndex = -1, newIndex = -1, clickOffset = 0;
	
	private String tooltip = null;
	
	private boolean internalExpanded = true;
	
	public ItemListComponent(ItemList node) {
		super(node);
		internalExpanded = !node.isExpandable();
	}

	@Override
	public void render(Theme theme, boolean mouseOver, int x, int y, int width, int height) {		
		int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
		if (internalExpanded) {
			this.tooltip = null;
			if (movingIndex == -1) {
				for (int i = 0; i < node.size(); i++) {
					Nameable nameable = node.get(i);
					boolean mouseOverItem = mouseOver && mouseX > x && mouseX < x + width && mouseY > y + ItemListComponent.TEXT_HEIGHT + i * ItemListComponent.TEXT_HEIGHT && mouseY < y + ItemListComponent.TEXT_HEIGHT * 2 + i * ItemListComponent.TEXT_HEIGHT;
					if (mouseOverItem) {
						this.tooltip = nameable.getDescription();
						break;
					}
				}
			}
			if (node.isOrdered()) {
				if (movingIndex != -1) {
					for (int i = 0; i < node.size(); i++) {
						int intersection = (y + TEXT_HEIGHT * 2 + i * TEXT_HEIGHT) - getMovingY(y, y + height);
						if (intersection > TEXT_HEIGHT / 2) {
							newIndex = i;
							break;
						}
					}
					if (newIndex != -1 && !Mouse.isButtonDown(0)) {
						moveItem(newIndex, movingIndex);
						movingIndex = -1;
						newIndex = -1;
					}
				}
			}
		}
		super.render(theme, mouseOver, x, y, width, height);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		if (node.isExpandable() && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + TEXT_HEIGHT) {
			internalExpanded = !internalExpanded;
			return;
		}
		if (internalExpanded) {
			if (node.isOrdered()) {
				for (int i = 0; i < node.size(); i++) {
					if (mouseX > x && mouseX < x + width && mouseY > y + TEXT_HEIGHT + i * TEXT_HEIGHT && mouseY < y + TEXT_HEIGHT * 2 + i * TEXT_HEIGHT) {
						clickOffset = mouseY - (y + TEXT_HEIGHT + i * TEXT_HEIGHT);
						this.movingIndex = i;
						break;
					}
				}
			}
		}
	}
	
	@Override
	public int getHeight() {
		return internalExpanded ? (node.size() * TEXT_HEIGHT + TEXT_HEIGHT) : TEXT_HEIGHT;
	}
	
	@Override
	public String getTooltip() {
		return this.tooltip != null ? this.tooltip : node.getDescription();
	}

	private void moveItem(int newIndex, int oldIndex) {
		Nameable nameable = node.get(oldIndex);
		node.remove(oldIndex);
		node.add(newIndex, nameable);
	}
	
	private int getMovingY(int y, int y1) {
		int mouseY = GLManager.getMouseY() - clickOffset;
		if (mouseY - TEXT_HEIGHT < y)
			mouseY = y + TEXT_HEIGHT;
		if (mouseY + TEXT_HEIGHT > y1)
			mouseY = y1 - TEXT_HEIGHT;
		return mouseY;
	}
	
	public int[] getMovingPosition(int x, int y, int width, int height) {
		int movingY = getMovingY(y, y - PADDING + height);
		return new int[] { x, 
				movingY, 
				width, 
				TEXT_HEIGHT 
				};
	}
	
	public int[] getNewPosition(int x, int y, int width, int height) {
		return new int[] {
				x, 
				y + TEXT_HEIGHT + newIndex * TEXT_HEIGHT, 
				width, 
				TEXT_HEIGHT
		};
	}
	
	public boolean hasNewPosition() {
		return newIndex != -1;
	}
	
	public boolean hasMovingPosition() {
		return movingIndex != -1;
	}

	public boolean isInternalExpanded() {
		return internalExpanded;
	}

	public int getMovingIndex() {
		return movingIndex;
	}

	public int getNewIndex() {
		return newIndex;
	}
}
