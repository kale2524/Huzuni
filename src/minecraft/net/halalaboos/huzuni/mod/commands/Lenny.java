package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.minecraft.client.gui.GuiScreen;

public final class Lenny extends BasicCommand {

	public Lenny() {
		super(new String[] { "lenny", "len" }, "( ͡° ͜ʖ ͡°)");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		GuiScreen.setClipboardString("( ͡° ͜ʖ ͡°)");
		huzuni.addChatMessage("Copied to clipboard.");
	}

}
