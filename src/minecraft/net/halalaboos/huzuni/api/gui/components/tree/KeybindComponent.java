package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.mod.BasicKeybind;

public class KeybindComponent extends NodeTreeComponent <BasicKeybind> {
	
	public static final int KEYBIND_HEIGHT = 12, CLEAR_HEIGHT = 12;
	
	private boolean editing;
	
	public KeybindComponent(BasicKeybind node) {
		super(node);
		setHeight(KEYBIND_HEIGHT + PADDING + CLEAR_HEIGHT);
	}
	
	@Override
	public void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
		if (editing) {
			node.setKeycode(keyCode);
			editing = false;
		}
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		if (mouseX > x + theme.getStringWidth(node.getName()) + 6 && mouseX < x + width && mouseY > y && mouseY < y + KEYBIND_HEIGHT) {
			editing = !editing;
		} else if (mouseX > x && mouseX < x + width && mouseY > y + KEYBIND_HEIGHT + PADDING && mouseY < y + KEYBIND_HEIGHT + PADDING + CLEAR_HEIGHT) {
			editing = false;
			node.setKeycode(-1);
		}
	}

	public boolean isEditing() {
		return editing;
	}

}
