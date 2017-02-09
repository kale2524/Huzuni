package net.halalaboos.huzuni.mod.commands;

import java.awt.Font;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.gui.Notification.NotificationType;

public final class SetFont extends BasicCommand {

	public SetFont() {
		super("font", "Set the chat font");
	}
	
	@Override
	public void giveHelp() {
		huzuni.addChatMessage(".font \"name\" <size>");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		Font font = new Font(args[0], Font.PLAIN, Integer.parseInt(args[1]));
		huzuni.chatFontRenderer.setFont(font, true);
		huzuni.addNotification(NotificationType.INFO, "Font Command", 5000, String.format("Chat font set to %s!", font.getName()));
	}

}
