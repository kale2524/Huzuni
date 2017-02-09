package net.halalaboos.huzuni.api.gui.components.tree;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.Node;

public abstract class NodeTreeComponent <N extends Node> {

	public static final int PADDING = 1;
	
	protected final List<NodeTreeComponent<?>> children = new ArrayList<NodeTreeComponent<?>>();
	
	protected final N node;
	
	protected int height, expandedHeight;
	
	protected boolean expanded = false, selected = false;

	public NodeTreeComponent(N node) {
		this.node = node;
		loadChildren();
	}
	
	public void keyTyped(char typedChar, int keyCode) {}
	
	public void render(Theme theme, boolean mouseOver, int x, int y, int width, int height)  {
		theme.renderNode(this, mouseOver, x, y, width, height);
	}
	
	public abstract void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId);

	public void update() {
		this.expandedHeight = height + PADDING;
		for (NodeTreeComponent<?> child : children) {
			child.update();
			expandedHeight += (child.isExpanded() ? child.getExpandedHeight() : child.getHeight()) + PADDING;
		}
	}
	
	protected void loadChildren() {
		children.clear();
		if (this.node instanceof Node) {
			List<Node> children = ((Node) this.node).getChildren();
			for (Node child : children) {
				NodeTreeComponent<?> component = TreeComponentFactory.getComponent(child);
				if (component != null)
					this.children.add(component);
			}
		}
	}
	
	public String getTooltip() {
		return node.getDescription();
	}
	
	public N getNode() {
		return node;
	}
	
	public boolean isExpanded() {
		return expanded;
	}
	
	public int getHeight() {
		return height;
	}

	protected void setHeight(int height) {
		this.height = height;
	}

	public int getExpandedHeight() {
		return expandedHeight;
	}

	public List<NodeTreeComponent<?>> getChildren() {
		return children;
	}
	
	public boolean isExpandable() {
		return children.size() > 0;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
}
