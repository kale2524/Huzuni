package net.halalaboos.huzuni.api.gui.components;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.ChatAllowedCharacters;

/**
 * Handles basic text field logic.
 * */
public class BasicTextField {

	private String text = "";
	
	private int selectionPosition = 0, maxLength = 50;

	public BasicTextField() {
		
	}

	/**
	 * Invoked when the keyboard is typed.
	 * */
	public void keyTyped(char typedChar, int keyCode) {
		switch (keyCode) {
		case Keyboard.KEY_BACK:
			backSpace();
			break;
		case Keyboard.KEY_HOME:
			move(0);
			break;
		case Keyboard.KEY_LEFT:
			this.moveDirection(-1);
			break;
		case Keyboard.KEY_RIGHT:
			this.moveDirection(1);
			break;
		case Keyboard.KEY_END:
			move(this.text.length());
			break;
		case Keyboard.KEY_DELETE:
			forwardSpace();
			break;

		default:
			if (ChatAllowedCharacters.isAllowedCharacter(typedChar)) {
				this.append(Character.toString(typedChar));
			}
			break;
		}
	}

	/**
	 * Performs the backspace operation on the text with the current selection position.
	 * */
	private void backSpace() {
		if (!this.text.isEmpty()) {
			if (selectionPosition >= this.text.length()) {
				this.text = this.text.substring(0, this.text.length() - 1);
			} else if (selectionPosition > 0) {
				this.text = this.text.substring(0, selectionPosition - 1) + this.text.substring(selectionPosition, this.text.length());
			}
			moveDirection(-1);
		}
	}

	/**
	 * Performs the forward space operation on the text with the current selection position.
	 * */
	private void forwardSpace() {
		if (!this.text.isEmpty()) {
			if (selectionPosition <= 0) {
				this.text = this.text.substring(1, this.text.length());
			} else if (selectionPosition < this.text.length()) {
				this.text = this.text.substring(0, selectionPosition) + this.text.substring(selectionPosition + 1, this.text.length());
			}
		}
	}

	/**
	 * Moves the selection position to the given index.
	 * */
	private void move(int index) {
		selectionPosition = index;
		keepSafe();
	}

	/**
	 * Moves the selection position into the direction given.
	 * */
	private void moveDirection(int direction) {
		selectionPosition += direction;
		keepSafe();
	}

	/**
	 * Keeps the selection position within 0 and the length of the text.
	 * */
	private void keepSafe() {
		if (selectionPosition > this.text.length()) {
			selectionPosition = this.text.length();
		} else if (selectionPosition < 0) {
			selectionPosition = 0;
		}
	}

	/**
	 * Appends the given string to the text at the selection position.
	 * */
	private void append(String string) {
		if (text.concat(string).length() <= maxLength) {
			if (selectionPosition >= this.text.length()) {
				this.text = this.text.concat(string);
			} else if (selectionPosition > 0) {
				this.text = this.text.substring(0, selectionPosition) + string + this.text.substring(selectionPosition, this.text.length());
			}
			selectionPosition += string.length();
		}
	}

	/**
	 * @return A string that can be rendered.
	 * */
	public String getRenderText(boolean showPlacement) {
		return showPlacement ? this.text.substring(0, selectionPosition) + "|" + this.text.substring(selectionPosition, this.text.length()) : this.text;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getSelectionPosition() {
		return selectionPosition;
	}

	public void setSelectionPosition(int selectionPosition) {
		this.selectionPosition = selectionPosition;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

}
