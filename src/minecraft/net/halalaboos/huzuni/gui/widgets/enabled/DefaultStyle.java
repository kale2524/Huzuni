package net.halalaboos.huzuni.gui.widgets.enabled;


import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.Mod;

/**
 * The default style used to render mods.
 * */
public class DefaultStyle implements ModRenderStyle {

	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		theme.drawStringWithShadow(name, glue.isRight() ? x1 - theme.getStringWidth(name) : x, y, color);
	}

	@Override
	public boolean shouldRender(Mod mod) {
		return mod.isEnabled() && mod.settings.isDisplayable();
	}
	
	@Override
	public String getName() {
		return "Default";
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
