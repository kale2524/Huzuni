package net.halalaboos.huzuni.mod.mining.templates;

public class SwastikaTemplate extends BasicTemplate  {

	public SwastikaTemplate() {
		super("Swastika", new int[] {
				0, 0,
				-1, 0,
				-2, 0,
				2, 0,
				
				0, 1,
				2, 1,
				
				0, 2,
				-1, 2,
				-2, 2,
				1, 2,
				2, 2,
				
				-2, 3,
				0, 3,
				
				-2, 4,
				0, 4,
				1, 4,
				2, 4
		});
	}
}
