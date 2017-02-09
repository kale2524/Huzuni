/**
 *
 */
package net.halalaboos.huzuni.gui.widgets.enabled;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.Mod;
import net.minecraft.client.renderer.GlStateManager;


/**
 * The iridium render style.
 * @author Halalaboos
 * @since Sep 22, 2013
 */
public class IridiumStyle implements ModRenderStyle {
	
	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		GlStateManager.enableBlend();
		GlStateManager.translate(0.5F, 0.5F, 0);
		theme.drawString(name, glue.isRight() ? x1 - theme.getStringWidth(name) : x, y, ((color >> 24 & 0xFF) << 24));
		GlStateManager.translate(-0.5F, -0.5F, 0);
		theme.drawString(name, glue.isRight() ? x1 - theme.getStringWidth(name) : x, y, color);
		GlStateManager.disableBlend();
	}

	@Override
	public boolean shouldRender(Mod mod) {
		return mod.isEnabled() && mod.settings.isDisplayable();
	}

	@Override
	public String getName() {
		return "Iridium";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String formatName(Mod mod) {
		return mod.getDisplayNameForRender();
	}

	@Override
	public int getModWidth(Theme theme, Mod mod) {
		return theme.getStringWidth(formatName(mod));
	}
}
