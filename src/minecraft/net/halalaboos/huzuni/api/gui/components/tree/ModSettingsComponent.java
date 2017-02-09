package net.halalaboos.huzuni.api.gui.components.tree;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.mod.ModSettings;

public class ModSettingsComponent extends NodeTreeComponent <ModSettings> {

	public ModSettingsComponent(ModSettings node) {
		super(node);
		setHeight(12);
	}

	@Override
	public void mouseClicked(Theme theme, int x, int y, int width, int height, int mouseX, int mouseY, int buttonId) {
		expanded = !expanded;
	}

}
