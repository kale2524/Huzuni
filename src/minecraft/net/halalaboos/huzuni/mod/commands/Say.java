package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;

public final class Say extends BasicCommand {

	public Say() {
		super(new String[] { "say", "s" }, "Says something in chat.");
	}
	
	@Override
	public void giveHelp() {
		huzuni.addChatMessage(".say <text>");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		String text = StringUtils.getAfter(input, 1);
		Minecraft.getMinecraft().getConnection().sendPacket(new CPacketChatMessage(text));
	}
	
}
