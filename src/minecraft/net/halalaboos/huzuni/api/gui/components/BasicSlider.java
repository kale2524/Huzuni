package net.halalaboos.huzuni.api.gui.components;

import net.halalaboos.huzuni.api.gui.Theme;
import org.lwjgl.input.Mouse;

import net.halalaboos.huzuni.api.util.render.GLManager;

/**
 * Handles basic slider logic.
 * */
public class BasicSlider {

	protected String title;
	
	protected int x, y, width, height, barSize;
	
	protected float sliderPercentage = 0.5F;
	
	protected boolean sliding = false;
	
	public BasicSlider(String title, int x, int y, int width, int height, int barSize) {
		this.title = title;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.barSize = barSize;
	}

	/**
	 * Renders the slider with the given theme.
	 * */
	public void renderSlider(Theme theme, boolean mouseOver) {
		int[] sliderArea = getSliderArea();
		theme.drawRect(sliderArea[0], sliderArea[1], sliderArea[2], sliderArea[3],false, false);
		theme.drawString(title, sliderArea[0] + 2, sliderArea[1] + sliderArea[3] / 2 - theme.getStringHeight(title) / 2 + 2, 0xFFFFFF);
		String value = getFormattedValue();
		theme.drawString(value, sliderArea[0] + sliderArea[2] - theme.getStringWidth(value) - 2, sliderArea[1] + sliderArea[3] / 2 - theme.getStringHeight(value) / 2 + 2, 0xFFFFFF);
		int[] sliderBar = getSliderBar();
		theme.drawRect(sliderBar[0], sliderBar[1], sliderBar[2], sliderBar[3], mouseOver, true);
	}

	/**
	 * Updates the slider percentage.
	 * */
	public void updateSliding() {
		if (this.sliding && Mouse.isButtonDown(0)) {
			this.sliderPercentage = (float) (GLManager.getMouseX() - x - (getBarSize() / 2F)) / (float) (getWidthForSlider());
			this.keepSafe();
		} else
			this.sliding = false;
	}

	/**
	 * Updates the position and size of the slider.
	 * */
	public void updatePosition(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public int[] getSliderArea() {
		return new int[] { x, y, width, height };
	}
	
	public int[] getSliderBar() {
		return new int[] { x + getSliderbarX(), y, getBarSize(), height };
	}
	
	public int getSliderbarX() {
		return (int) (sliderPercentage * getWidthForSlider());
	}

	/**
	 * @return The total width for the slider.
	 * */
	public float getWidthForSlider() {
		int SLIDER_PADDING = 0;
		float maxPointForRendering = (float) (width - getBarSize() - SLIDER_PADDING),
				beginPoint = (SLIDER_PADDING);
		return maxPointForRendering - beginPoint;
	}

	/**
	 * @return True if the position is within the slider area.
	 * */
	public boolean isPointInside(int x, int y) {
		return x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height;
	}

	/**
	 * Forces the slider percentage between 0 - 1.
	 * */
	private void keepSafe() {
		if (sliderPercentage > 1)
			sliderPercentage = 1;
		if (sliderPercentage < 0)
			sliderPercentage = 0;
	}
	
	public boolean isSliding() {
		return sliding;
	}
	
	public void setSliding(boolean sliding) {
		this.sliding = sliding;
	}
	
	public float getSliderPercentage() {
		return sliderPercentage;
	}
	
	public void setSliderPercentage(float sliderPercentage) {
		this.sliderPercentage = sliderPercentage;
	}
	
	public int getBarSize() {
		return barSize;
	}
	
	public void setBarSize(int barSize) {
		this.barSize = barSize;
	}
	
	public String getFormattedValue() {
		return "" + (int) (sliderPercentage * 100);
	}

	public String getTitle() {
		return title;
	}
}
