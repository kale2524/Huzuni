package net.halalaboos.huzuni.gui.widgets.enabled;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.BasicKeybind;
import net.halalaboos.huzuni.api.mod.Mod;

/**
 * The old-school style.
 * */
public class OldSchoolStyle implements ModRenderStyle {

	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		name = formatName(mod, glue.isRight());
		theme.drawStringWithShadow(name, glue.isRight() ? x1 - theme.getStringWidth(name) : x, y, mod.isEnabled() ? 0x00FF00 : 0xFF0000);
	}

	@Override
	public boolean shouldRender(Mod mod) {
		BasicKeybind keybind = Huzuni.INSTANCE.modManager.getKeybind(mod);
		return keybind != null && keybind.isBound() && mod.settings.isDisplayable();
	}

	@Override
	public String getName() {
		return "Old School";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String formatName(Mod mod) {
		return formatName(mod, false);
	}
	
	private String formatName(Mod mod, boolean right) {
		BasicKeybind keybind = Huzuni.INSTANCE.modManager.getKeybind(mod);	
		return right ? mod.getDisplayNameForRender() + " [" + keybind.getKeyName() + "]" : "[" + keybind.getKeyName() + "] " + mod.getDisplayNameForRender();
	}

	@Override
	public int getModWidth(Theme theme, Mod mod) {
		return theme.getStringWidth(formatName(mod));
	}

}
