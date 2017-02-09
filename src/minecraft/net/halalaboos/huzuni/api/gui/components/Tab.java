package net.halalaboos.huzuni.api.gui.components;

import net.halalaboos.huzuni.api.gui.Container;
import net.halalaboos.huzuni.api.settings.Nameable;

/**
 * Tab item used in the {@link TabComponent}. provides a {@link Container} for render when selected.
 * */
public class Tab implements Nameable {
	
	private final String name, description;
	
	private Container container;
	
	public Tab(String name, String description, Container container) {
		this.name = name;
		this.description = description;
		this.container = container;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
}
