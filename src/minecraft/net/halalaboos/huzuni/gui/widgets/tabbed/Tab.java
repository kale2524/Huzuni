package net.halalaboos.huzuni.gui.widgets.tabbed;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.api.gui.Theme;
import net.halalaboos.huzuni.api.gui.widget.Glue;
import net.halalaboos.huzuni.api.settings.Nameable;
import net.halalaboos.huzuni.api.util.IncrementalPosition;
import net.halalaboos.huzuni.api.util.render.GLManager;
import org.lwjgl.opengl.GL11;

/**
 * Tab used within the TabbedMenuWidget.
 * */
public class Tab implements Nameable {

    /**
     * Size of the item rendered to indicate tabs are expandable.
     * */
    public static final int EXPAND_SIZE = 12;

	private final List<Tab> tabs = new ArrayList<Tab>();
	
	private final IncrementalPosition selectionPosition = new IncrementalPosition(), openedPosition = new IncrementalPosition(), openedWidth = new IncrementalPosition();

    private final String name, description;

	private int index, width, internalWidth = 0, internalHeight = 0;

    private boolean expanded = false;
	
	public Tab(String name, String description) {
		this.name = name;
        this.description = description;
		openedPosition.setIncrement(8F, 8F, 8F);
        openedWidth.setIncrement(8F, 8F, 8F);
	}

	/**
     * Renders the tab and it's children if expanded.
     *
     * */
	public void render(TabbedMenuWidget tabbedMenuWidget, Theme theme, boolean highlight, int x, int y, int width, int height) {
        renderTab(tabbedMenuWidget, theme, highlight, x, y, width, height);

        openedPosition.setFinalPosition(highlight && expanded ? (tabbedMenuWidget.getGlue().isRight() ? -width : width) : 0, 0, 0);
        openedPosition.updatePosition();
        openedWidth.setFinalPosition(highlight && expanded ? (tabbedMenuWidget.getGlue().isRight() ? -internalWidth : internalWidth) : 0, 0, 0);
        openedWidth.updatePosition();

        if (hasTabs() && highlight && (expanded || (!openedPosition.hasFinished() && tabbedMenuWidget.animations.isEnabled()))) {
            int openedX = getOffsetX(tabbedMenuWidget.getGlue(), x, x + width), openedY = y;
            int moveOpenedX = (int) (tabbedMenuWidget.animations.isEnabled() ? openedPosition.getX() : openedPosition.getFinalX());
            int moveOpenedWidth = (int) (tabbedMenuWidget.animations.isEnabled() ? openedWidth.getX() : openedWidth.getFinalX());
            theme.drawBorder(tabbedMenuWidget.getGlue().isRight() ? openedX + moveOpenedX : openedX, openedY, tabbedMenuWidget.getGlue().isRight() ? -moveOpenedWidth : moveOpenedWidth, internalHeight, false);
            if (!openedPosition.hasFinished()) {
                GLManager.glScissor(tabbedMenuWidget.getGlue().isRight() ? openedX + moveOpenedWidth : openedX, openedY, tabbedMenuWidget.getGlue().isRight() ? openedX : openedX + moveOpenedWidth, openedY + internalHeight);
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }

            for (int i = 0; i < tabs.size(); i++) {
                Tab tab = tabs.get(i);
                if (i == index) {
                    selectionPosition.setFinalPosition(0, openedY, 0);
                    selectionPosition.updatePosition();
                }
                tab.render(tabbedMenuWidget, theme, i == index && (selectionPosition.hasFinished() || !tabbedMenuWidget.animations.isEnabled()), (tabbedMenuWidget.getGlue().isRight() ? openedX + moveOpenedX : openedX), openedY, internalWidth, tab.getHeight());
                openedY += tab.getHeight();
            }

            if (!openedPosition.hasFinished())
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
    }

    /**
     * @return The x position which should be used based on the glue provided.
     * */
    private int getOffsetX(Glue glue, int x, int x1) {
        return glue.isRight() ? x - 2 : x1 + 2;
    }

    /**
     * Renders the actual tab.
     * */
    protected void renderTab(TabbedMenuWidget tabbedMenuWidget, Theme theme, boolean highlight, int x, int y, int width, int height) {
        theme.drawBackgroundRect(x, y, width, height, highlight);
        if (highlight)
            theme.drawStringWithShadow(getName(), x + 4, y, isHighlighted() ? 0xFFFFFF : 0xBBBBBB);
        else
            theme.drawString(getName(), x + 2, y, isHighlighted() ? 0xFFFFFF : 0xBBBBBB);
        if (hasTabs() && highlight)
            theme.drawArrow(x + width - EXPAND_SIZE, y, EXPAND_SIZE, true, isExpanded() ? 0xFFFFFFFF : 0xFFBBBBBB);
    }

    /**
     * Invoked when the player presses enter on this tab.
     * */
    protected void keyReturn() {}

	/**
     * Invoked when the player attempts to modify the tab.
     * */
	public void modifyTab(TabMovement tabMovement) {
        if (expanded && hasTabs() && tabs.get(index).isExpanded()) {
            tabs.get(index).modifyTab(tabMovement);
        } else {
            switch (tabMovement) {
                case LEFT:
                    expanded = false;
                    break;
                case RIGHT:
                    if (hasTabs()) {
                        if (expanded)
                            tabs.get(index).modifyTab(tabMovement);
                        expanded = true;
                    }
                    break;
                case UP:
                    if (expanded) {
                        index--;
                        if (index < 0)
                            index = tabs.size() - 1;
                    }
                    break;
                case DOWN:
                    if (expanded) {
                        index++;
                        if (index >= tabs.size())
                            index = 0;
                    }
                    break;
                case RETURN:
                    if (expanded && hasTabs())
                        tabs.get(index).keyReturn();
                    else
                        keyReturn();
                    break;
                default:
            }
        }
    }

    /**
     * @return True if this tab has children tabs.
     * */
    public boolean hasTabs() {
        return !tabs.isEmpty();
    }

    /**
     * Updates the width/height of this tab as well as the internal width.
     * */
    public void updateSize(Theme theme, int minimumWidth) {
        this.width = theme.getStringWidth(getName()) + (hasTabs() ? EXPAND_SIZE : 0);
        this.internalWidth = minimumWidth;
        this.internalHeight = 0;
        for (Tab tab : tabs) {
            tab.updateSize(theme, minimumWidth);
            int tabWidth = tab.getWidth();
            if (internalWidth <= tabWidth + 6)
                this.internalWidth = tabWidth + 6;
            this.internalHeight += tab.getHeight();
        }
    }

    public List<Tab> getTabs() {
        return tabs;
    }

	public void addTab(Tab tab) {
		tabs.add(tab);
	}
	
	public int getWidth() {
		return width;
	}

    public int getHeight() {
        return 12;
    }

    public int getInternalWidth() {
		return internalWidth;
	}

    public int getInternalHeight() {
        return internalHeight;
    }

    public int getIndex() {
		return index;
	}

	public boolean isHighlighted() {
        return true;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
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
     * Movement types available for each tab. <br/>
     * LEFT and RIGHT should be reserved for expanding tabs. <br/>
     * UP and DOWN should be reserved for moving up or down within the tabs.
     * */
	public enum TabMovement {
        UP, DOWN, LEFT, RIGHT, RETURN
    }

}
