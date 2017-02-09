package net.halalaboos.huzuni.gui.widgets.enabled;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;

/**
 * The nyan render style.
 * */
public class NyanStyle implements ModRenderStyle {

	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		GLManager.glColor(0x80000000);
	    RenderUtils.drawRect(glue.isRight() ? x : x + 2, y, glue.isRight() ? x1 - 2 : x1, y + 11);
	    GLManager.glColor(color);
	    RenderUtils.drawRect(glue.isRight() ? x1 - 2 : x, y, glue.isRight() ? x1 : x + 2, y + 11);
		theme.drawString(name, glue.isRight() ? x1 - theme.getStringWidth(name) - 2 : x + 4, y, color);
	}

	@Override
	public boolean shouldRender(Mod mod) {
		return mod.isEnabled() && mod.settings.isDisplayable();
	}
	
	@Override
	public String getName() {
		return "Nyan";
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
		return 6 + theme.getStringWidth(formatName(mod));
	}
	
}
