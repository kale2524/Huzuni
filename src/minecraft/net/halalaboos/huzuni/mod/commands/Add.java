package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;

public final class Add extends BasicCommand {

	public Add() {
		super(new String[] { "add", "a" }, "Adds a player to your friends list");
	}
	
	@Override
	public void giveHelp() {
		huzuni.addChatMessage(".add \"username\" [\"alias\"]");
	}

	@Override
	protected void runCommand(String input, String[] args) {
		boolean successful = !huzuni.friendManager.isFriend(args[0]);
		if (args.length > 1) {
			huzuni.friendManager.addFriend(args[0], args[1]);
			huzuni.addChatMessage("Friend '" + args[0] + "' " + (successful ? "added!" : "already in your friends!"));
			huzuni.addChatMessage("Now under the alias " + args[1] + ".");
		} else {
			huzuni.friendManager.addFriend(args[0]);
			huzuni.addChatMessage("Friend '" + args[0] + "' " + (successful ? "added!" : "already in your friends!"));
		}
		if (successful)
			huzuni.friendManager.save();
	}

}
