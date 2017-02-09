package net.halalaboos.huzuni.mod.mining.templates;

public class PortalTemplate extends BasicTemplate {
	
	public PortalTemplate() {
		super("Portal", new int[] {
				0, 0,
				-1, 0,
				1, 0,
				2, 0,
				
				-1, 1,
				2, 1,
				
				-1, 2,
				2, 2,
				
				-1, 3,
				2, 3,
				
				-1, 4,
				0, 4,
				1, 4,
				2, 4
		});
	}
}
