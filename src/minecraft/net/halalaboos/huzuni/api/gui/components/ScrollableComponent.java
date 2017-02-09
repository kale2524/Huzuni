package net.halalaboos.huzuni.api.gui.components;

import org.lwjgl.input.Mouse;

import net.halalaboos.huzuni.api.gui.Component;
import net.halalaboos.huzuni.api.util.render.GLManager;

/**
 * A component which implements scrolling logic. The scrolling logic can be configured to be vertical (default) or horizontal.
 * */
public abstract class ScrollableComponent extends Component {

	private boolean scrolling = false;
	
	private float scrolledPercentage = 0F;
	
	private boolean verticalScrollbar = true;
	
	private int totalAreaLength = 0, scrollbarSize = 8, scrollbarClickOffset = 0;
	
	public ScrollableComponent(int offsetX, int offsetY, int width, int height) {
		super(offsetX, offsetY, width, height);	
	}
	
	@Override
	public void render() {
		updateScrolling();
		super.render();
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		
	}
	
	@Override
	public void update() {
		totalAreaLength = this.calculateTotalAreaLength();
	}
	
	@Override
	public void mouseClicked(int mouseX, int mouseY, int buttonId) {
		if (hasScrollbar() && isPointInsideScrollbar(mouseX, mouseY)) {
			scrolling = true;
			scrollbarClickOffset = (verticalScrollbar ? mouseY : mouseX) - getScrollbarPosition();
		}
	}

	@Override
	public void mouseWheel(int amount) {
		if (hasScrollbar())
			scrolledPercentage += -amount / (((float) totalAreaLength / (float) (verticalScrollbar ? getHeight() : getWidth())) * ((float) totalAreaLength / (float) (verticalScrollbar ? getHeight() : getWidth())));
	}
	
	/**
	 * @return The area of the scroll bar.
	 * */
	public int[] getScrollbarArea() {
		if (verticalScrollbar) {
			return new int[] {
					getX() + getWidth() - scrollbarSize,
					getY(),
					scrollbarSize,
					getHeight()
			};
		} else {
			return new int[] {
					getX(),
					getY() + getHeight() - scrollbarSize,
					getWidth(),
					scrollbarSize
			};
		}
	}
	
	/**
	 * @return The actual scroll bar... bar.
	 * */
	public int[] getScrollbar() {
		int scrollbarPosition = getScrollbarPosition();
		int scrollbarLength = getScrollbarLength();
		if (verticalScrollbar) {
			return new int[] {
					getX() + getWidth() - scrollbarSize,
					getY() + scrollbarPosition,
					scrollbarSize,
					scrollbarLength
			};
		} else {
			return new int[] {
					getX() + scrollbarPosition,
					getY() + getHeight() - scrollbarSize,
					scrollbarLength,
					scrollbarSize
			};
		}
	}
	
	/**
	 * @return The area within the component that can be rendered within.
	 * */
	public int[] getRenderArea() {
		if (hasScrollbar()) {
			if (verticalScrollbar) {
				return new int[] {
						getX(),
						getY(),
						getWidth() - scrollbarSize,
						getHeight()
				};
			} else {
				return new int[] {
						getX(),
						getY(),
						getWidth(),
						getHeight() - scrollbarSize
				};
			}
		} else {
			return new int[] {
					getX(),
					getY(),
					getWidth(),
					getHeight()
			};
		}
	}
	
	/**
	 * @return True if the given position is within the scroll bar.
	 * */
	public boolean isPointInsideScrollbar(int x, int y) {
		return isPointInside(x, y, getScrollbar());
	}
	
	/**
	 * @return True if the given position is within the render area.
	 * */
	public boolean isPointInsideRenderArea(int x, int y) {
		return isPointInside(x, y, getRenderArea());
	}
	
	/**
	 * Updates the scrolling information and scroll percentage.
	 * */
	protected void updateScrolling() {
		if (this.scrolling && Mouse.isButtonDown(0)) {
			this.scrolledPercentage = (float) ((verticalScrollbar ? GLManager.getMouseY() : GLManager.getMouseX()) - scrollbarClickOffset) / (float) ((verticalScrollbar ? getHeight() : getWidth()) - getScrollbarLength());
		} else
			this.scrolling = false;
		if (scrolledPercentage > 1)
			scrolledPercentage = 1;
		if (scrolledPercentage < 0)
			scrolledPercentage = 0;
		if (!hasScrollbar())
			scrolledPercentage = 0F;
		
	}
	
	/**
	 * @return The entire length of the area that the scrollable component can scroll.
	 * */
	protected abstract int calculateTotalAreaLength();
	
	/**
	 * @return The position where the scroll bar begins.
	 * */
	protected int getScrollbarPosition() {
		return (int) (scrolledPercentage *  getLengthOfScrollbarArea());
	}
	
	/**
	 * @return the length of the scroll bar area.
	 * */
	protected float getLengthOfScrollbarArea() {
		return (float) ((verticalScrollbar ? getHeight() : getWidth()) - getScrollbarLength());
	}
	
	/**
	 * @return The length of the scroll bar... bar.
	 * */
	protected int getScrollbarLength() {
		float ratio = (float) (verticalScrollbar ? getHeight() : getWidth()) / (float) totalAreaLength;
		if (ratio * (verticalScrollbar ? getHeight() : getWidth()) < 12)
			return 12;
		else
			return (int) (ratio * (verticalScrollbar ? getHeight() : getWidth()));
	}
	
	/**
	 * @return True if the given positions are within the render area.
	 * */
	protected boolean isWithinRenderArea(int firstPosition, int secondPosition) {
		int minimumPosition = (verticalScrollbar ? this.getY() : this.getX());
		int maxmimumPosition = (verticalScrollbar ? this.getY() + this.getHeight() : this.getX() + this.getWidth());
		return (firstPosition >= minimumPosition && secondPosition <= maxmimumPosition) || (firstPosition <= minimumPosition && secondPosition >= maxmimumPosition) || (firstPosition <= minimumPosition && secondPosition <= maxmimumPosition && secondPosition >= minimumPosition) || (secondPosition >= maxmimumPosition && firstPosition >= minimumPosition && firstPosition <= maxmimumPosition);
	}
	
	/**
	 * @return True if the component inside area is long enough to require a scroll bar.
	 * */
	public boolean hasScrollbar() {
		return totalAreaLength > (verticalScrollbar ? getHeight() : getWidth());
	}
	
	/**
	 * Resets the scrolling information.
	 * */
	public void resetScrollbar() {
		this.scrolledPercentage = 0F;
		this.scrolling = false;
	}

	public int getTotalAreaLength() {
		return totalAreaLength;
	}

	public void setTotalAreaLength(int totalAreaLength) {
		this.totalAreaLength = totalAreaLength;
	}

	public boolean isScrolling() {
		return scrolling;
	}

	public void setScrolling(boolean scrolling) {
		this.scrolling = scrolling;
	}

	public boolean isVerticalScrollbar() {
		return verticalScrollbar;
	}

	public void setVerticalScrollbar(boolean verticalScrollbar) {
		this.verticalScrollbar = verticalScrollbar;
	}

	public int getScrollbarSize() {
		return scrollbarSize;
	}

	public void setScrollbarSize(int scrollbarSize) {
		this.scrollbarSize = scrollbarSize;
	}
	
	/**
	 * @return The offset used for rendering and calculating the inside area.
	 * */
	public int getScrollOffset() {
		return (int) (scrolledPercentage * (float) (this.calculateTotalAreaLength() - (verticalScrollbar ? getHeight() : getWidth())));
	}
}
