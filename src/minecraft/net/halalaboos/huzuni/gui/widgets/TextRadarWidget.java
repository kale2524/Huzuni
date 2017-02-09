package net.halalaboos.huzuni.gui.widgets;


import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.settings.Value;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

/**
 * Renders the names of players within a given range.
 * */
public class TextRadarWidget extends BackgroundWidget {

	private final Value distance = new Value("Distance", "", 10F, 130F, 255F, 5F, "Distance required for entities to be rendered.");
	
	public TextRadarWidget(WidgetManager menuManager) {
		super("Text Radar", "Render an old-school text radar", menuManager);
		this.addChildren(distance);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
		int incrementOffset = getIncrementOffset(), originalWidth = width;;
		height = 0;
		width = 0;
		if (incrementOffset == -1)
			y = y + height - theme.getStringHeight("minimum");
		
		for (int i = 0; i < mc.world.playerEntities.size(); i++) {
			EntityPlayer player = mc.world.playerEntities.get(i);
			if (mc.player != player) {
				float distance = MathHelper.sqrt((float) (mc.player.posX - player.posX) * (float) (mc.player.posX - player.posX) + (float) (mc.player.posZ - player.posZ) * (float) (mc.player.posZ - player.posZ));
				if (distance < this.distance.getValue()) {
					String text = String.format("%s (%d)", player.getDisplayName().getFormattedText(), (int) distance);
					int textWidth = theme.getStringWidth(text);
					theme.drawStringWithShadow(text, getOffsetX(x, x + originalWidth, textWidth), y, 0xFFFFFF);
					height += theme.getStringHeight(text);
					y += incrementOffset * theme.getStringHeight(text);
					if (textWidth + 2 > width)
						width = textWidth + 2;
				}
			}
		}
		this.setWidth(width);
		this.setHeight(height <= 0 ? 10 : height);
	}
	
	private int getOffsetX(int x, int x1, int itemWidth) {
		return glue.isRight() ? x1 - itemWidth - 2 : x;
	}
	
	private int getIncrementOffset() {
		return glue.isBottom() ? -1 : 1;
	}

}
