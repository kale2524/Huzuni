package net.halalaboos.huzuni.api.gui.containers;

import net.halalaboos.huzuni.api.gui.Component;
import net.halalaboos.huzuni.api.gui.Container;

/**
 * Holds a single {@link Component} and updates it's positions to fill the {@link Container} entirely.
 * */
public class SingleComponentContainer <T extends Component> extends Container {

	private T component;
	
	private int padding;
	
	public SingleComponentContainer(int offsetX, int offsetY, int width, int height, T component, int padding) {
		super(offsetX, offsetY, width, height);
		this.component = component;
		this.padding = padding;
		if (component != null) {
			component.setOffsetX(padding);
			component.setOffsetY(padding);
			component.setWidth(width - padding * 2);
			component.setHeight(height - padding * 2);
			this.add(component);
		}
	}

	@Override
	public void render() {
		if (component != null) {
			component.setOffsetX(padding);
			component.setOffsetY(padding);
			component.setWidth(getWidth() - padding * 2);
			component.setHeight(getHeight() - padding * 2);
		}
		super.render();
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public T getComponent() {
		return component;
	}

	public void setComponent(T component) {
		this.component = component;
	}
	
}
