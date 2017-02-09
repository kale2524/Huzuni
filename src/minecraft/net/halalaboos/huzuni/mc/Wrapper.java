package net.halalaboos.huzuni.mc;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.event.EventKeyPress;
import net.halalaboos.huzuni.api.event.EventLoadWorld;
import net.halalaboos.huzuni.api.event.EventMouseClick;
import net.minecraft.client.multiplayer.WorldClient;

public final class Wrapper {

	private static final Huzuni huzuni = Huzuni.INSTANCE;
		
	private Wrapper() {
		
	}
	
	public static void onMouseClicked(int buttonId) {
		huzuni.eventManager.invoke(new EventMouseClick(buttonId));
	}
	
	public static void loadWorld(WorldClient world) {
		if (world != null) {
			if (huzuni.settings.firstUse.isEnabled()) {
				huzuni.addChatMessage("Welcome to huzuni!");
				huzuni.addChatMessage("Press right shift to open up the settings menu!");
				huzuni.addChatMessage("Type \".help\" for a list of commands!");
				huzuni.settings.firstUse.setEnabled(false);
			}
		}
		huzuni.lookManager.cancelTask();
		huzuni.eventManager.invoke(new EventLoadWorld(world));
	}
	
	public static void keyTyped(int keyCode) {
		huzuni.guiManager.widgetManager.keyTyped(keyCode);
		huzuni.eventManager.invoke(new EventKeyPress(keyCode));
	}
}
