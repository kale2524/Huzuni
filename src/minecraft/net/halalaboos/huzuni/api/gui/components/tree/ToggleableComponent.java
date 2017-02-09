package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.Toggleable;

public class ToggleableComponent extends NodeTreeComponent <Toggleable> {

	public ToggleableComponent(Toggleable node) {
		super(node);
		setHeight(12);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		node.toggle();
	}

}
