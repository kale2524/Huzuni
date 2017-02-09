package net.halalaboos.huzuni.settings;

import java.io.IOException;

import net.halalaboos.huzuni.api.gui.components.tree.TreeComponentFactory;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.gui.tree.components.TeamComponent;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import com.google.gson.JsonObject;

/**
 * Node that holds the player's team color and is used to determine the team color of other players.
 * */
public class Team extends Node {

	private boolean enabled;
	
	private int teamIndex = 0;
	
	private final int[] colorCode = new int[32];
	
    private final String colorcodeIdentifiers = "0123456789abcdef";
    	
    public Team() {
    	super("Team", "Avoid attacking your teammates by designating your team color");
    	for (int index = 0; index < 32; ++index) {
    		int offset = (index >> 3 & 1) * 85;
    		int red = (index >> 2 & 1) * 170 + offset;
    		int green = (index >> 1 & 1) * 170 + offset;
    		int blue = (index >> 0 & 1) * 170 + offset;

    		if (index == 6) {
    			red += 85;
    		}

    		if (index >= 16) {
    			red /= 4;
    			green /= 4;
    			blue /= 4;
    		}

    		this.colorCode[index] = (red & 255) << 16 | (green & 255) << 8 | blue & 255;
    	}
		TreeComponentFactory.addComponent(Team.class, TeamComponent.class);
    }
    
	@Override
	public void save(JsonObject object) throws IOException {
		object.addProperty(getName(), teamIndex);
	}

	@Override
	public void load(JsonObject object) throws IOException {
		if (isObject(object)) {
			teamIndex = object.get(getName()).getAsNumber().intValue();
		}
	}
	
	public boolean isTeam(Entity entity) {
		return hasTeamColor(entity.getDisplayName().getFormattedText());
	}
	
	public boolean hasTeamColor(String text) {
		return text.contains("\247" + colorcodeIdentifiers.charAt(teamIndex));
	}

	/**
	 * @return the hexadecimal color representing the team of the given entity. Returns -1 if no team could be found.
	 * */
	public int getTeamColor(Entity entity) {
		String[] split = entity.getDisplayName().getFormattedText().split("\247");
		if (split != null) {
			for (String parse : split) {
				if (parse.length() > 1) {
					String colorCode = parse.substring(0, 1);
					int index = colorcodeIdentifiers.indexOf(colorCode);
					if (index == -1) {
						continue;
					} else {
						if (parse.substring(1).equalsIgnoreCase(entity.getName()))
							return this.colorCode[index];
						else
							continue;
					}
				}
			}
		}
		return -1;
	}

	/**
	 * @return the hexadecimal color representing the team this node has selected.
	 * */
	public int getColor() {
		return colorCode[teamIndex];
	}
	
	public int getColor(int index) {
		return colorCode[index];
	}

	/**
	 * @return the hexadecimal color representing the team found within the given entities name.
	 * */
	public int getColor(String name) {
		for (TextFormatting format : TextFormatting.values()) {
			if (format.getFriendlyName().equals(name))
				return colorCode[format.getColorIndex()];
		}
		return -1;
	}
    
	public void incrementTeam() {
			teamIndex++;

		if (teamIndex < 0)
			teamIndex = 15;
		if (teamIndex > 15)
			teamIndex = 0;
	}
	
	public void decrementTeam() {
		teamIndex--;
		
		if (teamIndex < 0)
			teamIndex = 15;
		if (teamIndex > 15)
			teamIndex = 0;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public int getTeamIndex() {
		return teamIndex;
	}
	
	public boolean hasSelected() {
		return teamIndex != -1;
	}

	public void setTeamIndex(int teamIndex) {
		this.teamIndex = teamIndex;
	}

	public void toggle() {
		enabled = !enabled;
	}
}
