package net.halalaboos.huzuni.api.settings;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.JsonObject;

/**
 * Base node class which is used throughout the application. Each node is saveable, named, described, and can become parents/children of other nodes.
 * */
public class Node implements Nameable, Saveable {
	
	private final List<Node> children = new ArrayList<Node>();
	
	private String name, description;
	
	public Node(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	@Override
	public void save(JsonObject object) throws IOException {
		for (Node child : children)
			child.save(object);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		for (Node child : children)
			child.load(object);
	}
	
	public boolean isObject(JsonObject object) {
		return object.get(name) != null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

	public List<Node> getChildren() {
		return children;
	}
	
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	public boolean addChildren(Node... node) {
		return children.addAll(Arrays.asList(node));
	}

	public boolean containsChild(Node node) {
		return children.contains(node);
	}

	public boolean removeChild(Node node) {
		return children.remove(node);
	}

}
