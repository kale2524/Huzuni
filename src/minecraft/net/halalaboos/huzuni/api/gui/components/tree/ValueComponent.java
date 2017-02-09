package net.halalaboos.huzuni.api.gui.components.tree;

import java.text.DecimalFormat;

import net.halalaboos.huzuni.api.gui.components.BasicSlider;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.Value;

public class ValueComponent extends NodeTreeComponent <Value> {

	public static final int TEXT_HEIGHT = 12, SLIDER_HEIGHT = 12;
	
	private final BasicSlider slider = new BasicSlider("", 0, 0, 0, 0, 7);
	
	private final DecimalFormat formatter = new DecimalFormat("#.#");
	
	public ValueComponent(Value node) {
		super(node);
		this.setHeight(TEXT_HEIGHT + PADDING + SLIDER_HEIGHT);
	}
	
	@Override
	public void render(Theme theme, boolean mouseOver, int x, int y, int width, int height)  {
		slider.updatePosition(x, y + TEXT_HEIGHT + PADDING, width, SLIDER_HEIGHT);
		super.render(theme, mouseOver, x, y, width, height);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		slider.updatePosition(x, y + TEXT_HEIGHT + PADDING, width, SLIDER_HEIGHT);
		slider.setSliding(slider.isPointInside(mouseX, mouseY));
	}
	
	@Override
	public void update() {
		super.update();
		slider.updateSliding();
		if (slider.isSliding())
			updateValue();
		else
			slider.setSliderPercentage((node.getValue() - node.getMinValue()) / (node.getMaxValue() - node.getMinValue()));
	}
	
	private void updateValue() {
		float calculatedValue = (slider.getSliderPercentage() * (node.getMaxValue() - node.getMinValue()));
		if (node.getIncrementValue() == -1)
			node.setValue(calculatedValue + node.getMinValue());
		else
			node.setValue(((calculatedValue) - ((calculatedValue) % node.getIncrementValue())) + node.getMinValue());
		node.setValue(Float.valueOf(formatter.format(node.getValue())));
	}

	public BasicSlider getSlider() {
		return slider;
	}

}
