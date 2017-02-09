package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.util.text.TextFormatting;

public final class Toggle extends BasicCommand {

	public Toggle() {
		super(new String[] { "toggle", "t" }, "Enable/disable mods.");
		
	}
	
	@Override
	public void giveHelp() {
		huzuni.addChatMessage(".toggle \"mod\"");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		Mod mod = huzuni.modManager.getModIgnoreCase(args[0]);
		mod.toggle();
		String message = String.format("%s is now %s!", mod.getName(), (mod.isEnabled() ? TextFormatting.GREEN + "enabled" + TextFormatting.RESET : TextFormatting.RED + "disabled" + TextFormatting.RESET));
		huzuni.addChatMessage(message);
		huzuni.addNotification(NotificationType.INFO, "Toggle Command", 5000, message);
	}

}
