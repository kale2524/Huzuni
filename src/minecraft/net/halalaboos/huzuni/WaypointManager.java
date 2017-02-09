package net.halalaboos.huzuni;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.event.EventLoadWorld;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.settings.ColorNode;
import net.halalaboos.huzuni.api.settings.ItemList;
import net.halalaboos.huzuni.api.settings.JsonFileHandler;
import net.halalaboos.huzuni.api.settings.Nameable;
import net.halalaboos.huzuni.api.util.MinecraftUtils;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;

/**
 * Manages and allows easy access to all {@link Waypoint}s.
 * */
public final class WaypointManager extends JsonFileHandler {
	
	private final ItemList<Waypoint> waypoints = new ItemList<Waypoint>("Waypoints", "List of all waypoints") {
		@Override
		protected void saveItem(JsonObject object, Waypoint item) {
			object.addProperty("name", item.getName());
			object.addProperty("server", item.getServer());
			object.addProperty("x", item.getPosition().getX());
			object.addProperty("y", item.getPosition().getY());
			object.addProperty("z", item.getPosition().getZ());
			object.addProperty("color", String.valueOf(item.getColor().getRGB()));
		}

		@Override
		protected Waypoint loadItem(JsonObject object) {
			String name = object.get("name").getAsString(), server = object.get("server").getAsString();
			int x = object.get("x").getAsInt(), y = object.get("y").getAsInt(), z = object.get("z").getAsInt();
			Color color = new Color(object.get("color").getAsInt());
			return new Waypoint(name, server, new BlockPos(x, y, z), color);
		}
	
	};
	
	private int deathCount = 0;
	
	public WaypointManager(Huzuni huzuni) {
		super(huzuni, null);
	}
	
	@Override
	public void init() {
		huzuni.eventManager.addListener(this);
	}

	@Override
	protected void save(List<JsonObject> objects) throws IOException {
		JsonObject object = new JsonObject();
		waypoints.save(object);
		objects.add(object);
	}

	@Override
	protected void load(JsonObject object) throws IOException {
		if (waypoints.isObject(object))
			waypoints.load(object);
	}
	
	/**
	 * Attempts to remove a {@link Waypoint} from the list of waypoints based on it's name.
	 * @param name The name of the {@link Waypoint} to be removed.
	 * @return True if the {@link Waypoint} has been successfully removed.
	 * */
	public boolean removeWaypoint(String name) {
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint waypoint = waypoints.get(i);
			if (waypoint.isOnServer() && waypoint.getName().equalsIgnoreCase(name)) {
				waypoints.remove(waypoint);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Attempts to add a {@link Waypoint} to the list of waypoints.
	 * @param newWaypoint The {@link Waypoint} to be added.
	 * @return True if the {@link Waypoint} has been successfully added.
	 * */
	public boolean addWaypoint(Waypoint newWaypoint) {
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint waypoint = waypoints.get(i);
			if (waypoint.isOnServer() && waypoint.getName().equalsIgnoreCase(newWaypoint.getName())) {
				return false;
			}
		}
		waypoints.add(newWaypoint);
		return true;
	}
	
	/**
	 * Adds a death waypoint at the given position.
	 * @param position The position at which to add the death {@link Waypoint}.
	 * */
	public void addDeathPoint(BlockPos position) {
		this.addDeathPoint(deathCount, position);
	}
	
	/**
	 * Adds a death {@link Waypoint} at the given position. Recursive function to ensure that each death {@link Waypoint} has a unique name.
	 * @param deathCount The amount of times the user has died.
	 * @param position The position at which to add the death {@link Waypoint}.
	 * */
	private void addDeathPoint(int deathCount, BlockPos position) {
		String name = "Death Point " + deathCount;
		if (addWaypoint(new Waypoint(name, position, Color.RED))) {
			this.deathCount = deathCount + 1;
		} else
			addDeathPoint(deathCount + 1, position);
	}
	
	/**
	 * @return A list of {@link Waypoint}s on the current server.
	 * */
	public List<Waypoint> getLocalWaypoints() {
		List<Waypoint> locals = new ArrayList<Waypoint>();
		for (int i = 0; i < waypoints.size(); i++) {
			Waypoint waypoint = waypoints.get(i);
			if (waypoint.isOnServer()) {
				locals.add(waypoint);
			}
		}
		return locals;
	}
	
	/**
	 * Clears all local {@link Waypoint}s.
	 * @return The amount of {@link Waypoint}s remove.
	 * */
	public int clearLocalWaypoints() {
		List<Waypoint> localWaypoints = getLocalWaypoints();
		int count = 0;
		for (Waypoint waypoint : localWaypoints) {
			if (waypoints.remove(waypoint))
				count++;
		}
		return count;
	}
	
	@EventMethod
	public void onWorldLoad(EventLoadWorld event) {
		deathCount = 0;
	}
	
	public ItemList<Waypoint> getWaypoints() {
		return waypoints;
	}

    /**
     * Contains waypoint information including name, server, position, and color.
     * */
    public static class Waypoint implements Nameable {

        private String name;

        private String server;

        private BlockPos position;

        private ColorNode color = new ColorNode("color", Color.WHITE, "waypoint color");

        public Waypoint(String name, String server, BlockPos position, Color color) {
            this.name = name;
            this.server = server;
            this.position = position;
            this.color.setColor(color);
        }

        public Waypoint(String name, BlockPos position, Color color) {
            this(name, MinecraftUtils.getCurrentServer(), position, color);
        }

        public Waypoint(String name, BlockPos position) {
            this(name, MinecraftUtils.getCurrentServer(), position, GLManager.getRandomColor());
        }

        public Waypoint(BlockPos position) {
            this("Waypoint " + (int) (Math.random() * 500D), position);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return String.format("%s @ (%d, %d, %d)", server, position.getX(), position.getY(), position.getZ());
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public BlockPos getPosition() {
            return position;
        }

        public void setPosition(BlockPos position) {
            this.position = position;
        }

        /**
         * @return True if the waypoint was placed on the current server.
         * */
        public boolean isOnServer() {
            return server.equals(MinecraftUtils.getCurrentServer());
        }

        /**
         * @return The distance between this location and the player.
         * */
        public float getDistance() {
            return Minecraft.getMinecraft().player == null ? 0F : (float) Minecraft.getMinecraft().player.getDistance(position.getX(), position.getY(), position.getZ());
        }

        public Color getColor() {
            return color.getColor();
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
