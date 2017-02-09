package net.halalaboos.huzuni.gui.screen;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.Huzuni;
import net.minecraft.client.gui.GuiScreen;

public class HuzuniScreen extends GuiScreen {
	
	protected static final Huzuni huzuni = Huzuni.INSTANCE;
	
	public HuzuniScreen() {
		Keyboard.enableRepeatEvents(true);
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		// super.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
	}

	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
	}
	
	@Override
    public void onGuiClosed() {
    	super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
    }
}
