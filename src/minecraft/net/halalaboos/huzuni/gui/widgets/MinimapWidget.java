package net.halalaboos.huzuni.gui.widgets;

import java.util.List;

import net.halalaboos.huzuni.WaypointManager.Waypoint;
import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.halalaboos.huzuni.api.gui.widget.Widget;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.halalaboos.huzuni.api.util.render.Texture;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import static org.lwjgl.opengl.GL11.*;

public class MinimapWidget extends Widget {
	
	private final int diameter = 128, entityDiameter = 8, waypointDiameter = 8, borderSize = 2;
	
    private final Texture minimap = new Texture("huzuni/minimap.png"), entity = new Texture("huzuni/entity.png"), location = new Texture("huzuni/location.png");
	
	private final Toggleable names = new Toggleable("Names", "Render the names of entities/waypoints");
	
	private final Toggleable waypoints = new Toggleable("Waypoints", "Render waypoints within the map");

    private final Mode<String> entityMode = new Mode<String>("Entity mode", "Render players in a certain way yo", "Arrows", "Heads");
    
	public MinimapWidget(WidgetManager menuManager) {
		super("Minimap", "Renders a mini map", menuManager);
		this.setWidth(diameter);
		this.setHeight(diameter);
		this.addChildren(names, waypoints, entityMode);
		waypoints.setEnabled(true);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		float entitySize = entityDiameter / 2;
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.translate(x + diameter / 2, y + diameter / 2, 0F);
		GLManager.glColor(1F, 1F, 1F, 1F);
		minimap.render(-diameter / 2, -diameter / 2, diameter, diameter);
		entity.render(-entitySize, -entitySize, entityDiameter, entityDiameter);
		
		GLManager.glScissor(x, y, x + width, y + height);
		glEnable(GL_SCISSOR_TEST);
		
		// TODO: Fix this.
		/*
		for (int i = 0; i < mc.world.playerEntities.size(); i++) {
			EntityPlayer player = mc.world.playerEntities.get(i);
			
			if (mc.player == player)
				continue;
			
			float distance = MathHelper.sqrt((float) (mc.player.posX - player.posX) * (float) (mc.player.posX - player.posX) + (float) (mc.player.posZ - player.posZ) * (float) (mc.player.posZ - player.posZ));
			double angle = MathHelper.wrapDegrees(mc.player.rotationYaw - (Math.atan2(mc.getRenderManager().viewerPosZ - MathUtils.interpolate(player.prevPosZ, player.posZ, mc.timer.renderPartialTicks), mc.getRenderManager().viewerPosX - MathUtils.interpolate(player.prevPosX, player.posX, mc.frameTimer.renderPartialTicks)) * 180.0D / Math.PI));
			double pX = Math.cos(Math.toRadians(angle)) * distance;
			double pY = -Math.sin(Math.toRadians(angle)) * distance;
			boolean friend = huzuni.friendManager.isFriend(player.getName()), sneaking = player.isSneaking();
			int original = friend ? huzuni.friendManager.getColor().getRGB() : sneaking ? 0xFFFF0080 : 0xFFFFFFFF;
			int color = huzuni.settings.team.isEnabled() ? (huzuni.settings.team.isTeam(player) ? huzuni.settings.team.getColor() : huzuni.settings.team.getTeamColor(player) != -1 ? huzuni.settings.team.getTeamColor(player) : original) : original;
			String name = player.getName();
			boolean flipName = (pX > (diameter / 2) - entitySize - borderSize);
			GlStateManager.pushMatrix();
			translate(pX, pY, entitySize, entitySize);
			renderPlayer(player, entitySize);
			if (names.isEnabled())
				renderNameplate(name, false, flipName, color);
			GlStateManager.popMatrix();
		}*/
		
		if (waypoints.isEnabled()) {
			List<Waypoint> localWaypoints = huzuni.waypointManager.getLocalWaypoints();
			for (int i = 0; i < localWaypoints.size(); i++) {
				Waypoint waypoint = localWaypoints.get(i);
				float distance = MathHelper.sqrt((float) (mc.player.posX - waypoint.getPosition().getX()) * (float) (mc.player.posX - waypoint.getPosition().getX()) + (float) (mc.player.posZ - waypoint.getPosition().getZ()) * (float) (mc.player.posZ - waypoint.getPosition().getZ()));
				double angle = MathHelper.wrapDegrees(mc.player.rotationYaw - (Math.atan2(mc.getRenderManager().viewerPosZ - waypoint.getPosition().getZ(), mc.getRenderManager().viewerPosX - waypoint.getPosition().getX()) * 180.0D / Math.PI));
				double wX = Math.cos(Math.toRadians(angle)) * distance;
				double wY = -Math.sin(Math.toRadians(angle)) * distance;
				GlStateManager.pushMatrix();
				translate(wX, wY, entitySize, entitySize);
				GLManager.glColor(waypoint.getColor());
				this.location.render(-waypointDiameter / 2, -waypointDiameter / 2, waypointDiameter, waypointDiameter);
				if (names.isEnabled())
					renderNameplate(waypoint.getName(), true, false, waypoint.getColor().getRGB());
				GlStateManager.popMatrix();
			} 
		}
		glDisable(GL_SCISSOR_TEST);
		GlStateManager.popMatrix();
	}

	/**
     * Renders the player with the given entity size.
     * */
	private void renderPlayer(EntityPlayer player, float entitySize) {
		GLManager.glColor(1F, 1F, 1F, 1F);
		if (entityMode.getSelected() == 0) {
			GlStateManager.rotate(player.rotationYaw - mc.player.rotationYaw, 0F, 0F, 1F);
			entity.render(-entitySize, -entitySize, entityDiameter, entityDiameter);
			GlStateManager.rotate(-(player.rotationYaw - mc.player.rotationYaw), 0F, 0F, 1F);
		} else if (entityMode.getSelected() == 1) {
			if (player instanceof AbstractClientPlayer) {
				AbstractClientPlayer entityPlayer = (AbstractClientPlayer) player;
				GlStateManager.scale(0.5F, 0.5F, 0.5F);
				mc.getTextureManager().bindTexture(entityPlayer.getLocationSkin());
				RenderUtils.drawTextureRect(-entitySize, -entitySize, entityDiameter * 2, entityDiameter * 2, 8F / 64F, 8F / 64F, 16F / 64F, 16F / 64F);
				GlStateManager.scale(2F, 2F, 2F);
			}
		}
	}

	/**
     * Renders the name as a name plate with the qualities specified.
     * */
	private void renderNameplate(String name, boolean centered, boolean flipName, int color) {
		GlStateManager.scale(0.75F, 0.75F, 0.75F);
		if (centered) {
			theme.drawBackgroundRect(-theme.getStringWidth(name) / 2 - 2, 4, (theme.getStringWidth(name) / 2) * 2 + 4, 11, false);
			theme.drawStringWithShadow(name, -theme.getStringWidth(name) / 2, 3, color);
		} else {
			theme.drawBackgroundRect(flipName ? -theme.getStringWidth(name) - 2 : -2, -7, theme.getStringWidth(name) + 4, 12, false);
			theme.drawStringWithShadow(name, flipName ? -theme.getStringWidth(name) : 0, -7, color);
		}
	}

	/**
	 * Translates to the x, y positions with respect to the diameter and xSize and ySize
	 * */
	private void translate(double x, double y, double xSize, double ySize) {
		if (x > (diameter / 2) - xSize - borderSize)
			x = (diameter / 2) - xSize - borderSize;
		
		if (x < -(diameter / 2) + xSize + borderSize)
			x = -(diameter / 2) + xSize + borderSize;
		
		if (y > (diameter / 2) - ySize - borderSize)
			y = (diameter / 2) - ySize - borderSize;
		
		if (y < -(diameter / 2) + ySize + borderSize)
			y = -(diameter / 2) + ySize + borderSize;
		GlStateManager.translate(x, y, 0F);
	}
	
}
