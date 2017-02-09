package net.halalaboos.huzuni.gui;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Color;

import net.halalaboos.huzuni.Huzuni;
import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.components.Button;
import net.halalaboos.huzuni.api.gui.components.Tab;
import net.halalaboos.huzuni.api.gui.components.TabComponent;
import net.halalaboos.huzuni.api.gui.components.TextField;
import net.halalaboos.huzuni.api.gui.components.tree.ColorNodeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ItemListComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ItemSelectorComponent;
import net.halalaboos.huzuni.api.gui.components.tree.KeybindComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ModComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ModSettingsComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ModeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.NodeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTree;
import net.halalaboos.huzuni.api.gui.components.tree.NodeTreeComponent;
import net.halalaboos.huzuni.api.gui.components.tree.TaskManagerComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ToggleableComponent;
import net.halalaboos.huzuni.api.gui.components.tree.ValueComponent;
import net.halalaboos.huzuni.api.gui.components.tree.WidgetComponent;
import net.halalaboos.huzuni.gui.containers.SettingsContainer;
import net.halalaboos.huzuni.api.settings.Nameable;
import net.halalaboos.huzuni.api.settings.ItemSelector.ItemData;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.halalaboos.huzuni.gui.tree.components.TeamComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * The basic theme which is applied to all menus within the client. <br/>
 * The only menu found within the client is the settings menu.
 * */
public class BasicTheme extends Theme {

	private final Color background = new Color(0F, 0F, 0F, 0.75F), defaultColor = new Color(0.3F, 0.3F, 0.3F, 0.75F);
	
	public BasicTheme() {
		super("Basic", "The basic theme for the development gui.");
		this.addRenderer(SettingsContainer.class, new SettingsContainerRenderer());
		this.addRenderer(TabComponent.class, new TabComponentRenderer());
		this.addRenderer(NodeTree.class, new NodeTreeRenderer());
		this.addRenderer(Button.class, new ButtonRenderer());
		this.addRenderer(TextField.class, new TextFieldRenderer());
		this.addNodeRenderer(ColorNodeComponent.class, new ColorNodeRenderer());
		this.addNodeRenderer(ItemListComponent.class, new ItemListRenderer());
		this.addNodeRenderer(TaskManagerComponent.class, new TaskManagerRenderer());
		this.addNodeRenderer(KeybindComponent.class, new KeybindRenderer());
		this.addNodeRenderer(ModeComponent.class, new ModeRenderer());
		this.addNodeRenderer(ModComponent.class, new ModRenderer());
		this.addNodeRenderer(ModSettingsComponent.class, new ModSettingsRenderer());
		this.addNodeRenderer(WidgetComponent.class, new WidgetComponentRenderer());
		this.addNodeRenderer(ValueComponent.class, new ValueRenderer());
		this.addNodeRenderer(ToggleableComponent.class, new ToggleableRenderer());
		this.addNodeRenderer(TeamComponent.class, new TeamRenderer());
		this.addNodeRenderer(ItemSelectorComponent.class, new ItemSelectorRenderer());
		this.addNodeRenderer(NodeComponent.class, new NodeComponentRenderer());
	}

	@Override
	public void drawRect(int x, int y, int width, int height, boolean mouseOver, boolean highlight) {
		GLManager.glColor(RenderUtils.getColorWithAffects(highlight ? huzuni.settings.menuColor.getColor() : defaultColor, mouseOver, false));
		renderRectangle(x, y, width, height);
	}

	@Override
	public void drawBackgroundRect(int x, int y, int width, int height, boolean highlight) {
		GLManager.glColor(highlight ? huzuni.settings.menuColor.getColor() : background);
		renderRectangle(x, y, width, height);
	}

	@Override
	public void drawLine(int x, int y, int x1, int y1, boolean highlight) {
		GLManager.glColor(highlight ? huzuni.settings.menuColor.getColor() : background);
		RenderUtils.drawLine(2F, x, y, x1, y1);
	}
	
	@Override
	public void drawBorder(int x, int y, int width, int height, boolean highlight) {
		GLManager.glColor(highlight ? huzuni.settings.menuColor.getColor() : background);
		RenderUtils.drawBorder(2F, x, y, x + width, y + height);
	}
	
	
	@Override
	public void drawTooltip(String tooltip, int x, int y) {
		this.drawRect(x, y - getStringHeight(tooltip), getStringWidth(tooltip) + 4, getStringHeight(tooltip), false, false);
		this.drawStringWithShadow(tooltip, x + 2, y - getStringHeight(tooltip) - 1, 0xFFFFFF);
	}
	
	private void renderRectangle(int[] rect) {
		renderRectangle(rect[0], rect[1], rect[2], rect[3]);
	}
	
	private void renderRectangle(float x, float y, float width, float height) {
		RenderUtils.drawRect(x, y, x + width, y + height);
	}
	
	private void renderItemStack(ItemStack itemStack, int x, int y, float delta) {
		if (itemStack == null)
			return;
		GlStateManager.pushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        try {
    		GlStateManager.translate(0.0F, 0.0F, 32.0F);
        	Minecraft.getMinecraft().getRenderItem().zLevel = 200F;
        	Minecraft.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
        	Minecraft.getMinecraft().getRenderItem().renderItemOverlayIntoGUI(Minecraft.getMinecraft().fontRenderer, itemStack, x, y, "");
        	Minecraft.getMinecraft().getRenderItem().zLevel = 0F;
        } catch (Exception e) {
        	e.printStackTrace();
        }
        RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	public class SettingsContainerRenderer implements ComponentRenderer <SettingsContainer> {

		@Override
		public void render(SettingsContainer component) {
			GLManager.glColor(background);
			RenderUtils.drawRect(component.getX(), component.getY(), component.getX() + component.getWidth(), component.getY() + component.getHeight());
		}
		
	}
	
	public class ButtonRenderer implements ComponentRenderer <Button> {

		@Override
		public void render(Button button) {
			GLManager.glColor(RenderUtils.getColorWithAffects(button.isHighlight() ? huzuni.settings.menuColor.getColor() : defaultColor, button.isPointInside(GLManager.getMouseX(), GLManager.getMouseY()), false));
			RenderUtils.drawRect(button.getX(), button.getY(), button.getX() + button.getWidth(), button.getY() + button.getHeight());
			drawString(button.getTitle(), button.getX() + button.getWidth() / 2 - getStringWidth(button.getTitle()) / 2, button.getY() + button.getHeight() / 2 - 2 - getStringHeight(button.getTitle()) / 2, 0xFFFFFF);
		}
		
	}
	
	public class TextFieldRenderer implements ComponentRenderer <TextField> {

		@Override
		public void render(TextField textField) {
			drawRect(textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight(), false, false);
			if (!textField.hasText())
				drawString(TextFormatting.ITALIC + textField.getBackText(), textField.getX() + 2, textField.getY() + TextField.TEXT_HEIGHT / 2 - 1 - getStringHeight(textField.getBackText()) / 2, 0xAAAAAA);
			drawString(textField.getRenderText(textField.isSelected()), textField.getX() + 2, textField.getY() + TextField.TEXT_HEIGHT / 2 - 1 - getStringHeight(textField.getRenderText(textField.isSelected())) / 2, 0xFFFFFF);
		}
		
	}
	
	public class TabComponentRenderer implements ComponentRenderer <TabComponent> {

		@Override
		public void render(TabComponent component) {
			if (component.hasScrollbar()) {
				GLManager.glColor(defaultColor);
				renderRectangle(component.getScrollbarArea());
				GLManager.glColor(huzuni.settings.menuColor.getColor());
				renderRectangle(component.getScrollbar());
			}
			
			GLManager.glScissor(component.getRenderArea());
			glEnable(GL_SCISSOR_TEST);
			int xIncrement = -component.getScrollOffset();
			for (int i = 0; i < component.getTabs().size(); i++) {
				Tab tab = component.getTabs().get(i);
				int tabWidth = component.getTabWidth(tab);
				int[] tabArea = component.getTabArea(xIncrement, tabWidth);
				GLManager.glColor(RenderUtils.getColorWithAffects(component.getSelected() == i ? huzuni.settings.menuColor.getColor() : defaultColor, component.isPointInsideTab(GLManager.getMouseX(), GLManager.getMouseY(), xIncrement, tabWidth), false));
				renderRectangle(tabArea);
				drawString(tab.getName(), component.getX() + tabWidth / 2 + xIncrement - getStringWidth(tab.getName()) / 2, component.getY() + tabArea[3] / 2 - getStringHeight(tab.getName()) / 2, 0xFFFFFF);
				xIncrement += tabWidth + component.getPadding();
			}
			glDisable(GL_SCISSOR_TEST);
		}
		
	}
	
	public class NodeTreeRenderer implements ComponentRenderer <NodeTree> {

		@Override
		public void render(NodeTree nodeTree) {
			if (nodeTree.hasScrollbar()) {
				GLManager.glColor(defaultColor);
				renderRectangle(nodeTree.getScrollbarArea());
				GLManager.glColor(huzuni.settings.menuColor.getColor());
				renderRectangle(nodeTree.getScrollbar());
			}
			GLManager.glScissor(nodeTree.getRenderArea());
			glEnable(GL_SCISSOR_TEST);
			nodeTree.renderTree();
			glDisable(GL_SCISSOR_TEST);
		}
		
	}
	
	public class ColorNodeRenderer implements NodeRenderer<ColorNodeComponent> {

		@Override
		public void render(ColorNodeComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			
			drawString(nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
			drawRect(x, y + ColorNodeComponent.LABEL_SIZE, width, ColorNodeComponent.PICKER_HEIGHT, false, false);
			
			nodeTreeComponent.getSaturationSlider().renderSlider(BasicTheme.this, mouseOver && nodeTreeComponent.getSaturationSlider().isPointInside(mouseX, mouseY));
			nodeTreeComponent.getLuminanceSlider().renderSlider(BasicTheme.this, mouseOver && nodeTreeComponent.getLuminanceSlider().isPointInside(mouseX, mouseY));
			
			for (int i = 0; i < nodeTreeComponent.getOptions().size(); i++) {
				int[] area = nodeTreeComponent.getOptionArea(x, y, i);
				GLManager.glColor(nodeTreeComponent.getOptions().get(i));
				RenderUtils.drawRect(area[0], area[1], area[0] + area[2], area[1] + area[3]);
			}
		}
		
	}
	
	public class ItemListRenderer implements NodeRenderer<ItemListComponent> {

		@Override
		public void render(ItemListComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			drawRect(x, y, width, ItemListComponent.TEXT_HEIGHT, mouseOver && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + ItemListComponent.TEXT_HEIGHT, nodeTreeComponent.isInternalExpanded());
			drawString(TextFormatting.BOLD + nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
			
			if (nodeTreeComponent.getNode().isExpandable() && (nodeTreeComponent.isInternalExpanded() || (mouseOver && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + ItemListComponent.TEXT_HEIGHT)))
				drawExpanded(x + width - ItemListComponent.TEXT_HEIGHT, y, ItemListComponent.TEXT_HEIGHT, nodeTreeComponent.isInternalExpanded());
			
			if (nodeTreeComponent.isInternalExpanded()) {
				for (int i = 0; i < nodeTreeComponent.getNode().size(); i++) {
					Nameable nameable = nodeTreeComponent.getNode().get(i);
					if (nodeTreeComponent.getMovingIndex() != i) {
						if (nodeTreeComponent.getNode().isOrdered()) {
							drawRect(x, y + ItemListComponent.TEXT_HEIGHT + i * ItemListComponent.TEXT_HEIGHT, width, ItemListComponent.TEXT_HEIGHT, !nodeTreeComponent.hasMovingPosition() && mouseOver && mouseX > x && mouseX < x + width && mouseY > y + ItemListComponent.TEXT_HEIGHT + i * ItemListComponent.TEXT_HEIGHT && mouseY < y + ItemListComponent.TEXT_HEIGHT * 2 + i * ItemListComponent.TEXT_HEIGHT, false);
						}
						drawString(nameable.getName(), x + 2, y + ItemListComponent.TEXT_HEIGHT + i * ItemListComponent.TEXT_HEIGHT, 0xFFFFFF);
					}
				}
				if (nodeTreeComponent.getNode().isOrdered()) {
					if (nodeTreeComponent.hasNewPosition()) {
						int[] newPosition = nodeTreeComponent.getNewPosition(x, y, width, height);
						drawBorder(newPosition[0], newPosition[1], newPosition[2], newPosition[3], true);
					}
					if (nodeTreeComponent.hasMovingPosition()) {
						int[] movingPosition = nodeTreeComponent.getMovingPosition(x, y, width, height);
						drawRect(movingPosition[0], movingPosition[1], movingPosition[2], movingPosition[3], false, true);
						drawStringWithShadow(nodeTreeComponent.getNode().get(nodeTreeComponent.getMovingIndex()).getName(), x + 2, movingPosition[1], 0xFFFFFF);
					}
				}
			}
		}
		
	}
	
	public class TaskManagerRenderer implements NodeRenderer<TaskManagerComponent> {

		@Override
		public void render(TaskManagerComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			drawRect(x, y, width, TaskManagerComponent.TEXT_HEIGHT, mouseOver && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + TaskManagerComponent.TEXT_HEIGHT, nodeTreeComponent.isInternalExpanded());
			drawString(TextFormatting.BOLD + nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
			
			if (nodeTreeComponent.isInternalExpanded() || (mouseOver && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + TaskManagerComponent.TEXT_HEIGHT))
				drawExpanded(x + width - TaskManagerComponent.TEXT_HEIGHT, y, TaskManagerComponent.TEXT_HEIGHT, nodeTreeComponent.isInternalExpanded());
			
			if (nodeTreeComponent.isInternalExpanded()) {
				for (int i = 0; i < nodeTreeComponent.getNode().getTaskHolders().size(); i++) {
					String taskHolder = nodeTreeComponent.getNode().getTaskHolders().get(i);
					if (nodeTreeComponent.getMovingIndex() != i) {
						drawRect(x, y + TaskManagerComponent.TEXT_HEIGHT + i * TaskManagerComponent.TEXT_HEIGHT, width, TaskManagerComponent.TEXT_HEIGHT, !nodeTreeComponent.hasMovingPosition() && mouseOver && mouseX > x && mouseX < x + width && mouseY > y + TaskManagerComponent.TEXT_HEIGHT + i * TaskManagerComponent.TEXT_HEIGHT && mouseY < y + TaskManagerComponent.TEXT_HEIGHT * 2 + i * TaskManagerComponent.TEXT_HEIGHT, false);
						drawString(taskHolder, x + 2, y + TaskManagerComponent.TEXT_HEIGHT + i * TaskManagerComponent.TEXT_HEIGHT, 0xFFFFFF);
					}
				}
				if (nodeTreeComponent.hasNewPosition()) {
					int[] newPosition = nodeTreeComponent.getNewPosition(x, y, width, height);
					drawBorder(newPosition[0], newPosition[1], newPosition[2], newPosition[3], true);
				}
				if (nodeTreeComponent.hasMovingPosition()) {
					int[] movingPosition = nodeTreeComponent.getMovingPosition(x, y, width, height);
					drawRect(movingPosition[0], movingPosition[1], movingPosition[2], movingPosition[3], false, true);
					drawStringWithShadow(nodeTreeComponent.getNode().getTaskHolders().get(nodeTreeComponent.getMovingIndex()), x + 2, movingPosition[1], 0xFFFFFF);
				}
			}
		}
		
	}
	
	public class KeybindRenderer implements NodeRenderer<KeybindComponent> {

		@Override
		public void render(KeybindComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();

			drawString(nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
			int nameWidth = getStringWidth(nodeTreeComponent.getNode().getName()) + 6;
			drawRect(x + nameWidth, y, width - nameWidth, KeybindComponent.KEYBIND_HEIGHT, mouseOver && mouseX > x + nameWidth && mouseX < x + width && mouseY > y && mouseY < y + KeybindComponent.KEYBIND_HEIGHT, false);
			drawString(nodeTreeComponent.getNode().getKeyName(), x + width / 2 + nameWidth / 2 - getStringWidth(nodeTreeComponent.getNode().getKeyName()) / 2, y, nodeTreeComponent.isEditing() ? Huzuni.INSTANCE.settings.menuColor.getColor().getRGB() : 0xFFFFFF);
			
			drawRect(x, y + KeybindComponent.KEYBIND_HEIGHT + NodeTreeComponent.PADDING, width, KeybindComponent.CLEAR_HEIGHT, mouseOver && mouseX > x && mouseX < x + width && mouseY > y + KeybindComponent.KEYBIND_HEIGHT + NodeTreeComponent.PADDING && mouseY < y + KeybindComponent.KEYBIND_HEIGHT + NodeTreeComponent.PADDING + KeybindComponent.CLEAR_HEIGHT, false);
			drawString("Clear", x + width / 2 - getStringWidth("Clear") / 2, y + KeybindComponent.KEYBIND_HEIGHT + NodeTreeComponent.PADDING, 0xFFFFFF);
		}
		
	}
	
	public class ModeRenderer implements NodeRenderer<ModeComponent> {

		@Override
		public void render(ModeComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			drawString(TextFormatting.BOLD + nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
			y += ModeComponent.TEXT_HEIGHT;
			drawRect(x, y, ModeComponent.CYCLER_HEIGHT, ModeComponent.CYCLER_HEIGHT, mouseOver && mouseX > x && mouseX < x + ModeComponent.CYCLER_HEIGHT && mouseY > y && mouseY < y + ModeComponent.CYCLER_HEIGHT, false);
			drawArrow(x, y, ModeComponent.CYCLER_HEIGHT, false);
			
			drawRect(x + ModeComponent.CYCLER_HEIGHT, y, width - ModeComponent.CYCLER_HEIGHT * 2, ModeComponent.CYCLER_HEIGHT, mouseOver && mouseX > x + ModeComponent.CYCLER_HEIGHT && mouseX < x + width - ModeComponent.CYCLER_HEIGHT && mouseY > y && mouseY < y + ModeComponent.CYCLER_HEIGHT, false);
			
			drawRect(x + width - ModeComponent.CYCLER_HEIGHT, y, ModeComponent.CYCLER_HEIGHT, ModeComponent.CYCLER_HEIGHT, mouseOver && mouseX > x + width - ModeComponent.CYCLER_HEIGHT && mouseX < x + width && mouseY > y && mouseY < y + ModeComponent.CYCLER_HEIGHT, false);
			drawArrow(x + width - ModeComponent.CYCLER_HEIGHT, y, ModeComponent.CYCLER_HEIGHT, true);
			
			drawString(nodeTreeComponent.getNode().getName(nodeTreeComponent.getNode().getSelected()), x + width / 2 - getStringWidth(nodeTreeComponent.getNode().getName(nodeTreeComponent.getNode().getSelected())) / 2 + 2, y, 0xFFFFFF);
		}
		
	}
	
	public class ModRenderer implements NodeRenderer<ModComponent> {

		@Override
		public void render(ModComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			drawRect(x, y, width - height, height, mouseOver && mouseX > x && mouseX < x + width - height && mouseY > y && mouseY < y + height, nodeTreeComponent.getNode().isEnabled());
			drawRect(x + width - height, y, height, height, mouseOver && mouseX > x + width - height && mouseX < x + width && mouseY > y && mouseY < y + height, nodeTreeComponent.getNode().isEnabled() || nodeTreeComponent.isExpanded());
			if (mouseOver || nodeTreeComponent.isExpanded())
				drawExpanded(x + width - height, y, height, nodeTreeComponent.isExpanded());
			drawString(nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
		}
		
	}
	
	public class ModSettingsRenderer implements NodeRenderer<ModSettingsComponent> {

		@Override
		public void render(ModSettingsComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			drawRect(x, y, width, height, mouseOver, nodeTreeComponent.isExpanded());
			if (mouseOver || nodeTreeComponent.isExpanded())
				drawExpanded(x + width - height, y, height, nodeTreeComponent.isExpanded());
			drawString(nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
		}
		
	}
	
	public class WidgetComponentRenderer implements NodeRenderer<WidgetComponent> {

		@Override
		public void render(WidgetComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			if (nodeTreeComponent.getNode().hasChildren() && (mouseOver || nodeTreeComponent.isExpanded())) {
				drawRect(x, y, width - height, height, mouseOver && mouseX > x && mouseX < x + width - height && mouseY > y && mouseY < y + height, nodeTreeComponent.getNode().isEnabled());
				drawRect(x + width - height, y, height, height, mouseOver && mouseX > x + width - height && mouseX < x + width && mouseY > y && mouseY < y + height, nodeTreeComponent.getNode().isEnabled() || nodeTreeComponent.isExpanded());
				drawExpanded(x + width - height, y, height, nodeTreeComponent.isExpanded());
			} else
				drawRect(x, y, width, height, mouseOver, nodeTreeComponent.getNode().isEnabled());
			drawString(nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
		}
		
	}
	
	public class ValueRenderer implements NodeRenderer<ValueComponent> {

		@Override
		public void render(ValueComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			drawString(nodeTreeComponent.getNode().getName(), x + 2, y, nodeTreeComponent.getSlider().isSliding() ? huzuni.settings.getPrimaryColor().getRGB() : 0xFFFFFF);
			String value = "" + nodeTreeComponent.getNode().getValue() + nodeTreeComponent.getNode().getCarot();
			drawString(value, x + width - getStringWidth(value) - 2, y, nodeTreeComponent.getSlider().isSliding() ? huzuni.settings.getPrimaryColor().getRGB() : 0xFFFFFF);
			
			int[] sliderArea = nodeTreeComponent.getSlider().getSliderArea();
			drawRect(sliderArea[0], sliderArea[1], sliderArea[2], sliderArea[3], false, false);
			
			int[] sliderBar = nodeTreeComponent.getSlider().getSliderBar();
			drawRect(sliderBar[0], sliderBar[1], sliderBar[2], sliderBar[3], mouseOver, true);
		}
		
	}
	
	public class ToggleableRenderer implements NodeRenderer<ToggleableComponent> {

		@Override
		public void render(ToggleableComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			drawRect(x, y, height, height, mouseOver, nodeTreeComponent.getNode().isEnabled());
			drawString(nodeTreeComponent.getNode().getName(), x + height + 2, y, 0xFFFFFF);
		}
		
	}
	
	public class TeamRenderer implements NodeRenderer<TeamComponent> {

		@Override
		public void render(TeamComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			int mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			boolean withinColor = mouseX > x && mouseX < x + TeamComponent.COLOR_SIZE && mouseY > y && mouseY < y + height;
			drawRect(x + TeamComponent.COLOR_SIZE, y, width - TeamComponent.COLOR_SIZE, height, mouseOver && !withinColor, false);
			drawRect(x, y, TeamComponent.COLOR_SIZE, height, mouseOver && withinColor, false);
			GLManager.glColor(nodeTreeComponent.getNode().getColor(), 1F);
			RenderUtils.drawRect(x + 1, y + 1, x + TeamComponent.COLOR_SIZE - 1, y + height - 1);
			drawString(nodeTreeComponent.getNode().getName() + (nodeTreeComponent.getNode().isEnabled() ? ": On" : ": Off"), x + height + 2, y, 0xFFFFFF);
		}
		
	}
	
	public class ItemSelectorRenderer implements NodeRenderer<ItemSelectorComponent> {

		@Override
		public void render(ItemSelectorComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			drawString(nodeTreeComponent.getNode().getName(), x, y, 0xFFFFFF);
			y += ItemSelectorComponent.TEXT_HEIGHT;
			drawRect(x, y, width, height - ItemSelectorComponent.TEXT_HEIGHT, false, false);
			x += 1; y += 1;
			int originalX = x, mouseX = GLManager.getMouseX(), mouseY = GLManager.getMouseY();
			for (ItemData<?> itemData : nodeTreeComponent.getNode().getItemDatas()) {
				if (x + 21 >= originalX + width) {
					x = originalX;
					y += 21;
				}
				drawRect(x, y, 20, 20, mouseOver && mouseX > x && mouseX < x + 21 && mouseY > y && mouseY < y + 21, itemData.isEnabled());
				renderItemStack(itemData.item, x + 2, y + 2, 0);
				x += 21;
			}
		}
		
	}
	
	public class NodeComponentRenderer implements NodeRenderer<NodeComponent> {

		@Override
		public void render(NodeComponent nodeTreeComponent, boolean mouseOver, int x, int y, int width, int height) {
			drawRect(x, y, width, height, mouseOver, nodeTreeComponent.isExpanded());
			if (nodeTreeComponent.getNode().hasChildren() && (mouseOver || nodeTreeComponent.isExpanded())) {
				drawExpanded(x + width - height, y, height, nodeTreeComponent.isExpanded());
			}
			drawString(TextFormatting.BOLD + nodeTreeComponent.getNode().getName(), x + 2, y, 0xFFFFFF);
		}
		
	}
	
}
