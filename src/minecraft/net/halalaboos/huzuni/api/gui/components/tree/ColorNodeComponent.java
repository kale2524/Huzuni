package net.halalaboos.huzuni.api.gui.components.tree;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.gui.components.BasicSlider;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.ColorNode;
import net.halalaboos.huzuni.api.util.render.GLManager;

public class ColorNodeComponent extends NodeTreeComponent <ColorNode> {

	public static final int LABEL_SIZE = 12, PICKER_HEIGHT = 22, OPTION_SIZE = 10, SLIDER_SIZE = 15;
	
	private final List<Color> options = new ArrayList<Color>();
	
	private final BasicSlider saturationSlider = new BasicSlider("Saturation", 0, 0, 0, 0, 7);
	
	private final BasicSlider luminanceSlider = new BasicSlider("Luminance", 0, 0, 0, 0, 7);

	private int maxOptions;
	
	private boolean loaded = false;
	
	public ColorNodeComponent(ColorNode node) {
		super(node);
		this.setHeight(LABEL_SIZE + PICKER_HEIGHT + PADDING + SLIDER_SIZE + PADDING + SLIDER_SIZE);
		saturationSlider.setSliderPercentage(0.55F);
		luminanceSlider.setSliderPercentage(0.7F);
	}

	@Override
	public void render(Theme theme, boolean mouseOver, int x, int y, int width, int height) {
		if (!loaded) {
			maxOptions = getMaxOptionCount(width);
			generateMaxOptions();
			loaded = true;
		}
		saturationSlider.updatePosition(x, y + LABEL_SIZE + PICKER_HEIGHT + PADDING, width, SLIDER_SIZE);
		luminanceSlider.updatePosition(x, y + LABEL_SIZE + PICKER_HEIGHT + PADDING + SLIDER_SIZE + PADDING, width, SLIDER_SIZE);
		super.render(theme, mouseOver, x, y, width, height);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		saturationSlider.updatePosition(x, y + LABEL_SIZE + PICKER_HEIGHT + PADDING, width, SLIDER_SIZE);
		saturationSlider.setSliding(saturationSlider.isPointInside(mouseX, mouseY));
		luminanceSlider.updatePosition(x, y + LABEL_SIZE + PICKER_HEIGHT + PADDING + SLIDER_SIZE + PADDING, width, SLIDER_SIZE);
		luminanceSlider.setSliding(luminanceSlider.isPointInside(mouseX, mouseY));
		for (int i = 0; i < options.size(); i++) {
			int[] optionArea = this.getOptionArea(x, y, i);
			if (mouseX > optionArea[0] && mouseX < optionArea[0] + optionArea[2] && mouseY > optionArea[1] && mouseY < optionArea[1] + optionArea[3]) {
				node.setColor(options.get(i));
				break;
			}
		}
	}
	
	@Override
	public void update() {
		super.update();
		saturationSlider.updateSliding();
		luminanceSlider.updateSliding();
		if (saturationSlider.isSliding() || luminanceSlider.isSliding())
			loaded = false;
	}
	
	public int[] getOptionArea(int x, int y, int index) {
		int row = (int) Math.ceil(index / maxOptions);
		int column = index % maxOptions;
		int xPos = x + PADDING + (column * (OPTION_SIZE + (PADDING / 2)));
		int yPos = y + PADDING + LABEL_SIZE + (row * (OPTION_SIZE + (PADDING / 2)));
		return new int[] { xPos, yPos, OPTION_SIZE, OPTION_SIZE };
	}
	
	public int getMaxOptionCount(int width) {
		return (int) Math.ceil((float) width / (float) (OPTION_SIZE + PADDING));
	}
	
	public void generateOptions(int count) {
		options.clear();
		int increment = 360 / count;
		int hue = 0;
		for (int i = 0; i < count; i++) {
			hue += increment;
			options.add(GLManager.getHSBColor((float) hue / 360F, saturationSlider.getSliderPercentage(), luminanceSlider.getSliderPercentage()));
		}
	}
	
	public void generateMaxOptions() {
		int maxRows = (int) Math.ceil(PICKER_HEIGHT / (OPTION_SIZE + PADDING / 2));
		generateOptions(maxRows * maxOptions);
	}

	public List<Color> getOptions() {
		return options;
	}

	public BasicSlider getSaturationSlider() {
		return saturationSlider;
	}

	public BasicSlider getLuminanceSlider() {
		return luminanceSlider;
	}

}
