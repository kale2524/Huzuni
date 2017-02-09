package net.halalaboos.huzuni.gui.widgets;

import net.halalaboos.huzuni.api.gui.WidgetManager;

public class FacingWidget extends BackgroundWidget {
		
	public FacingWidget(WidgetManager menuManager) {
		super("Facing", "Render which direction the player is facing (based on x/z)", menuManager);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
        String facing = "Invalid";
        switch (mc.player.getHorizontalFacing()) {
            case NORTH:
                facing = "Towards negative Z";
                break;

            case SOUTH:
                facing = "Towards positive Z";
                break;

            case WEST:
                facing = "Towards negative X";
                break;

            case EAST:
                facing = "Towards positive X";
		default:
			break;
        }
        theme.drawStringWithShadow(facing, x, y, 0xFFFFFF);
		this.setWidth(theme.getStringWidth(facing) + 2);
		this.setHeight(theme.getStringHeight(facing));
	}
}
