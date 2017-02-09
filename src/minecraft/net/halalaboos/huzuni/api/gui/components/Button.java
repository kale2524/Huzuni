package net.halalaboos.huzuni.api.gui.components;

import net.halalaboos.huzuni.api.gui.Component;

/**
 * Basic button component.
 * */
public class Button extends Component {

	private String title;
	
	private boolean highlight = false;
	
	public Button(String title, int offsetX, int offsetY, int width, int height) {
		super(offsetX, offsetY, width, height);
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHighlight() {
		return highlight;
	}

	public void setHighlight(boolean highlight) {
		this.highlight = highlight;
	}
	
}
