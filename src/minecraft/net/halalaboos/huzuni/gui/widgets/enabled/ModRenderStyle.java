package net.halalaboos.huzuni.gui.widgets.enabled;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.settings.Nameable;

/**
 * Used to stylize the mods rendered within the enabled mods widget.
 * */
public interface ModRenderStyle extends Nameable {

	/**
	 * Renders the mod name.
	 * */
	void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1);

	/**
	 * Formats the mod name according to this style.
	 * */
	String formatName(Mod mod);

	/**
	 * @return The width this mod name will have.
	 * */
	int getModWidth(Theme theme, Mod mod);

	/**
	 * @return True if the given mod will be rendered by this style.
	 * */
	boolean shouldRender(Mod mod);
	
}
