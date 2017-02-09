package net.halalaboos.huzuni.api.gui.widget;

import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.Minecraft;

/**
 * Formats the positions of widgets based on the entire screen. <br/>
 * 0 - TOP/RIGHT <br/>
 * 1 - BOTTOM/LEFT <br/>
 * 2 - NORMAL/NORMAL <br/>
 * 3 - CENTER/CENTER
 * */
public enum ScreenGlue implements Glue {
	NONE(2, 2),
	TOP(2, 0), 
	RIGHT(0, 2),
	BOTTOM(2, 1), 
	LEFT(1, 2),
	CENTER_TOP(3, 0),
	CENTER_RIGHT(0, 3),
	CENTER_LEFT(1, 3),
	CENTER_BOTTOM(3, 1),
	TOP_AND_RIGHT(0, 0), BOTTOM_AND_RIGHT(0, 1),
	TOP_AND_LEFT(1, 0), BOTTOM_AND_LEFT(1, 1);
	
	private static final int PADDING = 10, SNAP_PADDING = 2;
	
	private final int x, y;
	
	ScreenGlue(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean modifiesX() {
		return x != 2;
	}
	
	@Override
	public boolean modifiesY() {
		return y != 2;
	}
	
	@Override
	public boolean isRight() {
		return x == 0;
	}
	
	@Override
	public boolean isLeft() {
		return x == 1;
	}
	
	@Override
	public boolean isTop() {
		return y == 0;
	}
	
	@Override
	public boolean isBottom() {
		return y == 1;
	}
	
	@Override
	public boolean isCenterX() {
		return x == 3;
	}
	
	@Override
	public boolean isCenterY() {
		return y == 3;
	}
	
	@Override
	public void formatX(Widget widget) {
		int width = GLManager.getScreenWidth();
		switch (x) {
		case 0:
			widget.setX(width - widget.getWidth() - SNAP_PADDING);
			break;
		case 1:
			widget.setX(SNAP_PADDING);
			break;
		case 3:
			widget.setX(width / 2 - widget.getWidth() / 2);
			break;
			default:
				break;
		}
	}

	@Override
	public void formatY(Widget widget) {
		int height = GLManager.getScreenHeight();
		switch (y) {
		case 0:
			if (!Minecraft.getMinecraft().player.getActivePotionEffects().isEmpty() && x == 0) {
				widget.setY(24 + SNAP_PADDING);
			} else
				widget.setY(SNAP_PADDING);
			break;
		case 1:
			widget.setY(height - widget.getHeight() - SNAP_PADDING);
			break;
		case 3:
			widget.setY(height / 2 - widget.getHeight() / 2);
			break;
			default:
				break;
		}
	}

	/**
	 * @return the integer associated with the format required for the widget
	 * */
	private static int getRequiredX(Widget widget) {
		int width = GLManager.getScreenWidth(), center = width / 2;
		int minX = PADDING, maxX = width - PADDING;
		if (widget.getX() + widget.getWidth() > maxX) {
			return 0;
		} else if (widget.getX() < minX) {
			return 1;
		} else if (widget.getX() + widget.getWidth() / 2 > center - PADDING && widget.getX() + widget.getWidth() / 2 < center + PADDING) {
			return 3;
		} else
			return 2;
	}

	/**
	 * @return the integer associated with the format required for the widget
	 * */
	private static int getRequiredY(Widget widget) {
		int height = GLManager.getScreenHeight(), center = height / 2;
		int minY = PADDING, maxY = height - PADDING;
		if (widget.getY() < minY) {
			return 0;
		} else if (widget.getY() + widget.getHeight() > maxY) {
			return 1;
		} else if (widget.getY() + widget.getHeight() / 2 > center - PADDING && widget.getY() + widget.getHeight() / 2 < center + PADDING) {
			return 3;
		} else
			return 2;
	}

	/**
	 * @return The glue which should be applied to the given widget.
	 * */
	public static ScreenGlue getScreenGlue(Widget widget) {
		int requiredX = getRequiredX(widget), requiredY = getRequiredY(widget);
		for (ScreenGlue menuGlue : values()) {
			if (menuGlue.x == requiredX && menuGlue.y == requiredY)
				return menuGlue;
		}
		return NONE;
	}

	/**
	 * Parses the name and returns the screen glue associated with the name.
	 * */
	public static ScreenGlue load(String name) {
		for (ScreenGlue glue : values()) {
			if (glue.name().equals(name))
				return glue;
		}
		return NONE;
	}
	
	/**
	 * Forces the widget to stay within the screen.
	 * */
	public static void keepWithinScreen(Widget widget) {
		int width = GLManager.getScreenWidth(), height = GLManager.getScreenHeight();
		if (widget.getX() < SNAP_PADDING)
			widget.setX(SNAP_PADDING);
		if (widget.getY() < SNAP_PADDING)
			widget.setY(SNAP_PADDING);
		if (widget.getX() + widget.getWidth() > width - SNAP_PADDING)
			widget.setX(width - widget.getWidth() - SNAP_PADDING);
		if (widget.getY() + widget.getHeight() > height - SNAP_PADDING)
			widget.setY(height - widget.getHeight() - SNAP_PADDING);
	}
}
