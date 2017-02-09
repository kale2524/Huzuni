package net.halalaboos.huzuni.api.gui.components.tree;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.gui.components.ScrollableComponent;
import net.halalaboos.huzuni.api.util.render.GLManager;

public class NodeTree extends ScrollableComponent {
	
	private final List<NodeTreeComponent<?>> treeComponents = new ArrayList<NodeTreeComponent<?>>();
	
	private int indexAmount = 8;
	
	private NodeTreeComponent<?> selectedComponent = null;
	
	public NodeTree(int offsetX, int offsetY, int width, int height) {
		super(offsetX, offsetY, width, height);
		this.setVerticalScrollbar(true);
	}
	
	@Override
	public void render() {
		super.render();
	}
	
	public void renderTree() {
		int mouseX = GLManager.getMouseX(), 
				mouseY = GLManager.getMouseY(), 
				width = this.getWidth() - (this.hasScrollbar() ? this.getScrollbarSize() + NodeTreeComponent.PADDING : 0), 
				y = this.getY() - getScrollOffset();
		this.setTooltip(null);
		for (int i = 0; i < treeComponents.size(); i++) {
			NodeTreeComponent<?> component = treeComponents.get(i);
			boolean mouseOver = mouseX > this.getX() && mouseX < this.getX() + width && mouseY > y && mouseY < y + component.getHeight();
			boolean withinRenderArea = isWithinRenderArea(y, y + component.getHeight());
			component.update();
			if (withinRenderArea) {
				component.render(getTheme(), mouseOver, this.getX(), y, width, component.getHeight());
				if (mouseOver)
					this.setTooltip(component.getTooltip());
			}
			if (component.isExpanded()) {
				getTheme().drawLine(this.getX() + indexAmount / 2, y + component.getHeight() + NodeTreeComponent.PADDING, this.getX() + indexAmount / 2, y + component.getExpandedHeight() - NodeTreeComponent.PADDING, true);
				renderChildren(this.getX() + indexAmount, y + component.getHeight() + NodeTreeComponent.PADDING, width - indexAmount, mouseX, mouseY, 0, component);
				y += component.getExpandedHeight();
			} else
				y += component.getHeight() + NodeTreeComponent.PADDING;
		}
	}
	
	/**
	 * Renders the children of the given {@link NodeTreeComponent}. It is a recursive function.
	 * @param x The x position to be rendered within.
	 * @param y The y position to be rendered within. This has been incremented by the previous {@link NodeTreeComponent}s heights.
	 * @param width The greater x position to be rendered within.
	 * @param y1 The greater y position to be rendered within.
	 * @param originalY The original Y position the components are rendered within. This is because the render function increments the y field by the heights of other {@link NodeTreeComponent}s.
	 * @param mouseX The X position of the mouse.
	 * @param mouseY The Y position of the mouse.
	 * @param height The height of the previous {@link NodeTreeComponent}s.
	 * @param component The component to be rendered.
	 * */
	private void renderChildren(int x, int y, int width, int mouseX, int mouseY, int height, NodeTreeComponent<?> component) {
		for (NodeTreeComponent<?> child : component.getChildren()) {
			boolean mouseOver = mouseX > x && mouseX < x + width && mouseY > y + height && mouseY < y + height + child.getHeight();
			boolean withinRenderArea = isWithinRenderArea(y + height, y + height + child.getHeight());
			child.update();
			if (withinRenderArea) {
				child.render(getTheme(), mouseOver, x, y + height, width, child.getHeight());	
				if (mouseOver)
					setTooltip(child.getTooltip());	
			}
			if (child.isExpanded()) {
				getTheme().drawLine(x + indexAmount / 2, y + child.getHeight() + NodeTreeComponent.PADDING, x + indexAmount / 2, y + child.getExpandedHeight() - NodeTreeComponent.PADDING, true);
				renderChildren(x + indexAmount, y, width - indexAmount, mouseX, mouseY, height + child.getHeight() + NodeTreeComponent.PADDING, child);
			}
			height += (child.isExpanded() ? child.getExpandedHeight() : child.getHeight() + NodeTreeComponent.PADDING);
		}
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (selectedComponent != null)
			selectedComponent.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int buttonId) {
		if (isPointInsideRenderArea(mouseX, mouseY)) {
			updateSelected(null);
			int width = this.getWidth() - (this.hasScrollbar() ? this.getScrollbarSize() : 0) - NodeTreeComponent.PADDING, 
					y = this.getY() - getScrollOffset();
			
			for (int i = 0; i < treeComponents.size(); i++) {
				NodeTreeComponent<?> component = treeComponents.get(i);
				if (mouseX > this.getX() && mouseX < this.getX() + width && mouseY > y && mouseY < y + component.getHeight() && isWithinRenderArea(y, y + component.getHeight())) {
					updateSelected(component);
					component.mouseClicked(getTheme(), this.getX(), y, width, component.getHeight(), mouseX, mouseY, buttonId);
					break;
				} else if (component.isExpanded()) {
					boolean childClicked = clickChild(this.getX() + indexAmount, y + component.getHeight() + NodeTreeComponent.PADDING, width - indexAmount, mouseX, mouseY, buttonId, 0, component);
					if (childClicked)
						break;
					else
						y += component.getExpandedHeight();
				} else
					y += component.getHeight() + NodeTreeComponent.PADDING;
			}
		}
		super.mouseClicked(mouseX, mouseY, buttonId);
	}
	
	/**
	 * Invokes the mouse click functions of the children of the given {@link NodeTreeComponent}. It is a recursive function.
	 * @param x The x position to be rendered within.
	 * @param y The y position to be rendered within. This has been incremented by the previous {@link NodeTreeComponent}s heights.
	 * @param width The width of the area.
	 * @param y1 The greater y position to be rendered within.
	 * @param originalY The original Y position the components are rendered within. This is because the mouse click function increments the y field by the heights of other {@link NodeTreeComponent}s.
	 * @param mouseX The X position of the mouse.
	 * @param mouseY The Y position of the mouse.
	 * @param height The height of the previous {@link NodeTreeComponent}s.
	 * @param component The component to be handled.
	 * */
	private boolean clickChild(int x, int y, int width, int mouseX, int mouseY, int buttonId, int height, NodeTreeComponent<?> component) {
		for (NodeTreeComponent<?> child : component.getChildren()) {
			if (mouseX > x && mouseX < x + width && mouseY > y + height && mouseY < y + height + child.getHeight() && isWithinRenderArea(y + height, y + height + child.getHeight())) {
				updateSelected(child);
				child.mouseClicked(getTheme(), x, y + height, width, child.getHeight(), mouseX, mouseY, buttonId);
				return true;
			} else if (child.isExpanded()) {
				boolean childClicked = clickChild(x + indexAmount, y + height, width - indexAmount, mouseX, mouseY, buttonId, height + child.getHeight(), child);
				if (childClicked)
					return true;
				height += child.getExpandedHeight();
			} else
				height += child.getHeight() + NodeTreeComponent.PADDING;
		}
		return false;
	}
	
	/**
	 * Updates the selected {@link NodeTreeComponent} to be either selected or unselected.
	 * @param component The {@link NodeTreeComponent} to be updated.
	 * */
	private void updateSelected(NodeTreeComponent<?> component) {
		if (this.selectedComponent != null && this.selectedComponent != component)
			this.selectedComponent.setSelected(false);
		if (component != null) {
			this.selectedComponent = component;
			this.selectedComponent.setSelected(true);
		}
	}

	@Override
	protected int calculateTotalAreaLength() {
		int length = 0;
		for (int i = 0; i < treeComponents.size(); i++) {
			NodeTreeComponent<?> component = treeComponents.get(i);
			length += (component.isExpanded() ? component.getExpandedHeight() : component.getHeight() + NodeTreeComponent.PADDING);
		}
		return length - NodeTreeComponent.PADDING;
	}

	public boolean add(NodeTreeComponent<?> component) {
		return treeComponents.add(component);
	}

	public boolean remove(NodeTreeComponent<?> component) {
		return treeComponents.remove(component);
	}

	public void clear() {
		treeComponents.clear();
	}
}
