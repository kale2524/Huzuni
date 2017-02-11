package net.halalaboos.huzuni.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class HuzuniLink extends GuiButton {

	public HuzuniLink(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
		if (this.visible) {
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            this.mouseDragged(mc, mouseX, mouseY);
            
            this.drawCenteredString(mc.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, hovered ? 16777120 : 0xFFFFFF);
        }
    }
	
}
