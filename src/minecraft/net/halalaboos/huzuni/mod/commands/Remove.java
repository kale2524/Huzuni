package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;

public final class Remove extends BasicCommand {

	public Remove() {
		super(new String[] { "remove", "delete", "del" }, "Removes a player from the friends list");
	}
	
	@Override
	public void giveHelp() {
		huzuni.addChatMessage(".remove <username>");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		if (huzuni.friendManager.isFriend(args[0])) {
			huzuni.friendManager.removeFriend(args[0]);
			huzuni.addChatMessage(args[0] + " removed.");
			huzuni.friendManager.save();
		} else
			huzuni.addChatMessage(args[0] + " is not in your friends list!");
	}

}
