package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;

/**
 * @author brudin
 * @version 1.0
 * @since 4/14/14
 */
public final class GetCoords extends BasicCommand {
	
	public GetCoords() {
		super(new String[] { "getcoords", "gc" }, "Copies your current coordinates to your clipboard.");
		
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		String coords = getFormattedCoordinates(Minecraft.getMinecraft().player);
		huzuni.addChatMessage(coords + " copied to your clipboard.");
		GuiScreen.setClipboardString(coords);
	}
	
	private String getFormattedCoordinates(Entity entity) {
		return (int) entity.posX + ", " + (int) entity.posY + ", " + (int) entity.posZ;
	}
}
