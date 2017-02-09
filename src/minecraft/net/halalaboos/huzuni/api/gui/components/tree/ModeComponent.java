package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.Mode;

public class ModeComponent extends NodeTreeComponent <Mode<?>> {
	
	public static final int TEXT_HEIGHT = 12, CYCLER_HEIGHT = 12;
	
	public ModeComponent(Mode<?> node) {
		super(node);
		setHeight(TEXT_HEIGHT + CYCLER_HEIGHT);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		y += TEXT_HEIGHT;
		if (mouseX > x + width - CYCLER_HEIGHT && mouseX < x + width && mouseY > y && mouseY < y + CYCLER_HEIGHT) {
			node.setSelectedItem(node.getSelected() + 1);
		} else if (mouseX > x && mouseX < x + CYCLER_HEIGHT && mouseY > y && mouseY < y + CYCLER_HEIGHT) {
			node.setSelectedItem(node.getSelected() - 1);
		} else if (buttonId == 0) {
			node.setSelectedItem(node.getSelected() + 1);
		} else if (buttonId == 1) {
			node.setSelectedItem(node.getSelected() - 1);
		}
	}

}
