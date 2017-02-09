package net.halalaboos.huzuni.gui.widgets.enabled;


import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.BasicKeybind;
import net.halalaboos.huzuni.api.mod.Mod;
import net.minecraft.util.text.TextFormatting;

/**
 * The skidded style.
 * */
public class SkiddedStyle implements ModRenderStyle {

	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		name = formatName(mod, glue.isRight());
        theme.drawStringWithShadow(name, glue.isRight() ? x1 - theme.getStringWidth(name) : x, y, 0xFFFF);
	}

	@Override
	public boolean shouldRender(Mod mod) {
		BasicKeybind keybind = Huzuni.INSTANCE.modManager.getKeybind(mod);
		return keybind != null && keybind.isBound() && mod.settings.isDisplayable();
	}
    
	@Override
	public String getName() {
		return "Skidded";
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
		return right ? (mod.isEnabled() ? "on = " : "off = ") + (mod.isEnabled() ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED) + mod.getDisplayNameForRender() + TextFormatting.RESET + " = " + keybind.getKeyName() : 
			keybind.getKeyName() + " = " + (mod.isEnabled() ? TextFormatting.DARK_GREEN : TextFormatting.DARK_RED) + mod.getDisplayNameForRender() + TextFormatting.RESET + " = " + (mod.isEnabled() ? "on" : "off");
	}

	@Override
	public int getModWidth(Theme theme, Mod mod) {
		return theme.getStringWidth(formatName(mod));
	}

}
