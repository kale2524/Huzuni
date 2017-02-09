package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.minecraft.client.Minecraft;

public final class Clear extends BasicCommand {

	public Clear() {
		super(new String[] { "clear", "clr" }, "Clears all messages from chat");
	}

	@Override
	protected void runCommand(String input, String[] args) {
		// TODO: Fix this.
		//Minecraft.getMinecraft().ingameGUI.getChatGUI().clearChatMessages();
	}

}
