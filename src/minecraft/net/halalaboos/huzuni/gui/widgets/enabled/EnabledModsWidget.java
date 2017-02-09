package net.halalaboos.huzuni.gui.widgets.enabled;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.organize.AlphabeticalOrganizer;
import net.halalaboos.huzuni.api.settings.organize.CategoryOrganizer;
import net.halalaboos.huzuni.api.settings.organize.DownwardOrganizer;
import net.halalaboos.huzuni.api.settings.organize.Organizer;
import net.halalaboos.huzuni.api.settings.organize.RandomOrganizer;
import net.halalaboos.huzuni.api.settings.organize.UpwardOrganizer;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.gui.widgets.BackgroundWidget;

/**
 * Widget which renders the enabled mods in-game.
 * */
public class EnabledModsWidget extends BackgroundWidget {
	
	private final Timer timer = new Timer();
	
	private final List<Mod> renderList = new ArrayList<Mod>();

	private final Mode<ModRenderStyle> style = new Mode<ModRenderStyle>("Style", "Style used to render enabled mods.", new DefaultStyle(), new IridiumStyle(), new MorbidStyle(), new NyanStyle(), new OldSchoolStyle(), new PigPlusStyle(), new SkiddedStyle()) {
		@Override
		public String getName(ModRenderStyle item) {
			return item.getName();
		}
	};
	
	private final Mode<String> modColor = new Mode<String>("Mod color", "Coloring used for each mod.", "Mod", "Category", "Growing rainbow", "Static rainbow");
	
	private final Mode<Organizer> listOrganization = new Mode<Organizer>("List organization", "Organization method used for mods.", new RandomOrganizer(), new UpwardOrganizer(), new DownwardOrganizer(), new CategoryOrganizer(), new AlphabeticalOrganizer()) {
		@Override
		public void setSelectedItem(int selectedItem) {
			super.setSelectedItem(selectedItem);
			organizeMods();
		}
	};
	
	private float grow = 0F;
	
	public EnabledModsWidget(WidgetManager menuManager) {
		super("Enabled mods", "Renders mods in-game", menuManager);
		this.addChildren(style, listOrganization, modColor);
		this.setWidth(100);
		this.setHeight(50);
	}

	@Override
	public void load() {
		super.load();
		organizeMods();
	}
	
	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
		calculateSize();
		int incrementOffset = glue.isBottom() ? -1 : 1;
		if (glue.isBottom())
			y = y + height - theme.getStringHeight("minimum") - 2;
        x = glue.isRight() ? x - 2 : x + 2;
		int index = 0, maxIndex = getMaxIndex();
		for (int i = 0; i < renderList.size(); i++) {
			Mod mod = renderList.get(i);
			if (style.getSelectedItem().shouldRender(mod)) {
				index++;
				String name = style.getSelectedItem().formatName(mod);
				if (modColor.getSelected() == 0)
					style.getSelectedItem().render(theme, glue, mod, name, mod.settings.getDisplayColor().getRGB(), x, y, x + width, y + 12);
				else if (modColor.getSelected() == 1)
					style.getSelectedItem().render(theme, glue, mod, name, getColorFromCategory(mod.getCategory()), x, y, x + width, y + 12);
				else if (modColor.getSelected() == 2)
					style.getSelectedItem().render(theme, glue, mod, name, getRainbowColor(grow, index, maxIndex), x, y, x + width, y + 12);
				else if (modColor.getSelected() == 3)
					style.getSelectedItem().render(theme, glue, mod, name, getRainbowColor(0F, index, maxIndex), x, y, x + width, y + 12);
				y += incrementOffset * 12;
			}
		}
		if (modColor.getSelected() == 2 && timer.hasReach(50)) {
			grow += 0.01F;
			timer.reset();
		}
	}

	/**
	 * @return The amount of mods that should be rendered based on the style.
	 * */
	private int getMaxIndex() {
		int index = 0;
		for (int i = 0; i < renderList.size(); i++) {
			Mod mod = renderList.get(i);
			if (style.getSelectedItem().shouldRender(mod))
				index++;
		}
		return index;
	}

	/**
     * Calculates the size of this widget.
     * */
	private void calculateSize() {
		int width = 0, height = 0;
		for (int i = 0; i < renderList.size(); i++) {
			Mod mod = renderList.get(i);
			if (style.getSelectedItem().shouldRender(mod)) {
				int itemWidth = style.getSelectedItem().getModWidth(theme, mod) + 4;
				if (itemWidth > width)
					width = itemWidth;
				height += 12;
			}
		}
		if (width == 0)
			width = 100;
		if (height == 0)
			height = 12;
		this.setWidth(width);
		this.setHeight(height);
	}

	/**
	 * @return A HSB color using a base value plus the ratio of an index and a max index.
	 * */
	private int getRainbowColor(float base, int index, int maxIndex) {
		return GLManager.getHSBColor(base + ((float) index / (float) maxIndex), 1F, 1F).getRGB();
	}

	/**
     * @return A color to associate the given category with.
     * */
	private int getColorFromCategory(Category category) {
		switch (category) {
			case COMBAT:
				return 0xAD3939;
			case MOVEMENT:
				return 0x517E9C;
			case VISUAL:
				return 0x5E67AB;
			case MISC:
				return 0x519C55;
			case MINING:
				return 0x96844D;
			//case CHAT:
			//	return 0x934C8D;
			default:
				return 0x878787;
		}
	}

	/**
     * Organizes the mods based on the list organization selected.
     * */
	public void organizeMods() {
		renderList.clear();
		renderList.addAll(huzuni.modManager.getMods());
		listOrganization.getSelectedItem().organize(renderList);
	}
	
	public void load(JsonObject object) throws IOException {
		super.load(object);
		this.organizeMods();
	}
	
}
