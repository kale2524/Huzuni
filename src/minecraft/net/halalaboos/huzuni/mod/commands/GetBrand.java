package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.minecraft.client.Minecraft;

public final class GetBrand extends BasicCommand {
	
	public GetBrand() {
		super(new String[] { "getbrand", "brand" }, "Gives server brand info.");
		
	}

	@Override
	protected void runCommand(String input, String[] args) {
		if (Minecraft.getMinecraft().isSingleplayer()) {
			huzuni.addChatMessage("You're not connected to a server!");
		} else {
			huzuni.addChatMessage("Brand: " + Minecraft.getMinecraft().player.getServerBrand());
		}
	}
}
