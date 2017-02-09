package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.mod.Mod;
import net.minecraft.util.text.TextFormatting;

/**
 * @author brudin
 * @version 1.0
 * @since 4/10/14
 */
public final class Mods extends BasicCommand {

	public Mods() {
		super(new String[] { "mods", "hacks" }, "Lists all of the mods.");
	}

	@Override
	protected void runCommand(String input, String[] args) {
		String allMods = "Mods (" + huzuni.modManager.getMods().size() + "): ";
		for (Mod mod : huzuni.modManager.getMods()) {
			allMods += (mod.isEnabled() ? TextFormatting.GREEN : TextFormatting.RED) + mod.getName() + TextFormatting.RESET + ", ";
		}
		huzuni.addChatMessage(allMods.substring(0, allMods.length() - 2));
	}
	
}