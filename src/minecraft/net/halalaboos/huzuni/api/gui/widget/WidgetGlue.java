package net.halalaboos.huzuni.api.gui.widget;

import net.halalaboos.huzuni.api.gui.WidgetManager;

/**
 * Glue that can be applied to a widget which glues one widget to another.
 * */
public class WidgetGlue implements Glue {
	
	public static final int TOP_LEFT = 0, 
			TOP_RIGHT = 1, 
			BOTTOM_LEFT = 2, 
			BOTTOM_RIGHT = 3,
			LEFT_TOP = 4, 
			RIGHT_TOP = 5, 
			LEFT_BOTTOM = 6, 
			RIGHT_BOTTOM = 7,
			TOP_CENTER = 8,
			BOTTOM_CENTER = 9,
			LEFT_CENTER = 10,
			RIGHT_CENTER = 11;
	
	private final int style;
	
	private final Widget widget;
	
	private static final int SNAP_PADDING = 2;
	
	public WidgetGlue(int style, Widget tab) {
		this.style = style;
		this.widget = tab;
	}
	
	@Override
	public boolean modifiesX() {
		return true;
	}

	@Override
	public boolean modifiesY() {
		return true;
	}

	@Override
	public boolean isRight() {
		return widget.getGlue().isRight();
	}

	@Override
	public boolean isLeft() {
		return widget.getGlue().isLeft();
	}

	@Override
	public boolean isTop() {
		return widget.getGlue().isTop();
	}

	@Override
	public boolean isBottom() {
		return widget.getGlue().isBottom();
	}

	@Override
	public boolean isCenterX() {
		return widget.getGlue().isCenterX();
	}

	@Override
	public boolean isCenterY() {
		return widget.getGlue().isCenterY();
	}
	
	@Override
	public void formatX(Widget widget) {
		switch (style) {
		case LEFT_BOTTOM:
		case LEFT_TOP:
		case LEFT_CENTER:
			widget.setX(this.widget.getX() - widget.getWidth() - SNAP_PADDING);
			break;
		case RIGHT_BOTTOM:
		case RIGHT_TOP:
		case RIGHT_CENTER:
			widget.setX(this.widget.getX() + this.widget.getWidth() + SNAP_PADDING);
			break;
		case BOTTOM_LEFT:
		case TOP_LEFT:
			widget.setX(this.widget.getX());
			break;
		case BOTTOM_RIGHT:
		case TOP_RIGHT:
			widget.setX(this.widget.getX() + this.widget.getWidth() - widget.getWidth());
			break;
		case TOP_CENTER:
		case BOTTOM_CENTER:
			widget.setX(this.widget.getX() + this.widget.getWidth() / 2 - widget.getWidth() / 2);
			break;
		}
	}

	@Override
	public void formatY(Widget widget) {
		switch (style) {
		case LEFT_BOTTOM:
		case RIGHT_BOTTOM:
			widget.setY(this.widget.getY() + this.widget.getHeight() - widget.getHeight());
			break;
		case LEFT_TOP:
		case RIGHT_TOP:
			widget.setY(this.widget.getY());
			break;
		case BOTTOM_LEFT:
		case BOTTOM_RIGHT:
		case BOTTOM_CENTER:
			widget.setY(this.widget.getY() + this.widget.getHeight() + SNAP_PADDING);
			break;
		case TOP_LEFT:
		case TOP_RIGHT:
		case TOP_CENTER:
			widget.setY(this.widget.getY() - widget.getHeight() - SNAP_PADDING);
			break;
		case LEFT_CENTER:
		case RIGHT_CENTER:
			widget.setY(this.widget.getY() + this.widget.getHeight() / 2 - widget.getHeight() / 2);
			break;
		}
	}

	public Widget getWidget() {
		return widget;
	}

	/**
	 * @return True if the glue has any association with the given widget.
	 * */
	public boolean isGluedTo(Widget widget) {
		if (this.widget == widget)
			return true;
		else if (this.widget.getGlue() instanceof WidgetGlue) {
			return ((WidgetGlue) this.widget.getGlue()).isGluedTo(widget);
		} else
			return false;
	}
	
	public int getStyle() {
		return style;
	}

	/**
	 * Calculates intersection between the current widget vs the other widget.
	 * @return The glue which should be applied to the current widget.
	 * */
	public static WidgetGlue getMenuGlue(Widget current, Widget other) {
		float x = current.getX(), x1 = current.getX() + current.getWidth(), y = current.getY(), y1 = current.getY() + current.getHeight();
		float otherX = other.getX(), otherX1 = other.getX() + other.getWidth(), otherY = other.getY(), otherY1 = other.getY() + other.getHeight();
		float width = other.getWidth(), height = other.getHeight();
		float horizontalIntersection = 0, verticalIntersection = 0;
		float distX = Math.abs(x - otherX), distX1 = Math.abs(otherX1 - x1), distY = Math.abs(y - otherY), distY1 = Math.abs(otherY1 - y1), 
				distXCenter = Math.abs((current.getX() + current.getWidth() / 2) - (other.getX() + other.getWidth() / 2)),
				distYCenter = Math.abs((current.getY() + current.getHeight() / 2) - (other.getY() + other.getHeight() / 2));;
		if ((x < otherX && x1 > otherX1) || (x > otherX && x1 < otherX1)) {
			if (y < otherY1 && y > otherY)
				if (distX < distX1 && distX < distXCenter)
					return new WidgetGlue(BOTTOM_LEFT, other);
				else if (distX1 < distX && distX1 < distXCenter)
					return new WidgetGlue(BOTTOM_RIGHT, other);
				else
					return new WidgetGlue(BOTTOM_CENTER, other);
			else if (y1 > otherY && y1 < otherY1)
				if (distX < distX1 && distX < distXCenter)
					return new WidgetGlue(TOP_LEFT, other);
				else if (distX1 < distX && distX1 < distXCenter)
					return new WidgetGlue(TOP_RIGHT, other);
				else
					return new WidgetGlue(TOP_CENTER, other);
		}
		
		if ((y < otherY && y1 > otherY1) || (y > otherY && y1 < otherY1)) {
			if (x < otherX1 && x > otherX)
				if (distY < distY1 && distY < distYCenter)
					return new WidgetGlue(RIGHT_TOP, other);
				else if (distY1 < distY && distY1 < distYCenter)
					return new WidgetGlue(RIGHT_BOTTOM, other);
				else
					return new WidgetGlue(RIGHT_CENTER, other);
			else if (x1 > otherX && x1 < otherX1)
				if (distY < distY1 && distY < distYCenter)
					return new WidgetGlue(LEFT_TOP, other);
				else if (distY1 < distY && distY1 < distYCenter)
					return new WidgetGlue(LEFT_BOTTOM, other);
				else
					return new WidgetGlue(LEFT_CENTER, other);
		}
			
		if (x < otherX && x1 > otherX)
			horizontalIntersection = x1 - otherX;
		else if (x < otherX1 && x1 > otherX1)
			horizontalIntersection = otherX1 - x;
		
		if (y < otherY && y1 > otherY) 
			verticalIntersection = y1 - otherY;
		else if (y < otherY1 && y1 > otherY1)
			verticalIntersection = otherY1 - y;
		
		
		if (horizontalIntersection != 0 || verticalIntersection != 0) {
			if (horizontalIntersection / width > verticalIntersection / height) {
				if (y > otherY && y < otherY1)
					if (distX < distX1 && distX < distXCenter)
						return new WidgetGlue(BOTTOM_LEFT, other);
					else if (distX1 < distX && distX1 < distXCenter)
						return new WidgetGlue(BOTTOM_RIGHT, other);
					else
						return new WidgetGlue(BOTTOM_CENTER, other);
				else if (y1 > otherY && y1 < otherY1)
					if (distX < distX1 && distX < distXCenter)
						return new WidgetGlue(TOP_LEFT, other);
					else if (distX1 < distX && distX1 < distXCenter)
						return new WidgetGlue(TOP_RIGHT, other);
					else
						return new WidgetGlue(TOP_CENTER, other);
			} else {
				if (x > otherX && x < otherX1)
					if (distY < distY1 && distY < distYCenter)
						return new WidgetGlue(RIGHT_TOP, other);
					else if (distY1 < distY && distY1 < distYCenter)
						return new WidgetGlue(RIGHT_BOTTOM, other);
					else
						return new WidgetGlue(RIGHT_CENTER, other);
				else if (x1 > otherX && x1 < otherX1)
					if (distY < distY1 && distY < distYCenter)
						return new WidgetGlue(LEFT_TOP, other);
					else if (distY1 < distY && distY1 < distYCenter)
						return new WidgetGlue(LEFT_BOTTOM, other);
					else
						return new WidgetGlue(LEFT_CENTER, other);
		   }
			return null;
		} else
			return null;
	}

	/**
	 * @return True if the other widget is glued to the current widget.
	 * */
	public static boolean isGluedTo(Widget current, Widget other) {
		if (other.getGlue() instanceof WidgetGlue) {
			WidgetGlue menuGlue = (WidgetGlue) other.getGlue();
			return menuGlue.isGluedTo(current);
		} else
			return false;
	}

	/**
	 * Creates a string representing the glue type and widget name.
	 * */
	public static String getName(WidgetGlue glue) {
		return String.format("WidgetGlue(%s, %s)", glue.widget.getName(), glue.style);
	}

	/**
	 * Parses the widget and glue type from the input string.
	 * */
	public static boolean loadGlue(Widget ingameMenu, WidgetManager menuManager, String glue) {
		if (glue.startsWith("WidgetGlue")) {
			String[] information = glue.substring(glue.indexOf("(") + 1, glue.lastIndexOf(")")).split(", ");
			ingameMenu.setGlue(new WidgetGlue(Integer.parseInt(information[1]), menuManager.getWidget(information[0])));
			return true;
		} else
			return false;
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof WidgetGlue) {
			WidgetGlue otherGlue = (WidgetGlue) object;
			return otherGlue.style == this.style && otherGlue.widget == this.widget;
		}
		return super.equals(object);
	}
	
}
