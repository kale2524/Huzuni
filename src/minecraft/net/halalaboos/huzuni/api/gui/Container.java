package net.halalaboos.huzuni.api.gui;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.api.util.render.GLManager;

/**
 * Component that holds and manages other components
 * */
public class Container extends Component {
	
	private final Timer tooltipTimer = new Timer();
	
	private final List<Component> components = new ArrayList<Component>();
		
	private Component selectedComponent = null;
	
	public Container(int offsetX, int offsetY, int width, int height) {
		super(offsetX, offsetY, width, height);
	}
	
	@Override
	public void render() {
		super.render();
		this.setTooltip(null);
		for (Component component : components) {
			component.updatePositionsWithOffset(getX(), getY());
			component.update();
			component.render();
			if (component.isPointInside(GLManager.getMouseX(), GLManager.getMouseY()) && component.hasTooltip()) {
				this.setTooltip(component.getTooltip());
			}
		}
		if (getTooltip() != null) {
			if (tooltipTimer.hasReach(1000))
				getTheme().drawTooltip(getTooltip(), GLManager.getMouseX(), GLManager.getMouseY());
		} else
			tooltipTimer.reset();
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		if (hasSelected())
			selectedComponent.keyTyped(typedChar, keyCode);
	}
	
	@Override
	public void update() {
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int buttonId) {
		updateSelectedComponent(null);
		for (int i = components.size() - 1; i >= 0; i--) {
			Component component = components.get(i);
			if (component.isPointInside(mouseX, mouseY)) {
				updateSelectedComponent(component);
				component.mouseClicked(mouseX, mouseY, buttonId);
				components.remove(component);
				components.add(component);
				break;
			}
		}
	}
	
	@Override
	public void mouseWheel(int amount) {
		for (int i = components.size() - 1; i >= 0; i--) {
			Component component = components.get(i);
			if (component.isPointInside(GLManager.getMouseX(), GLManager.getMouseY())) {
				component.mouseWheel(amount);
			}
		}
	}
	
	private void updateSelectedComponent(Component component) {
		if (selectedComponent != null && selectedComponent != component)
			selectedComponent.setSelected(false);
		if (component != null) {
			selectedComponent = component;
			selectedComponent.setSelected(true);
		}
	}
	
	public boolean hasSelected() {
		return selectedComponent != null;
	}
	
	public void add(Component component) {
		components.add(component);
		component.setTheme(getTheme());
	}
	
	public void remove(Component component) {
		components.add(component);
	}
	
	public void clear() {
		components.clear();
	}

	@Override
	public void setTheme(Theme theme) {
		super.setTheme(theme);
		for (Component component : components) {
			component.setTheme(theme);
		}
	}
}
