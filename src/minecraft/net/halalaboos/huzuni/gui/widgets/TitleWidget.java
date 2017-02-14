package net.halalaboos.huzuni.gui.widgets;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.WidgetManager;

public class TitleWidget extends BackgroundWidget {

	public TitleWidget(WidgetManager menuManager) {
		super("Title", "Render the client title", menuManager);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
		String huzuniVersion = "Huzuni " + Huzuni.VERSION;
		theme.drawStringWithShadow(huzuniVersion, x, y, 0xFFFFFF);
		this.setWidth(theme.getStringWidth(huzuniVersion) + 2);
		this.setHeight(theme.getStringHeight(huzuniVersion));
	}

}
