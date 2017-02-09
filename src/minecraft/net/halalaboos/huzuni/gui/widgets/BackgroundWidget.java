package net.halalaboos.huzuni.gui.widgets;

import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.gui.widget.Widget;
import net.halalaboos.huzuni.api.settings.Toggleable;

/**
 * Abstract Widget implementation that contains a background node and premade rendering for the background node.
 * */
public abstract class BackgroundWidget extends Widget {

	protected final Toggleable background = new Toggleable("Background", "Renders a background on the component");
	
	public BackgroundWidget(String name, String description, WidgetManager menuManager) {
		super(name, description, menuManager);
		this.addChildren(background);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		if (background.isEnabled())
			theme.drawBackgroundRect(x, y, width, height, false);
	}

}
