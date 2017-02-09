package net.halalaboos.huzuni.mod.commands;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.mod.Mod;

public final class AllOff extends BasicCommand {
	
    public AllOff() {
		super(new String[] { "alloff", "off" }, "Turns all enabled mods off.");
	}
    
	@Override
	protected void runCommand(String input, String[] args) {
        for (Mod mod : huzuni.modManager.getMods()) {
            if (mod.isEnabled())
            	mod.setEnabled(false);
        }
        huzuni.addChatMessage("All mods turned off.");
	}
}