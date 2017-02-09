package net.halalaboos.huzuni.mod.misc;

import net.halalaboos.huzuni.api.event.EventMouseClick;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.mod.Mod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;

/**
 * Allows the player to middle click other players to add them as a friend.
 * */
public class MiddleClickFriends extends Mod {
	
	public MiddleClickFriends() {
		super("Middle click friends", "Middle clicking players allows you to add/remove them to and from the friends list");
		this.setCategory(Category.MISC);
		this.setEnabled(true);
		this.settings.setDisplayable(false);
	}

	@Override
	protected void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	protected void onDisable() {
		huzuni.eventManager.removeListener(this);
	}
	
	@EventMethod
	public void onMouseClicked(EventMouseClick event) {
		if (event.buttonId == 2) {
			if (mc.objectMouseOver != null) {
				if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.ENTITY && mc.objectMouseOver.entityHit instanceof EntityPlayer) {
					if (huzuni.friendManager.isFriend(mc.objectMouseOver.entityHit.getName())) {
						huzuni.addChatMessage(String.format("Removed %s as a friend.", mc.objectMouseOver.entityHit.getName()));
						huzuni.friendManager.removeFriend(mc.objectMouseOver.entityHit.getName());
						huzuni.friendManager.save();
					} else {
						huzuni.friendManager.addFriend(mc.objectMouseOver.entityHit.getName());
						huzuni.addChatMessage(String.format("Added %s as a friend.", mc.objectMouseOver.entityHit.getName()));
						huzuni.friendManager.save();
					}
				}
			}
		}
	}
	
}
