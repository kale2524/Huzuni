package net.halalaboos.huzuni.api.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.widget.ScreenGlue;
import net.halalaboos.huzuni.api.gui.widget.Widget;
import net.halalaboos.huzuni.api.gui.widget.WidgetGlue;
import net.halalaboos.huzuni.api.settings.JsonFileHandler;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;

/**
 * Manages widgets and handles their logic/render. Can save or load the widgets.
 */
public class WidgetManager extends JsonFileHandler {
	
	private final List<Widget> widgets = new ArrayList<Widget>();
	
	private Theme theme;
	
	public WidgetManager(Huzuni huzuni) {
		super(huzuni, null);
	}

	/***
	 * Renders the widgets.
	 */
	public void render() {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled()) {
				widget.update();
			}
		}
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled()) {
				widget.useGlue();
				widget.renderMenu(widget.getX(), widget.getY(), widget.getWidth(), widget.getHeight());
			}
		}
	}

	/**
	 * Forces all widgets to apply their glue, excluding the origin widget.
	 * */
	public void formatWidgets(Widget origin) {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled() && origin != widget) {
				widget.useGlue();
			}
		}
	}

	/**
	 * Renders outlines around widgets along with red lines indicating where glue can be applied.
	 * */
	public void renderWidgetOutlines() {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled()) {
				GLManager.glColor(1F, 1F, 1F, 1F);
				RenderUtils.drawBorder(1F, widget.getX(), widget.getY(), widget.getX() + widget.getWidth(), widget.getY() + widget.getHeight());
				if (widget.isDragging()) {
					renderGlue(widget);
				}
			}
		}
	}

	/**
	 * Renders the tooltips over widgets.
	 * */
	public void renderTooltip(int mouseX, int mouseY) {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled() && !widget.isDragging() && widget.isPointInside(mouseX, mouseY)) {
				theme.drawTooltip(widget.getName(), mouseX, mouseY);
				break;
			}
		}
	}

	/**
	 * Renders red lines indicating where on the widget glue will be applied to.
	 */
	private void renderGlue(Widget widget) {
		int x = -1, y = -1;
		if (widget.getGlue() instanceof ScreenGlue) {
			ScreenGlue screenGlue = (ScreenGlue) widget.getGlue();
			x = screenGlue.isLeft() ? widget.getX() : screenGlue.isRight() ? widget.getX() + widget.getWidth() : screenGlue.isCenterX() ? widget.getX() + widget.getWidth() / 2 : -1;
			y = screenGlue.isTop() ? widget.getY() : screenGlue.isBottom() ? widget.getY() + widget.getHeight() : screenGlue.isCenterY() ? widget.getY() + widget.getHeight() / 2 : -1;
		} else if (widget.getGlue() instanceof WidgetGlue) {
			WidgetGlue widgetGlue = (WidgetGlue) widget.getGlue();
			int style = widgetGlue.getStyle();
			x = style == WidgetGlue.LEFT_BOTTOM || style == WidgetGlue.LEFT_TOP || style == WidgetGlue.LEFT_CENTER || style == WidgetGlue.TOP_RIGHT || style == WidgetGlue.BOTTOM_RIGHT ? widget.getX() + widget.getWidth() : style == WidgetGlue.RIGHT_BOTTOM || style == WidgetGlue.RIGHT_TOP || style == WidgetGlue.RIGHT_CENTER || style == WidgetGlue.TOP_LEFT || style == WidgetGlue.BOTTOM_LEFT ? widget.getX() : style == WidgetGlue.TOP_CENTER || style == WidgetGlue.BOTTOM_CENTER ? widget.getX() + widget.getWidth() / 2 : -1;
			y = style == WidgetGlue.TOP_LEFT || style == WidgetGlue.TOP_RIGHT || style == WidgetGlue.TOP_CENTER || style == WidgetGlue.LEFT_BOTTOM || style == WidgetGlue.RIGHT_BOTTOM ? widget.getY() + widget.getHeight() : style == WidgetGlue.BOTTOM_LEFT || style == WidgetGlue.BOTTOM_RIGHT || style == WidgetGlue.BOTTOM_CENTER || style == WidgetGlue.LEFT_TOP || style == WidgetGlue.RIGHT_TOP ? widget.getY() : style == WidgetGlue.LEFT_CENTER || style == WidgetGlue.RIGHT_CENTER ? widget.getY() + widget.getHeight() / 2 : -1;
		}
		GLManager.glColor(1F, 0F, 0F, 1F);
		if (x != -1)
			RenderUtils.drawLine(1F, x, widget.getY(), x, widget.getY() + widget.getHeight());
		
		if (y != -1) 
			RenderUtils.drawLine(1F, widget.getX(), y, widget.getX() + widget.getWidth(), y);
	}

	/**
	 * Invoked when the mouse button is clicked.
	 * */
	public void mouseClicked(int x, int y, int buttonId) {
		for (int i = widgets.size() - 1; i >= 0; i--) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled() && widget.mouseClicked(x, y, buttonId)) {
				widgets.remove(widget);
				widgets.add(widget);
				return;
			}
		}
	}

	public void update() {
	}

	/**
	 * Calculates the glue which can be applied to the given widget.
	 * */
	public WidgetGlue calculateWidgetGlue(Widget widget) {
		for (int i = 0; i < widgets.size(); i++) {
			Widget other = widgets.get(i);
			if (widget != other && other.isEnabled() && !WidgetGlue.isGluedTo(widget, other)) {
				WidgetGlue glue = WidgetGlue.getMenuGlue(widget, other);
				if (glue != null) {
					return glue;
				}
			}
		}
		return null;
	}

	public void addWidget(Widget widget) {
		widgets.add(widget);
		widget.setTheme(theme);
	}

	/**
	 * Applies the given theme to widgets.
	 * */
	public void setTheme(Theme theme) {
		this.theme = theme;
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			widget.setTheme(theme);
		}
	}

	/**
	 * Invoked when the keyboard is typed.
	 * */
	public void keyTyped(int keyCode) {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.isEnabled())
				widget.keyTyped(keyCode);
		}
	}

	/**
	 * @return The widget with the given name.
	 * */
	public Widget getWidget(String name) {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			if (widget.getName().equals(name))
				return widget;
		}
		return null;
	}
	
	public List<Widget> getWidgets() {
		return widgets;
	}
	
	@Override
	public void load() {
		super.load();
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			widget.load();
		}
	}
	
	
	@Override
	public void init() {
		for (int i = 0; i < widgets.size(); i++) {
			Widget widget = widgets.get(i);
			widget.init();
		}
	}
	
	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		for (Widget widget : widgets) {
			JsonObject object = new JsonObject();
			widget.save(object);
			objects.add(object);
		}
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		for (Widget widget : widgets) {
			if (widget.isObject(object)) {
				widget.load(object);
				return;
			}
		}
	}
	
}
