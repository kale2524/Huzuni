package net.halalaboos.huzuni;

import java.awt.Color;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.settings.JsonFileHandler;

/**
 * Manages friends added by the user.
 * */
public final class FriendManager extends JsonFileHandler {
	
	private static final Color color = new Color(0x59BFFF);
	
	private final Map<String, String> friends = new HashMap<String, String>();
	
	public FriendManager(Huzuni huzuni) {
		super(huzuni, null);
	}
	
	@Override
	public void init() {
	}

	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		for (String username : friends.keySet()) {
			JsonObject object = new JsonObject();
			object.addProperty("name", username);
			object.addProperty("alias", friends.get(username));
			objects.add(object);
		}
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		String username = object.get("name").getAsString();
		String alias = object.get("alias").getAsString();
		addFriend(username.toLowerCase(), alias);
	}

	/**
	 * Adds the username to the friends list with the given alias.
	 * */
	public void addFriend(String username, String alias) {
		friends.put(username.toLowerCase(), alias);
	}

	/**
     * Adds the username to the friends list with no alias (blank string).
     * */
	public void addFriend(String username) {
		addFriend(username.toLowerCase(), "");
	}
	
	public void removeFriend(String username) {
		friends.remove(username.toLowerCase());
	}

	/**
	 * @return True if the username is within the friends list.
	 * */
	public boolean isFriend(String username) {
		return friends.containsKey(username.toLowerCase());
	}

	/**
     * @return True if the username has a custom alias.
     * */
	public boolean hasAlias(String username) {
		return !friends.get(username.toLowerCase()).isEmpty();
	}

	/**
     * @return The alias associated with the given username or the username if none can be found.
     * */
	public String getAlias(String username) {
		String alias = friends.get(username.toLowerCase());
		return alias.isEmpty() ? username : alias;
	}
	
	public Color getColor() {
		return color;
	}
}
