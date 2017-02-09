package net.halalaboos.huzuni.gui.tree.components;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTreeComponent;
import net.halalaboos.huzuni.settings.Team;

/**
 * NodeTreeComponent used for the Team node.
 * */
public class TeamComponent extends NodeTreeComponent <Team> {

	public static final int COLOR_SIZE = 12;
	
	public TeamComponent(Team node) {
		super(node);
		setHeight(COLOR_SIZE);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		if (mouseX > x && mouseX < x + COLOR_SIZE && mouseY > y && mouseY < y + COLOR_SIZE) {
			if (buttonId == 0) {
				node.incrementTeam();
			} else if (buttonId == 1) {
				node.decrementTeam();
			}
		} else if (mouseX > x + COLOR_SIZE && mouseX < x + width && mouseY > y && mouseY < y + height) {
			node.toggle();			
		}
	}

}
