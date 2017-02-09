package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Widget;

public class WidgetComponent extends NodeTreeComponent <Widget> {

	public WidgetComponent(Widget node) {
		super(node);
		setHeight(12);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		if (node.hasChildren() && (mouseX > x + width - height && mouseX < x + width && mouseY > y && mouseY < y + height || buttonId == 1))
			expanded = !expanded;
		else
			node.setEnabled(!node.isEnabled());
	}

}
