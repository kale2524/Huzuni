package net.halalaboos.huzuni.api.gui.components.tree;

import java.util.HashMap;
import java.util.Map;

import net.halalaboos.huzuni.api.settings.Node;

/**
 * Allows users to register {@link Node} classes with their corresponding {@link NodeTreeComponent} classes.
 * */
public final class TreeComponentFactory {
	
	private static final Map<Class<? extends Node>, Class<? extends NodeTreeComponent<?>>> componentMap = new HashMap<Class<? extends Node>, Class<? extends NodeTreeComponent<?>>>();
	
	private TreeComponentFactory() {
		
	}
	
	/**
	 * Creates the {@link NodeTreeComponent} associated with the {@link Node} class provided.
	 * <br>
	 * Note that this 
	 * @param baseClass The base {@link Node} class.
	 * @param node The {@link Node} object.
	 * */
	@SuppressWarnings("unchecked")
	public static <C extends NodeTreeComponent<?>> C getComponent(Node node) {
		for (Class<? extends Node> nodeClass : componentMap.keySet()) {
			if (nodeClass.isAssignableFrom(node.getClass())) {
				try {
					return (C) componentMap.get(nodeClass).getConstructor(nodeClass).newInstance(node);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return (C) new NodeComponent(node);
	}
	
	/**
	 * Adds a {@link Node} class file into a map with it's corresponding {@link Component} class.
	 * @param nodeClass The {@link Node} class.
	 * @param componentClass The {@link NodeTreeComponent} class.
	 * */
	public static void addComponent(Class<? extends Node> nodeClass, Class<? extends NodeTreeComponent<?>> componentClass) {
		componentMap.put(nodeClass, componentClass);
	}
	
}
