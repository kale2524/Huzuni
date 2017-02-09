package net.halalaboos.huzuni.api.gui;

import java.util.HashMap;
import java.util.Map;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTreeComponent;
import net.halalaboos.huzuni.api.settings.Nameable;
import net.halalaboos.huzuni.api.settings.Node;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.Texture;
import net.minecraft.client.Minecraft;

/**
 * Provides render functions for {@link Component}s and {@link Container}s.
 * */
public abstract class Theme implements Nameable {
	
	protected final Huzuni huzuni = Huzuni.INSTANCE;
	
	private final Texture icons = new Texture("huzuni/icons.png");

	private final Map<Class<? extends Component>, ComponentRenderer<?>> renderers = new HashMap<Class<? extends Component>, ComponentRenderer<?>>();
	
	private final Map<Class<? extends NodeTreeComponent<?>>, NodeRenderer<?>> nodeTreeRenderers = new HashMap<Class<? extends NodeTreeComponent<?>>, NodeRenderer<?>>();
	
	private final String name, description;
			
	public Theme(String name, String description) {
		this.name = name;
		this.description = description;
	}
	
	/**
	 * Uses {@link ComponentRenderer}s to render the given {@link Component}.
	 * */
	public void renderComponent(Component component) {
		for (Class<? extends Component> componentClass : renderers.keySet()) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				ComponentRenderer renderer = renderers.get(componentClass);
				renderer.render(component);
			}
		}
	}
	
	/**
	 * Uses {@link NodeRenderer}s to render the given {@link Node}.
	 * */
	public void renderNode(NodeTreeComponent<?> nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
		for (Class<? extends NodeTreeComponent<?>> nodeTreeComponentClass : nodeTreeRenderers.keySet()) {
			if (nodeTreeComponentClass.isAssignableFrom(nodeTreeComponent.getClass())) {
				NodeRenderer renderer = nodeTreeRenderers.get(nodeTreeComponentClass);
				renderer.render(nodeTreeComponent, mouseOver, x, y, width, height);
			}
		}
	}
	
	/**
	 * Provided to draw rectangles used for the backgrounds of containers, mostly used for {@link net.halalaboos.huzuni.api.gui.widget.Widget}s.
	 * */
	public abstract void drawBackgroundRect(int x, int y, int width, int height, boolean highlight);

	/**
	 * Provided to draw rectangles with hover effects and highlight effects, used primarily for interactable {@link Component}s.
	 * */
	public abstract void drawRect(int x, int y, int width, int height, boolean mouseOver, boolean highlight);
	
	/**
	 * Provided to render the tool-tips of {@link Container}s.
	 * */
	public abstract void drawTooltip(String tooltip, int x, int y);
	
	/**
	 * Draws a line from the first two positions to the second two positions.
	 * */
	public abstract void drawLine(int x, int y, int x1, int y1, boolean highlight);
	
	/**
	 * Draws a rectangular border around the given coordinates.
	 * */
	public abstract void drawBorder(int x, int y, int width, int height, boolean highlight);

	/**
	 * Draws the expanded/closed icon within the given location and with the given size.
	 * */
	public void drawExpanded(int x, int y, int size,boolean expanded) {
		GLManager.glColor(1F, 1F, 1F, 1F);
		icons.render(x, y, size, size, expanded ? 64F / 256F : 0F, 0F, expanded ? 128F / 256F : 64F / 256F, 64F/ 256F);
	}
	
	/**
	 * Draws an arrow at the given x and y position with the given size, either facing right or left.
	 * */
	public void drawArrow(int x, int y, int size, boolean right, int color) {
		GLManager.glColor(color);
		icons.render(x, y, size, size, 0F, right ? 0 : 64F / 256F, 64F / 256F, right ? 64F / 256F : 128F / 256F);
	}

	/**
	 * Draws an arrow at the given x and y position with the given size, either facing right or left.
	 * */
	public void drawArrow(int x, int y, int size, boolean right) {
        drawArrow(x, y, size, right, 0xFFFFFFFF);
	}
	
	/**
	 * Draws a string at the given location.
	 * */
	public void drawString(String text, int x, int y, int color) {
		if (huzuni.settings.customFont.isEnabled())
			huzuni.guiFontRenderer.drawString(text, x, y, color);
		else
			Minecraft.getMinecraft().fontRenderer.drawString(text, x, y + 2, color);
	}

	/**
	 * Draws a string at the given location with a shadow.
	 * */
	public void drawStringWithShadow(String text, int x, int y, int color) {
		if (huzuni.settings.customFont.isEnabled())
			huzuni.guiFontRenderer.drawStringWithShadow(text, x, y, color);
		else
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y + 2, color);
	}
	
	/**
	 * Gives the width of the given string, provided by the font renderer.
	 * */
	public int getStringWidth(String text) {
		if (huzuni.settings.customFont.isEnabled())
			return huzuni.guiFontRenderer.getStringWidth(text);
		else
			return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
		
	}
	
	/**
	 * Gives the height of the given string, provided by the font renderer.
	 * */
	public int getStringHeight(String text) {
		return 10;
	}
	
	/**
	 * Registers a {@link ComponentRenderer} to the given {@link Component} class.
	 * */
	public void addRenderer(Class<? extends Component> componentClass, ComponentRenderer<? extends Component> renderer) {
		renderers.put(componentClass, renderer);
	}

	/**
	 * Registers a {@link NodeRenderer} to the given {@link Node} class.
	 * */
	public void addNodeRenderer(Class<? extends NodeTreeComponent<?>> nodeTreeComponentClass, NodeRenderer<?> renderer) {
		nodeTreeRenderers.put(nodeTreeComponentClass, renderer);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	/**
	 * Renders a given {@link Component}. These are used within a theme.
	 * */
	public interface ComponentRenderer <T extends Component> {
		
		/**
		 * The function used to render a given {@link Component}.
		 * */
		void render(T component);
	}
	
	/**
	 * Renders a given {@link Node}. These are used within a theme.
	 * */
	public interface NodeRenderer <T extends NodeTreeComponent<?>> {
		
		/**
		 * The function used to render a given {@link Node}.
		 * */
		void render(T nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height);
	}
}
