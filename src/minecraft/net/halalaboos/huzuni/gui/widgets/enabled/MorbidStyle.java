package net.halalaboos.huzuni.gui.widgets.enabled;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.Mod;

/**
 * The morbid render style.
 * */
public class MorbidStyle implements ModRenderStyle {

	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		theme.drawStringWithShadow(name, glue.isRight() ? x1 - theme.getStringWidth(name) : x, y, 0xFFFFFF);
	}

	@Override
	public boolean shouldRender(Mod mod) {
		return mod.isEnabled() && mod.settings.isDisplayable();
	}

	@Override
	public String getName() {
		return "Morbid";
	}


	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String formatName(Mod mod) {
		return "\2470[\247r" + mod.getDisplayNameForRender() + "\2470]";
	}

	@Override
	public int getModWidth(Theme theme, Mod mod) {
		return theme.getStringWidth(formatName(mod));
	}

}
