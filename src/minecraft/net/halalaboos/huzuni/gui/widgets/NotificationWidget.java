package net.halalaboos.huzuni.gui.widgets;

import java.util.List;

import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.gui.widget.Widget;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.halalaboos.huzuni.gui.Notification;

/**
 * Widget which displays the notifications received from the mod.
 * */
public class NotificationWidget extends Widget {

	private final List<Notification> notifications;
	
	public NotificationWidget(WidgetManager menuManager, List<Notification> notifications) {
		super("Notifications", "Renders in-game notifications", menuManager);
		this.notifications = notifications;
		this.setWidth(100);
		this.setHeight(50);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		calculateSize();
		int notificationPadding = 2, incrementOffset = getIncrementOffset();
		for (int i = 0; i < notifications.size(); i++) {
			Notification notification = notifications.get(i);
			if (i == 0) {
				if (incrementOffset == -1)
					y = y + height - notification.getHeight();
			}
			if (notification.hasExpired()) {
				notifications.remove(i);
			} else {
				int notificationHeight = notification.getHeight(), notificationWidth = notification.getWidth(theme);
				notification.getPosition().setFinalPosition(notificationWidth, 0, 0);
				notification.getPosition().updatePosition();

				int renderX = (int) (glue.isRight() ? x + width - notification.getPosition().getX() : x - notificationWidth + notification.getPosition().getX());
				int renderY = y;
				
				float percent = (float) (Timer.getSystemTime() - notification.getTime()) / (float) notification.getDuration();
				theme.drawBackgroundRect(renderX, renderY, notificationWidth, notificationHeight, false);
				
				GLManager.glColor(notification.getType().color, 0.7F);
				RenderUtils.drawRect(renderX, renderY, renderX + Notification.ICON_SIZE, renderY + Notification.ICON_SIZE);
				theme.drawStringWithShadow(notification.getType().text, renderX + notification.getType().offsetX, renderY + notification.getType().offsetY, 0xFFFFFF);
				
				for (int j = 0; j < notification.getMessage().length; j++) {
					theme.drawStringWithShadow(notification.getMessage()[j], renderX + (j == 0 ? Notification.ICON_SIZE + 4 : 3), renderY, 0xFFFFFF);
					renderY += 12;
				}
				
				GLManager.glColor(0F, 1F, 0F, 0.7F);
				RenderUtils.drawRect(renderX, renderY, renderX + notificationWidth * (1F - percent), renderY + 1);
			
				y += incrementOffset * (notificationHeight + notificationPadding);
			}
		}
	}

	/**
     * Keeps the widget size relative to the notifications size.
     * */
	private void calculateSize() {
		int width = 100, height = 12, notificationPadding = 2;
		for (int i = 0; i < notifications.size(); i++) {
			Notification notification = notifications.get(i);
			if (!notification.hasExpired()) {
				if (notification.getWidth(theme) > width)
					this.width = notification.getWidth(theme);
				height += notification.getHeight() + notificationPadding;
			}
		}
		this.setWidth(width);
		this.setHeight(height);
	}
	
	private int getIncrementOffset() {
		return glue.isBottom() ? -1 : 1;
	}

}
