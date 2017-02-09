package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author brudin
 * @version 1.0
 * @since 4/14/14
 */
public final class GetIP extends BasicCommand {
	
	public GetIP() {
		super("getip", "Copies the server IP to your clipboard.");
		
	}

	@Override
	protected void runCommand(String input, String[] args) {
		if (Minecraft.getMinecraft().isSingleplayer()) {
			huzuni.addChatMessage("You're not connected to a server!");
		} else {
			String ip = Minecraft.getMinecraft().getCurrentServerData().serverIP;
			GuiScreen.setClipboardString(ip);
			huzuni.addChatMessage(ip + " copied to your clipboard.");
		}
	}
}
