package net.halalaboos.huzuni.gui.widgets.tabbed;

import java.text.DecimalFormat;
import java.util.List;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.settings.*;
import org.lwjgl.input.Keyboard;

import com.google.gson.JsonObject;

import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.gui.widget.Widget;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.util.IncrementalPosition;

/**
 * Widget which allows the user to enable/disable mods easily.
 * */
public class TabbedMenuWidget extends Widget {
	
	private final IncrementalPosition selectionPosition = new IncrementalPosition();
	
	public final Value minimumWidth = new Value("Minimum width", "", 0F, 100F, 200F, 1F, "Minimum width allowed for the menu.") {
		@Override
		public void setValue(float value) {
			super.setValue(value);
			updateTabSize();
		}
	};
	
	public final ItemList<Tab> tabList = new ItemList<Tab>("Order", "Modify the order of each tab.") {

		@Override
		protected void saveItem(JsonObject object, Tab item) {
			object.addProperty("name", item.getName());
			object.addProperty("description", item.getDescription());
		}

		@Override
		protected Tab loadItem(JsonObject object) {
			return new Tab(object.get("name").getAsString(), object.get("description").getAsString());
		}
	};
	
	public final Toggleable animations = new Toggleable("Animations", "Animates the menu movements.");
	
	private int tabIndex = 0;

	public TabbedMenuWidget(WidgetManager menuManager) {
		super("Tabbed menu", "Renders a menu that navigated using the arrows keys and return key", menuManager);
		this.addChildren(minimumWidth, tabList, animations);
		tabList.setOrdered(true);
		animations.setEnabled(true);
	}
	
	@Override
	public void init() {
		super.init();
	}
	
	@Override
	public void load() {
		super.load();
		if (tabList.isEmpty())
			generateTabs();
		populateTabs(huzuni.modManager.getMods());
		updateTabSize();
	}
	
	@Override
	public void renderMenu(int x, int y, int width, int height) {
		theme.drawBorder(x, y, width, height, false);

		for (int i = 0; i < tabList.size(); i++) {
			Tab tab = tabList.get(i);
			if (i == tabIndex) {
				selectionPosition.setFinalPosition(0, y, 0);
				selectionPosition.updatePosition();
			}
			tab.render(this, theme, i == tabIndex && (selectionPosition.hasFinished() || !animations.isEnabled()), x, y, width, tab.getHeight());
			y += tab.getHeight();
		}
	}

	@Override
	public void keyTyped(int keyCode) {
		Huzuni.INSTANCE.LOGGER.info("Keycode Tab Hit: " + keyCode);
		
        Tab tab = getSelectedTab();
        if (tab.isExpanded()) {
            switch (keyCode) {
                case Keyboard.KEY_UP:
                    tab.modifyTab(Tab.TabMovement.UP);
                    break;
                case Keyboard.KEY_DOWN:
                    tab.modifyTab(Tab.TabMovement.DOWN);
                    break;
                case Keyboard.KEY_LEFT:
                    tab.modifyTab(glue.isRight() ? Tab.TabMovement.RIGHT : Tab.TabMovement.LEFT);
                    break;
                case Keyboard.KEY_RIGHT:
                    tab.modifyTab(glue.isRight() ? Tab.TabMovement.LEFT : Tab.TabMovement.RIGHT);
                    break;
                case Keyboard.KEY_RETURN:
                    tab.modifyTab(Tab.TabMovement.RETURN);
                    break;
                default:
                    break;
            }
        } else {
            switch (keyCode) {
                case Keyboard.KEY_UP:
                    if (selectionPosition.hasFinished() || !animations.isEnabled()) {
                        tabIndex--;
                        if (tabIndex < 0)
                            tabIndex = tabList.size() - 1;
                    }
                    break;
                case Keyboard.KEY_DOWN:
                    if (selectionPosition.hasFinished() || !animations.isEnabled()) {
                        tabIndex++;
                        if (tabIndex >= tabList.size())
                            tabIndex = 0;
                    }
                    break;
                case Keyboard.KEY_RIGHT:
                    if (!glue.isRight())
                        getSelectedTab().modifyTab(Tab.TabMovement.RIGHT);
                    break;
                case Keyboard.KEY_LEFT:
                    if(glue.isRight())
                        getSelectedTab().modifyTab(Tab.TabMovement.RIGHT);
                default:
                    break;
            }
        }
        updateTabSize();
    }

	/**
     * Generates the tab objects based on the categories present within the client.
     * */
	private void generateTabs() {
		for (Category category : Category.values()) {
			if (category == Category.NONE)
				continue;
			Tab tab = new Tab(category.formalName, "Mods for " + category.formalName + ".");
			tabList.add(tab);
		}
	}

	/**
     * Populates the tabs with the mods available.
     * */
	private void populateTabs(List<Mod> mods) {
		for (Tab tab : tabList.getItems()) {
			for (Mod mod : mods)
				if (tab.getName().equals(mod.getCategory().formalName))
                    tab.addTab(new ModTab(mod));
		}
	}

	/**
     * Updates the tab width/height starting with the minimum width.
     * */
	private void updateTabSize() {
		width = (int) minimumWidth.getValue();
		height = 0;
		for (Tab tab : tabList.getItems()) {
			tab.updateSize(theme, (int) minimumWidth.getValue());
			if (width <= tab.getWidth() + 6) {
				width = tab.getWidth() + 6;
			}
			height += tab.getHeight();
		}
	}

	/**
     * @return The selected tab.
     * */
	private Tab getSelectedTab() {
		return tabList.getItems().get(tabIndex);
	}

	/**
     * Allows the player to edit mods.
     * */
	public class ModTab extends Tab {

        private final Mod mod;

        public ModTab(Mod mod) {
            super(mod.getName(), mod.getDescription());
            this.mod = mod;
            for (Node node : mod.getChildren()) {
                if (node instanceof Value)
                    addTab(new ValueTab((Value) node));
                if (node instanceof Toggleable)
                    addTab(new ToggleableTab((Toggleable) node));
                if (node instanceof Mode)
                    addTab(new ModeTab((Mode) node));
            }
        }

        @Override
        public void keyReturn() {
            mod.toggle();
        }

        @Override
        public boolean isHighlighted() {
            return mod.isEnabled();
        }

    }

    /**
     * Allows the player to edit a toggleable node.
     * */
    public class ToggleableTab extends Tab {

        private final Toggleable toggleable;

        public ToggleableTab(Toggleable toggleable) {
            super(toggleable.getName(), toggleable.getDescription());
            this.toggleable = toggleable;
        }

        @Override
        public void keyReturn() {
            toggleable.toggle();
        }

        @Override
        public boolean isHighlighted() {
            return toggleable.isEnabled();
        }
    }

    /**
     * Allows the player to edit a mode node.
     * */
    public class ModeTab <T> extends Tab {

        private final Mode<T> mode;

        public ModeTab(Mode<T> mode) {
            super(mode.getName(), mode.getDescription());
            this.mode = mode;
        }

        @Override
        public void modifyTab(TabMovement tabMovement) {
            if (isExpanded()) {
                switch (tabMovement) {
                    case LEFT:
                        mode.setSelectedItem(mode.getSelected() - 1);
                        break;
                    case RIGHT:
                        mode.setSelectedItem(mode.getSelected() + 1);
                        break;
                    case RETURN:
                        this.setExpanded(false);
                }
            }
        }

        @Override
        public void keyReturn() {
            this.setExpanded(!isExpanded());
        }

        @Override
        public boolean isHighlighted() {
            return isExpanded();
        }

        @Override
        public String getName() {
            return mode.getName() + String.format(" (%s)", mode.getSelectedItem());
        }
    }

    /**
     * Allows the player to edit a Value node.
     * */
    public class ValueTab extends Tab {

        private final Value value;

        private final DecimalFormat formatter = new DecimalFormat("#.#");

        public ValueTab(Value value) {
            super(value.getName(), value.getDescription());
            this.value = value;
        }

        @Override
        protected void renderTab(TabbedMenuWidget tabbedMenuWidget, Theme theme, boolean highlight, int x, int y, int width, int height) {
            theme.drawBackgroundRect(x, y, width, height - 4, highlight);
            String name = getName();
            if (highlight)
                theme.drawStringWithShadow(name, x + (isExpanded() ? 4 : 2), y, isExpanded() ? 0xFFFFFF : 0xBBBBBB);
            else
                theme.drawString(name, x + (isExpanded() ? 4 : 2), y, isExpanded() ? 0xFFFFFF : 0xBBBBBB);
            theme.drawBackgroundRect(x, y + height - 4, width, 4, false);
            theme.drawBackgroundRect(x, y + height - 4, (int) (width * ((value.getValue() - value.getMinValue()) / (value.getMaxValue() - value.getMinValue()))), 4, true);
        }

        @Override
        public void modifyTab(TabMovement tabMovement) {
            if (isExpanded()) {
                float increment = value.getIncrementValue() != -1 ? value.getIncrementValue() * (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 5F : 1F) : (value.getMaxValue() - value.getMinValue()) * (1F / (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 5F : 10F));
                switch (tabMovement) {
                    case LEFT:
                        value.setValue(Float.valueOf(formatter.format(value.getValue() - increment)));
                        break;
                    case RIGHT:
                        value.setValue(Float.valueOf(formatter.format(value.getValue() + increment)));
                        break;
                    case RETURN:
                        this.setExpanded(false);
                }
            }
        }

        @Override
        public String getName() {
            return String.format("%s %s", value.getName(), value.getValue() + value.getCarot());
        }

        @Override
        public int getHeight() {
            return 16;
        }

        @Override
        public void keyReturn() {
           this.setExpanded(!isExpanded());
        }
    }
	
}
