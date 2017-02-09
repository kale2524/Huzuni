package net.halalaboos.huzuni.api.gui.widget;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.Minecraft;

/**
 * Widgets are movable menus that have their own settings and display their own custom information.
 * */
public abstract class Widget extends Node {
	
	protected static final Minecraft mc = Minecraft.getMinecraft();
	
	protected static final Huzuni huzuni = Huzuni.INSTANCE;
	
	protected final WidgetManager menuManager;
		
	protected Theme theme;
	
	protected int x, y, width, height;
	
	protected int offsetX, offsetY;
	
	protected boolean enabled = false, dragging = false;
	
	protected Glue oldGlue = ScreenGlue.NONE, glue = ScreenGlue.NONE;
		
	public Widget(String name, String description, WidgetManager menuManager) {
		super(name, description);
		this.setWidth(100);
		this.setHeight(12);
		this.menuManager = menuManager;
	}

	/**
	 * @return True if the given position is within this widget.
	 * */
	public boolean isPointInside(int x, int y) {
		return x >= this.x && x <= this.x + this.width && y >= this.y && y <= this.y + this.height;
	}

	/**
	 * Updates the widgets position, dragging information, and glue information.
	 * */
	public void update() {
		if (Mouse.isButtonDown(0)) {
			int dx = GLManager.getMouseDX(), dy = GLManager.getMouseDY();
			if (dragging) {
				this.x += dx;
				this.y += dy;
				ScreenGlue.keepWithinScreen(this);
				menuManager.formatWidgets(this);
				Glue newGlue = ScreenGlue.getScreenGlue(this);
				WidgetGlue widgetGlue = menuManager.calculateWidgetGlue(this);
				if (widgetGlue != null) {
					newGlue = widgetGlue;
				}
				if (!newGlue.equals(oldGlue)) {
					updateGlue(oldGlue, newGlue);
					this.oldGlue = glue;
				}
				this.glue = newGlue;
			}
		} else {
			this.dragging = false;
		}
	}

	/**
	 * Applies the glue to this widget only if not dragging the widget.
	 * */
	public void useGlue() {
		if (!dragging) {
			glue.formatX(this);
			glue.formatY(this);
			ScreenGlue.keepWithinScreen(this);
		}
	}

	@Deprecated
	protected void updateGlue(Glue oldGlue, Glue newGlue) {
		
	}

	/**
	 * Invoked when the mouse button is clicked.
	 * */
	public boolean mouseClicked(int x, int y, int buttonId) {
		boolean returnValue = dragging = isPointInside(x, y);
		if (returnValue) {
			offsetX = x - this.x;
			offsetY = y - this.y;
		}
		return returnValue;
	}

	/**
	 * Invoked when rendering the menu. This is where the custom information is displayed.
	 * */
	public abstract void renderMenu(int x, int y, int width, int height);

	/**
	 * Invoked when the keyboard is typed.
	 * */
	public void keyTyped(int keyCode) {}

	/**
	 * Invoked when loading the widget.
	 * */
	public void load() {}

	/**
	 * Invoked when initializing the widget.
	 * */
	public void init() {}

	public int getX() {
		return x;
	}

	public Widget setX(int x) {
		this.x = x;
		return this;
	}

	public int getY() {
		return y;
	}

	public Widget setY(int y) {
		this.y = y;
		return this;
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

	public boolean isDragging() {
		return dragging;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public Widget setEnabled(boolean enabled) {
		this.enabled = enabled;
		return this;
	}

	public Glue getGlue() {
		return glue;
	}

	public Widget setGlue(Glue glue) {
		this.glue = glue;
		return this;
	}
	
	public Theme getTheme() {
		return theme;
	}

	public void setTheme(Theme theme) {
		this.theme = theme;
	}
	
	@Override
	public boolean isObject(JsonObject object) {
		JsonElement name = object.get("name");
		return name == null ? false : name.getAsString().equals(getName());
	}
	
	@Override
	public void load(JsonObject object) throws IOException {
		super.load(object);
		if (isObject(object)) {
			setEnabled(object.get("enabled").getAsBoolean());
			setX(object.get("x").getAsInt());
			setY(object.get("y").getAsInt());
			String glue = object.get("glue").getAsString();
			if (!WidgetGlue.loadGlue(this, menuManager, glue))
				setGlue(ScreenGlue.load(object.get("glue").getAsString()));
		}
	}

	@Override
	public void save(JsonObject object) throws IOException {
		super.save(object);
		object.addProperty("name", getName());
		object.addProperty("enabled", isEnabled());
		object.addProperty("x", x);
		object.addProperty("y", y);
		if (glue instanceof ScreenGlue)
			object.addProperty("glue", ((ScreenGlue) glue).name());
		else if (glue instanceof WidgetGlue) {
			object.addProperty("glue", WidgetGlue.getName(((WidgetGlue) glue)));
		}
	}	
	
}
