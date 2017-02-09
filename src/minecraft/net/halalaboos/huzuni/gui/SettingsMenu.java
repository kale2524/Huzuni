package net.halalaboos.huzuni.gui;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Container;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.components.Tab;
import net.halalaboos.huzuni.api.gui.components.TabComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ColorNodeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ItemListComponent;
import net.halalaboos.huzuni.api.gui.components.tree.KeybindComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ModComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ModSettingsComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ModeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTree;
import net.halalaboos.huzuni.api.gui.components.tree.TaskManagerComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ToggleableComponent;
import net.halalaboos.huzuni.api.gui.components.tree.TreeComponentFactory;
import net.halalaboos.huzuni.api.gui.components.tree.ValueComponent;
import net.halalaboos.huzuni.api.gui.components.tree.WidgetComponent;
import net.halalaboos.huzuni.api.gui.containers.NodeTreeSearchContainer;
import net.halalaboos.huzuni.gui.containers.SettingsContainer;
import net.halalaboos.huzuni.api.gui.containers.SingleComponentContainer;
import net.halalaboos.huzuni.api.gui.widget.Widget;
import net.halalaboos.huzuni.api.mod.BasicKeybind;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.mod.ModSettings;
import net.halalaboos.huzuni.api.settings.ItemList;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.ColorNode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.TaskManager;
import net.halalaboos.huzuni.api.util.IncrementalPosition;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.minecraft.client.renderer.GlStateManager;

/**
 * Main menu used within the mod. Allows the user to access all mods, widgets, and settings.
 * */
public class SettingsMenu {

	private static final int WIDTH = 150, TAB_WIDTH = 12, TAB_HEIGHT = 50;
	
	private final SettingsContainer container = new SettingsContainer(0, 0, 0, 0);
	
	private final TabComponent tabComponent = new TabComponent(0, 0, 0, 0);
	
	private final IncrementalPosition expandedPosition = new IncrementalPosition();
	
	private boolean expanded = true;
	
	private int height = 0;
	
	private Tab mods, widgets, settings;

	/**
	 * Initializes the default values, adds default tabs, registers tree components to their associated node.
	 * */
	public void init() {
		TreeComponentFactory.addComponent(Mod.class, ModComponent.class);
		TreeComponentFactory.addComponent(Toggleable.class, ToggleableComponent.class);
		TreeComponentFactory.addComponent(Widget.class, WidgetComponent.class);
		TreeComponentFactory.addComponent(ModSettings.class, ModSettingsComponent.class);
		TreeComponentFactory.addComponent(Value.class, ValueComponent.class);
		TreeComponentFactory.addComponent(ColorNode.class, ColorNodeComponent.class);
		TreeComponentFactory.addComponent(BasicKeybind.class, KeybindComponent.class);
		TreeComponentFactory.addComponent(Mode.class, ModeComponent.class);
		TreeComponentFactory.addComponent(TaskManager.class, TaskManagerComponent.class);
		TreeComponentFactory.addComponent(ItemList.class, ItemListComponent.class);
		resetExpansion();
		expandedPosition.setIncrement(8F, 8F, 8F);
		tabComponent.setScrollbarSize(2);
		container.add(tabComponent);
		add(mods = new Tab("Mods", "Adjust mod settings.", null));
		add(widgets = new Tab("Widgets ", "Adjust widget settings.", null));
		add(settings = new Tab("Settings", "Adjust settings.", null));
	}

	/**
     * Loads the information required for each tab.
     * */
	public void load() {
		mods.setContainer(generateMods());
		widgets.setContainer(generateWidgets());
		settings.setContainer(generateSettings());
	}

	/**
     * Renders the menu.
     * */
	public void renderMenu(int screenWidth, int screenHeight, int tabHeight, int padding) {
		int tabY = GLManager.getScreenHeight() / 2;

		height = screenHeight / 2;
		int x = 0, y = height - height / 2;
		expandedPosition.setFinalPosition(expanded ? x : x - WIDTH, 0, 0);
		expandedPosition.updatePosition();
		if (expanded || !expandedPosition.hasFinished()) {
			x = (int) expandedPosition.getX();
			
			container.setX(x);
			container.setY(y);
			container.setWidth(WIDTH);
			container.setHeight(height);
	
			tabComponent.setOffsetX(padding);
			tabComponent.setOffsetY(padding);
			tabComponent.setWidth(WIDTH - padding * 2);
			tabComponent.setHeight(tabHeight - padding * 2);
			container.render();
			renderSettingsTab(x + WIDTH, tabY - TAB_HEIGHT / 2, TAB_WIDTH, TAB_HEIGHT, "Minimize me!");
			if (tabComponent.hasSelectedTab()) {
				Container selectedContainer = tabComponent.getSelectedTab().getContainer();
				selectedContainer.setTheme(container.getTheme());
				selectedContainer.setX(container.getX() + padding);
				selectedContainer.setY(container.getY() + tabComponent.getOffsetY() + tabComponent.getHeight() + padding);
				selectedContainer.setWidth(WIDTH - padding * 2);
				selectedContainer.setHeight(height - tabComponent.getHeight() - padding * 3);
				selectedContainer.render();
			}
		} else {
			renderSettingsTab(x, tabY - TAB_HEIGHT / 2, TAB_WIDTH, TAB_HEIGHT, "Expand me!");
		}
	}

	/**
     * Renders the settings tab (the point which allows the user to expand or close the settings menu)
     * */
	private void renderSettingsTab(int x, int y, int width, int height, String tooltip) {
		int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
		container.getTheme().drawBackgroundRect(x, y, width, height, false);
		boolean mouseOver = mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + height / 2 - container.getTheme().getStringHeight("Settings") / 2 + 2 - container.getTheme().getStringHeight("Settings"), y + width / 2, 0F);
		GlStateManager.rotate(90F, 0F, 0F, 1F);
		container.getTheme().drawStringWithShadow("Settings", 0, 0, mouseOver ? 0xFFFFAA : 0xFFFFFF);
		GlStateManager.popMatrix();
		
		if (mouseOver)
			container.getTheme().drawTooltip(tooltip, mouseX, mouseY);

	}

	/**
     * Invoked when the keyboard is typed.
     * */
	public void keyTyped(int keyCode, char typedChar) {
		if (expanded && expandedPosition.hasFinished()) {
			if (tabComponent.hasSelectedTab()) {
				Container selectedContainer = tabComponent.getSelectedTab().getContainer();
				selectedContainer.keyTyped(typedChar, keyCode);
			}
			container.keyTyped(typedChar, keyCode);
		}
	}

	/**
     * Invoked when the mouse is clicked.
     * */
	public void mouseClicked(int x, int y, int buttonId) {
		if (expanded && expandedPosition.hasFinished()) {
			float tabY = GLManager.getScreenHeight() / 2;
			if (x > WIDTH && x < WIDTH + TAB_WIDTH && y > tabY - TAB_HEIGHT && y < tabY + TAB_HEIGHT) {
				this.expanded = !expanded;
				return;
			}
			if (tabComponent.hasSelectedTab()) {
				Container selectedContainer = tabComponent.getSelectedTab().getContainer();
				selectedContainer.mouseClicked(x, y, buttonId);
			}
			container.mouseClicked(x, y, buttonId);
		} else {
			int tabY = GLManager.getScreenHeight() / 2;
			if (x > 0 && x < TAB_WIDTH && y > tabY - TAB_HEIGHT && y < tabY + TAB_HEIGHT)
				this.expanded = !expanded;
		}
	}

	/**
     * Invoked when the mouse wheel has been scrolled.
     * */
	public void mouseWheel(int amount) {
		if (expanded && expandedPosition.hasFinished()) {
			if (tabComponent.hasSelectedTab()) {
				Container selectedContainer = tabComponent.getSelectedTab().getContainer();
				selectedContainer.mouseWheel(amount);
			}
			container.mouseWheel(amount);
		}
	}

	/**
     * Updates the containers.
     * */
	public void update() {
		if (expanded && expandedPosition.hasFinished()) {
			if (tabComponent.hasSelectedTab()) {
				Container selectedContainer = tabComponent.getSelectedTab().getContainer();
				selectedContainer.update();
			}
			container.update();
		}
	}

	/**
     * Resets the menu expansion.
     * */
	public void resetExpansion() {
		expandedPosition.setPosition(-WIDTH, 0, 0);
	}

	/**
     * Generates the mods container for the mods tab.
     * */
	private Container generateMods() {
		NodeTreeSearchContainer container = new NodeTreeSearchContainer(0, 0, 0, 0);
		for (int i = 0; i < Huzuni.INSTANCE.modManager.getMods().size(); i++)
			container.addComponent(new ModComponent(Huzuni.INSTANCE.modManager.getMods().get(i)));
		return container;
	}

	/**
     * Generates the widgets container for the widgets tab.
     * */
	private Container generateWidgets() {
		NodeTree tree = new NodeTree(0, 0, 0, 0);
		for (Widget widget : Huzuni.INSTANCE.guiManager.widgetManager.getWidgets())
			tree.add(new WidgetComponent(widget));
		return new SingleComponentContainer<NodeTree>(0, 0, 0, 0, tree, 0);
	}

	/**
     * Generates the settings container for the settings tab.
     * */
	private Container generateSettings() {
		NodeTree tree = new NodeTree(0, 0, 0, 0);
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.settings.keyOpenMenu));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.settings.team));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.settings.lineSettings));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.settings.menuSettings));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.lookManager));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.hotbarManager));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.clickManager));
		tree.add(TreeComponentFactory.getComponent(Huzuni.INSTANCE.waypointManager.getWaypoints()));
		return new SingleComponentContainer<NodeTree>(0, 0, 0, 0, tree, 0);
	}
	
	public void add(Tab tab) {
		tabComponent.add(tab);
	}
	
	public void setTheme(Theme theme) {
		container.setTheme(theme);
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

}
