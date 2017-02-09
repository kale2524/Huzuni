package net.halalaboos.huzuni.api.gui.containers;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.gui.Container;
import net.halalaboos.huzuni.api.gui.components.TextField;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTree;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTreeComponent;

/**
 * Basic container with a built in search field and node tree.
 * */
public class NodeTreeSearchContainer extends Container {

	private final List<NodeTreeComponent<?>> nodeTreeComponents = new ArrayList<NodeTreeComponent<?>>();
	
	private final TextField searchField = new TextField("Search", 0, 0, 0) {

		@Override
		public void keyTyped(char typedChar, int keyCode) {
			super.keyTyped(typedChar, keyCode);
			if (this.hasText()) {
				nodeTree.clear();
				for (NodeTreeComponent<?> component : nodeTreeComponents) {
					if (component.getNode().getName().toLowerCase().contains(getText().toLowerCase())) {
						nodeTree.add(component);
					}
				}
			} else {
				nodeTree.clear();
				for (NodeTreeComponent<?> component : nodeTreeComponents) {
					nodeTree.add(component);
				}
			}
		}
	};
	
	private final NodeTree nodeTree = new NodeTree(0, 0, 0, 0);
	
	private int padding = 0;
	
	public NodeTreeSearchContainer(int offsetX, int offsetY, int width, int height) {
		super(offsetX, offsetY, width, height);
		this.add(nodeTree);
		this.add(searchField);
	}
	
	@Override
	public void render() {
		searchField.setOffsetX(padding);
		searchField.setOffsetY(padding);
		searchField.setWidth(getWidth() - padding * 2);
		
		nodeTree.setOffsetX(padding);
		nodeTree.setOffsetY(searchField.getHeight() + padding + 1);
		nodeTree.setWidth(getWidth() - padding * 2);
		nodeTree.setHeight(getHeight() - searchField.getHeight() - padding * 2 - 1);
		super.render();
	}
	
	public void addComponent(NodeTreeComponent<?> component) {
		this.nodeTreeComponents.add(component);
		this.nodeTree.add(component);
	}


}
