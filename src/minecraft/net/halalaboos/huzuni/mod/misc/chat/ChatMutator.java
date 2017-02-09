package net.halalaboos.huzuni.mod.misc.chat;

import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.mod.misc.chat.mutators.*;
import net.minecraft.network.play.client.CPacketChatMessage;

/**
 * Modifies sent chat messages before sending them.
 * */
public class ChatMutator extends Mod {
	
	public ChatMutator() {
		super("Chat mutator", "Modify all messages sent in-game.");
		this.setCategory(Category.MISC);
		this.addChildren(new SpeechTherapist(), new DolanSpeak(), new Educated(), new SpeedyGonzales(), new Flanders(), new SpellCheck(), new LeetSpeak(), new Underdeveloped(), new Emoticon(), new Backwards(), new Ramisme());
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
	}

	@EventMethod
	public void onPacket(EventPacket event) {
		if (event.type == EventPacket.Type.SENT) {
			if (event.getPacket() instanceof CPacketChatMessage) {
				CPacketChatMessage packetChatMessage = (CPacketChatMessage) event.getPacket();
				String message = packetChatMessage.getMessage();
				boolean serverCommand = message.startsWith("/");
				boolean clientCommand = message.startsWith(huzuni.commandManager.getCommandPrefix());
				for (Node child : this.getChildren()) {
					if (child instanceof Mutator) {
						Mutator mutator = (Mutator) child;
						if (mutator.isEnabled()) {
							if (serverCommand && !mutator.modifyServerCommands())
								continue;
							if (clientCommand && !mutator.modifyClientCommands())
								continue;
							message = mutator.mutate(message);
						}
					}
				}
				event.setPacket(new CPacketChatMessage(message));
			}
		}
	}

}
