package net.halalaboos.huzuni.gui.widgets.enabled;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.mod.BasicKeybind;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;

/**
 * The pig plus style.
 * */
public class PigPlusStyle implements ModRenderStyle {

    private static final int ICON_SIZE = 8;
    
	@Override
	public void render(Theme theme, Glue glue, Mod mod, String name, int color, int x, int y, int x1, int y1) {
		name = formatName(mod, glue.isRight());
    	renderIcon(glue.isRight() ? x1 - 12 : x + 2, y + 2, mod);
        theme.drawStringWithShadow(name, glue.isRight() ? x1 - theme.getStringWidth(name) - 15 : x + 14, y, mod.isEnabled() ? 0xFFFFFF : 0x999999);
	}

	@Override
	public boolean shouldRender(Mod mod) {
		BasicKeybind keybind = Huzuni.INSTANCE.modManager.getKeybind(mod);
		return keybind != null && keybind.isBound() && mod.settings.isDisplayable();
	}
    
    /**
     * Renders pig plus enabled / disabled icon.
     */
    private void renderIcon(int xPos, int yPos, Mod mod) {
    	GLManager.glColor(0xFF000000);
    	RenderUtils.drawRect(xPos + 1, yPos + 1, xPos + ICON_SIZE + 1, yPos + ICON_SIZE + 1);
    	GLManager.glColor(0xFF4D4D4D);
    	RenderUtils.drawBorder(1F, xPos, yPos, xPos + ICON_SIZE, yPos + ICON_SIZE);
    	GLManager.glColor(mod.isEnabled() ? 0xFF83F52C : 0xFFE3170D);
    	RenderUtils.drawRect(xPos, yPos, xPos + ICON_SIZE, yPos + ICON_SIZE);
    }

	@Override
	public String getName() {
		return "Pig Plus";
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
		return right ? mod.getDisplayNameForRender() + " " + keybind.getKeyName() : keybind.getKeyName() + "  " + mod.getDisplayNameForRender();
	}
	
	@Override
	public int getModWidth(Theme theme, Mod mod) {
		return 14 + theme.getStringWidth(formatName(mod));
	}

}
