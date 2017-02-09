package net.halalaboos.huzuni.api.gui;

/**
 * Base component class that can be held within a container.
 * */
public class Component {

	private int x, y, width, height;
	
	private int offsetX, offsetY;
	
	private boolean selected = false;
	
	private String tooltip = null;
	
	private Theme theme;
	
	public Component(int offsetX, int offsetY, int width, int height) {
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.width = width;
		this.height = height;
	}

	/**
	 * Renders the component using the theme.
	 * */
	public void render() {
		theme.renderComponent(this);
	}

	/**
	 * Invoked when the keyboard is typed.
	 * */
	public void keyTyped(char typedChar, int keyCode) {
		
	}

	/**
	 * Invoked before rendering to update anything.
	 * */
	public void update() {
		
	}

	/**
	 * Invoked when the mouse is clicked.
	 * */
	public void mouseClicked(int mouseX, int mouseY, int buttonId) {
		
	}

	/**
	 * Invoked when the mouse wheel moves.
	 * */
	public void mouseWheel(int amount) {
		
	}

	/**
	 * @return True if the given position is within the component.
	 * */
	public boolean isPointInside(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}

	/**
	 * @return True if the given position is within the rectangle provided.
	 * */
	public boolean isPointInside(int x, int y, int[] rect) {
		return x >= rect[0] && y >= rect[1] && x <= rect[0] + rect[2] && y <= rect[1] + rect[3];
	}
	
	public String getTooltip() {
		return tooltip;
	}
	
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * Updates the components position with the x, y provided and keeps the offset that has been applied to this component by it's container.
	 * */
	public void updatePositionsWithOffset(int x, int y) {
		this.x = x + offsetX;
		this.y = y + offsetY;
	}
	
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getOffsetX() {
		return offsetX;
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
		this.x = offsetX;
	}

	public int getOffsetY() {
		return offsetY;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
		this.y = offsetY;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}
	
	public boolean hasTooltip() {
		return tooltip != null;
	}
}
