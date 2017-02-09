package net.halalaboos.huzuni;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.mod.CommandPointer;
import net.halalaboos.huzuni.api.settings.JsonFileHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;

/**
 * Manages player capes within the client. Saves player uuids with a cape into a json file. Anyone can easily modify this json file and that is a-okay!
 * */
public final class CapeManager extends JsonFileHandler {

	private final Object lock = new Object();
	
	private final String url = "http://halalaboos.net/huzuni/getcape.php?";
	
	private final ResourceLocation capeLocation = new ResourceLocation("huzuni/default.png");
	
	private final Map<String, ResourceLocation> capes = new HashMap<String, ResourceLocation>();
	
	private final Map<String, CheckCapeThread> threads = new HashMap<String, CheckCapeThread>();

	public CapeManager(Huzuni huzuni) {
		super(huzuni, null);
		huzuni.commandManager.generateCommands(this);
	}

	/**
     * @return The resource location for the player's cape.
     * */
	public ResourceLocation getPlayerCape(UUID uuid) {
		synchronized (lock) {
			String formatted = formatUUID(uuid);
			if (capes.containsKey(formatted)) {
				return capes.get(formatted);
			} else {
				if (!threads.containsKey(formatted)) {
					CheckCapeThread thread = new CheckCapeThread(formatted);
					threads.put(formatted, thread);
					thread.start();
				}
				return null;
			}
		}
	}
	
	@CommandPointer(description = "Reloads the player's cape!", value = { "reloadcape" })
	public void reloadCape(EntityPlayer player) {
		if (player != null) {
			String formatted = formatUUID(player.getUniqueID());
			synchronized (lock) {
				if (!threads.containsKey(formatted)) {
					CheckCapeThread thread = new CheckCapeThread(formatted);
					threads.put(formatted, thread);
					thread.start();
					huzuni.addChatMessage(String.format("Reloading %s's cape!", player.getName()));
				}
			}
		} else
			huzuni.addChatMessage("Player not found!");
	}

	@Override
	public void init() {
	}

	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		for (String uuid : capes.keySet()) {
			if (capes.get(uuid) != null) {
				JsonObject object = new JsonObject();
				object.addProperty("uuid", uuid);
				object.addProperty("cape", capes.get(uuid) == null ? 0 : 1);
				objects.add(object);
			}
		}
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		capes.put(object.get("uuid").getAsString(), capeLocation);
	}

	/**
	 * Connects to the webserver and updates the cape manager with the cape information for the uuid provided.
	 * */
	private class CheckCapeThread extends Thread {

		private final String uuid;

		private CheckCapeThread(String uuid) {
			this.uuid = uuid;
		}

		@Override
		public void run() {
			try {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("uuid", uuid);
				int response = Integer.parseInt(HttpUtil.postMap(new URL(url), map, false, Minecraft.getMinecraft().getProxy()).trim());
				synchronized (lock) {
					if (response == 1) {
						capes.put(uuid, capeLocation);
					} else {
						capes.put(uuid, null);
					}
				}
			} catch (Exception e) {
				synchronized (lock) {
					capes.put(uuid, null);
				}
			}
			synchronized (lock) {
				threads.remove(uuid);
			}
		}
	}

	/**
	 * Removes the '-' characters from the UUID string.
	 * */
	private String formatUUID(UUID uuid) {
		return uuid.toString().replaceAll("-", "");
	}
}
