package net.halalaboos.huzuni.gui.widgets;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.halalaboos.huzuni.api.gui.WidgetManager;

/***
 * Renders the computer's clock time within the game.
 */
public class TimeWidget extends BackgroundWidget {
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
	
	public TimeWidget(WidgetManager menuManager) {
		super("Time", "Render the current time", menuManager);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
        String date = dateFormat.format(new Date());
        theme.drawStringWithShadow(date, x, y, 0xFFFFFF);
		this.setWidth(theme.getStringWidth(date) + 2);
		this.setHeight(theme.getStringHeight(date));
	}
}
