package net.halalaboos.huzuni;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.halalaboos.huzuni.api.settings.JsonFileHandler;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.api.settings.ColorNode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.settings.KeyOpenMenu;
import net.halalaboos.huzuni.settings.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

/**
 * Holds the global settings used within the mods. <br/>
 * This could be considered a manager for the settings of the client.
 * */
public final class HuzuniSettings extends JsonFileHandler {
	
	public final Node lineSettings = new Node("Line settings", "Adjust the settings applied to line rendering.");
	
	public final Value lineSize = new Value("Line Size", "", 0.2F, 1F, 10F, "Thickness of lines rendered in 3D");
	
	public final Toggleable lineSmooth = new Toggleable("Line Smooth", "Renders 3D lines smoothly");
	
	public final Toggleable infiniteLines = new Toggleable("Infinite Lines", "Render lines through the near plane");

	public final Node menuSettings = new Node("Menu settings", "Adjust the settings applied to the menu.");
	
	public final Toggleable customChat = new Toggleable("Custom Chat Font", "Renders the chat with a custom font");
	
	public final Toggleable customFont = new Toggleable("Custom Menu Font", "Renders the menus with a custom font");
	
	public final Toggleable firstUse = new Toggleable("First Use", "Determines if this is the first use of the client!");

	public final ColorNode menuColor = new ColorNode("Menu Color", new Color(0, 167, 255, 255), "Primary color rendered over the gui");
	
	public final Team team = new Team();
	
	public final KeyOpenMenu keyOpenMenu = new KeyOpenMenu();
		
	private Session lastSession = null;
	
	private String newestVersion = "";
		
	public HuzuniSettings(Huzuni huzuni) {
		super(huzuni, null);
	}

	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		JsonObject object = new JsonObject();
		lineSettings.save(object);
		menuSettings.save(object);
		team.save(object);
		huzuni.lookManager.save(object);
		huzuni.hotbarManager.save(object);
		keyOpenMenu.save(object);
		firstUse.save(object);
		saveSession(objects, lastSession);
		objects.add(object);
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		if (!loadSession(object)) {
			lineSettings.load(object);
			menuSettings.load(object);
			team.load(object);
			keyOpenMenu.load(object);
			firstUse.load(object);
			huzuni.lookManager.load(object);
			huzuni.hotbarManager.load(object);
		}
	}
	
	public void init() {
		lineSmooth.setEnabled(true);
		infiniteLines.setEnabled(true);
		customFont.setEnabled(true);
		firstUse.setEnabled(true);
		lineSettings.addChildren(lineSize, lineSmooth, infiniteLines);
		menuSettings.addChildren(customChat, customFont, huzuni.guiManager.getThemes(), menuColor);
		try {
			UUID.fromString(Minecraft.getMinecraft().getSession().getPlayerID());
			lastSession = Minecraft.getMinecraft().getSession();			
		} catch (Exception e) {	
		}
	}

	/**
	 * Saves session information into a JsonObject
	 * */
	private void saveSession(List<JsonObject> objects, Session session) {
		JsonObject object = new JsonObject();
		if (session != null) {
			object.addProperty("username", session.getUsername());
			object.addProperty("uuid", session.getPlayerID());
			object.addProperty("token", session.getToken());
			object.addProperty("sessionId", session.getSessionID());
			objects.add(object);
		}
	}

	/**
	 * Loads session information from a JsonObject
	 * */
	private boolean loadSession(JsonObject object) {
		JsonElement usernameElement = object.get("username");
		if (usernameElement != null) {
			String name = usernameElement.getAsString();
			String uuid = object.get("uuid").getAsString();
			String token = object.get("token").getAsString();
			String sessionId = object.get("sessionId").getAsString();
			lastSession = new Session(name, uuid, token, sessionId);
			return true;
		}
		return false;
	}
	
	public Color getPrimaryColor() {
		return menuColor.getColor();
	}

	public Session getLastSession() {
		return lastSession;
	}

	public void setLastSession(Session lastSession) {
		this.lastSession = lastSession;
	}

	public String getNewestVersion() {
		return newestVersion;
	}

	public void setNewestVersion(String newestVersion) {
		this.newestVersion = newestVersion;
	}
	
	/**
	 * @return True if the client version is out of date.
	 * */
	public boolean hasUpdate() {
		return !newestVersion.isEmpty() && !newestVersion.equals(Huzuni.VERSION);
	}
}
