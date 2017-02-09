package net.halalaboos.huzuni.api.mod;

/**
 * Categorization used within mods.
 * */
public enum Category {
	NONE("None"),
	VISUAL("Visual"),
	COMBAT("Combat"),
	MISC("Misc"),
	MOVEMENT("Movement"),
	MINING("Mining");
	
	public final String formalName;
	
	Category(String formalName) {
		this.formalName = formalName;
	}
}
