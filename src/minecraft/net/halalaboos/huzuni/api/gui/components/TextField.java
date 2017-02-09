package net.halalaboos.huzuni.api.gui.components;

import net.halalaboos.huzuni.api.gui.Component;

/**
 * Basic text field component.
 * */
public class TextField extends Component {

	public static final int TEXT_HEIGHT = 12;

	private final BasicTextField basicTextField = new BasicTextField();

	private String backText;
	
	private int cursorCounter = 0;
	
	public TextField(String backText, int offsetX, int offsetY, int width) {
		super(offsetX, offsetY, width, TEXT_HEIGHT);
		this.backText = backText;
	}
	
	@Override
	public void update() {
		super.update();
		cursorCounter++;
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
		basicTextField.keyTyped(typedChar, keyCode);
	}
	
	public String getBackText() {
		return backText;
	}

	public void setBackText(String backText) {
		this.backText = backText;
	}

	/**
	 * @return The text which should be rendered.
	 * */
	public String getRenderText(boolean showPlacement) {
		return basicTextField.getRenderText(showPlacement && this.cursorCounter / 24 % 2 == 0);
	}

	public BasicTextField getBasicTextField() {
		return basicTextField;
	}
	
	public boolean hasText() {
		return !basicTextField.getText().isEmpty();
	}

	public String getText() {
		return basicTextField.getText();
	}
}
