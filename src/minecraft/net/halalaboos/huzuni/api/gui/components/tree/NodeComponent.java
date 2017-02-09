package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.Node;

public class NodeComponent extends NodeTreeComponent<Node> {

	public NodeComponent(Node node) {
		super(node);
		this.setHeight(12);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		expanded = !expanded;
	}

}
