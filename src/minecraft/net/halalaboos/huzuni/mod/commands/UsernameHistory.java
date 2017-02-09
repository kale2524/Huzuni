package net.halalaboos.huzuni.mod.commands;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mojang.authlib.GameProfile;

import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.util.FileUtils;
import net.minecraft.util.text.TextFormatting;

public final class UsernameHistory extends BasicCommand {

	public UsernameHistory() {
		super(new String[] { "usernamelookup", "lookup" }, "Looks up a player's username history.");
	}
	
	@Override
	public void giveHelp() {
		huzuni.addChatMessage(".usernamelookup <username>");
	}
	
	@Override
	protected void runCommand(String input, String[] args) {
		final String name = args[0];
		new Thread() {
			@Override
			public void run() {
				try {
					String uuid = grabUUID(name);
					String names = FileUtils.readURL(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names"));
					if (names.isEmpty()) {
						huzuni.addChatMessage(name + " has had no username changes.");
					} else {
						Collection<GameProfile> profiles = new Gson().fromJson(names, new TypeToken<Collection<GameProfile>>(){}.getType());
						String output = "";
						for (GameProfile profile : profiles)
							output += "\"" + profile.getName() + "\", ";
						huzuni.addChatMessage(TextFormatting.GOLD + name + TextFormatting.RESET + " has had the usernames: " + output.substring(0, output.length() - 2) + ".");
					}
				} catch (Exception e) {
					huzuni.addChatMessage("Failed to look up user.");
					e.printStackTrace();
				}
			}
		}.start();
	}

	private String grabUUID(String name) {
		try {
			String userInfo = FileUtils.readURL(new URL("https://api.mojang.com/users/profiles/minecraft/" + name));
			Map<String, Object> output = new Gson().fromJson(userInfo, new TypeToken<Map<String, Object>>(){}.getType());
			return output.get("id").toString();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
