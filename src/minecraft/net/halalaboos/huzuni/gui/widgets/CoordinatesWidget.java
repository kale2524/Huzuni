package net.halalaboos.huzuni.gui.widgets;

import net.halalaboos.huzuni.api.gui.WidgetManager;

/**
 * Widget which displays the player's coordinates.
 * */
public class CoordinatesWidget extends BackgroundWidget {

	public CoordinatesWidget(WidgetManager menuManager) {
		super("Coordinates", "Render players coordinates", menuManager);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
		String coordinates = String.format("( %d, %d, %d )", (int) mc.player.posX, (int) mc.player.posY, (int) mc.player.posZ);
		theme.drawStringWithShadow(coordinates, x, y, 0xFFFFFF);
		this.setWidth(theme.getStringWidth(coordinates) + 2);
		this.setHeight(theme.getStringHeight(coordinates));
	}
}
