package net.halalaboos.huzuni.mod.visual;

import static org.lwjgl.opengl.GL11.glLineWidth;

import java.awt.Color;

import net.halalaboos.huzuni.WaypointManager.Waypoint;
import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventPacket.Type;
import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.halalaboos.huzuni.api.util.render.Texture;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;

/**
 * Renders the waypoints added by the player.
 * */
public class Waypoints extends Mod implements Renderer {
	
	private final Texture location = new Texture("huzuni/location.png");
	
	public final Toggleable distance = new Toggleable("Distance", "Show distance from waypoint"),
			customFont = new Toggleable("Custom font", "Render with custom font renderer"),
			renderIcon = new Toggleable("Icon", "Render with an icon above waypoint"),
			lines = new Toggleable("Lines", "Render a line to the waypoint"),
			deathPoints = new Toggleable("Death points", "Add a waypoint when you die"),
			scale = new Toggleable("Scale", "Scale the nameplates as you are further from the player"),
			background = new Toggleable("Background", "Render backgrounds behind waypoint names");
	
	public final Value opacity = new Value("Opacity", "%", 0F, 100F, 100F, 1F, "Opacity of the icon");
	
	public final Value scaleValue = new Value("Scale Amount", "%", 1F, 1.2F, 3F, "Amount the name plates will be scaled");
			
	public Waypoints() {
		super("Waypoints", "Add/remove points of interest to and from the game");
		setCategory(Category.VISUAL);
		addChildren(distance, customFont, renderIcon, lines, background, deathPoints, scale, scaleValue, opacity);
		distance.setEnabled(true);
		renderIcon.setEnabled(true);
		scale.setEnabled(true);
		this.setEnabled(true);
		this.settings.setDisplayable(false);
		huzuni.commandManager.addCommand(new BasicCommand(new String[] { "waypoint", "wp" }, "Add/remove waypoints.") {

			@Override
			public void giveHelp() {
				huzuni.addChatMessage(".waypoint add \"name\" <x> <y> <z>");
				huzuni.addChatMessage(".waypoint add \"name\"");
				huzuni.addChatMessage(".waypoint remove \"name\"");
			}
			
			@Override
			protected void runCommand(String input, String[] args) throws Exception {
				throw new NullPointerException();
			}
		
		}.addSubcommand(new BasicCommand(new String[] { "add", "a" }, "Add waypoints.") {

			@Override
			protected void runCommand(String input, String[] args) {
				String name = args[0];
				if (args.length > 1) {
					int x = (int) Double.parseDouble(args[1]);
					int y = (int) Double.parseDouble(args[2]);
					int z = (int) Double.parseDouble(args[3]);
					if (huzuni.waypointManager.addWaypoint(new Waypoint(name, new BlockPos(x, y, z)))) {
						huzuni.addChatMessage(String.format("Waypoint '" + TextFormatting.RED + "%s" + TextFormatting.GRAY + "' added at %d, %d, %d", name, x, y, z));
						huzuni.waypointManager.save();
					} else {
						huzuni.addChatMessage(String.format("Waypoint '" + TextFormatting.RED + "%s" + TextFormatting.GRAY + "' already exists!", name));
					}
				} else {
					BlockPos position = mc.player.getPosition();
					if (huzuni.waypointManager.addWaypoint(new Waypoint(name, position))) {
						huzuni.addChatMessage(String.format("Waypoint '" + TextFormatting.RED + "%s" + TextFormatting.GRAY + "' added at %d, %d, %d", name, position.getX(), position.getY(), position.getZ()));
						huzuni.waypointManager.save();
					} else {
						huzuni.addChatMessage(String.format("Waypoint '" + TextFormatting.RED + "%s" + TextFormatting.GRAY + "' already exists!", name));
					}
				}
			}
			
		}).addSubcommand(new BasicCommand(new String[] { "remove", "r", "delete", "del", "d" }, "Remove waypoints.") {

			@Override
			protected void runCommand(String input, String[] args) {
				String name = args[0];
				if (huzuni.waypointManager.removeWaypoint(name)) {
					huzuni.addChatMessage(String.format("Waypoint '" + TextFormatting.RED + "%s" + TextFormatting.GRAY + "' removed!", name));
				} else {
					huzuni.addChatMessage(String.format("Waypoint '" + TextFormatting.RED + "%s" + TextFormatting.GRAY + "' does not exist!", name));
				}
			}
			
		}).addSubcommand(new BasicCommand(new String[] { "clear", "c" }, "Clear waypoints.") {

			@Override
			protected void runCommand(String input, String[] args) {
				int amount = huzuni.waypointManager.clearLocalWaypoints();
				huzuni.addChatMessage(String.format("%d waypoints cleared!", amount));
			}
			
		}));
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
		huzuni.renderManager.addWorldRenderer(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.renderManager.removeWorldRenderer(this);
	}

	@Override
	public void render(float partialTicks) {
		for (int i = 0; i < huzuni.waypointManager.getWaypoints().size(); i++) {
			Waypoint waypoint = huzuni.waypointManager.getWaypoints().get(i);
			if (waypoint.isOnServer()) {
				float renderX = (float) (waypoint.getPosition().getX() - mc.getRenderManager().viewerPosX);
				float renderY = (float) (waypoint.getPosition().getY() - mc.getRenderManager().viewerPosY);
				float renderZ = (float) (waypoint.getPosition().getZ() - mc.getRenderManager().viewerPosZ);
				Color color = waypoint.getColor();
				double scale = (MathUtils.getDistance(waypoint.getPosition()) / 8D) / (1.5F + (2F - scaleValue.getValue()));
				if (scale < 1D || !this.scale.isEnabled())
					scale = 1D;
				if (lines.isEnabled())
					huzuni.renderManager.addLine(renderX, renderY, renderZ, (float) color.getRed() / 255F, (float) color.getGreen() / 255F, (float) color.getBlue() / 255F, opacity.getValue() / 100F);
				GlStateManager.pushMatrix();
				RenderUtils.prepareBillboarding(renderX, renderY, renderZ, true);
				GlStateManager.scale(scale, scale, scale);
				location.bindTexture();
				GLManager.glColor(color, opacity.getValue() / 100F);
				String renderName = waypoint.getName() + (distance.isEnabled() ? " (" + ((int) waypoint.getDistance()) + ")" : "");
				if (customFont.isEnabled()) {
					int width = huzuni.guiFontRenderer.getStringWidth(renderName);
					if (renderIcon.isEnabled())
						location.render(-16F, -16F - (huzuni.guiFontRenderer.getStringHeight(renderName)) * 2F, 32, 32);
					if (background.isEnabled()) {
						GlStateManager.disableTexture2D();
						GLManager.glColor(0F, 0F, 0F, opacity.getValue() / 100F);
						RenderUtils.drawBorderRect(-width / 2 - 2, -2, width / 2 + 2, 10, 2F);
						GLManager.glColor(1F, 1F, 1F, opacity.getValue() / 100F);
						GlStateManager.enableTexture2D();
					}
					huzuni.guiFontRenderer.drawStringWithShadow(renderName, -width / 2, -2, 0xFFFFFF);
				} else {
					int width = mc.fontRenderer.getStringWidth(renderName);
					if (renderIcon.isEnabled())
						location.render(-16F, -16F - (mc.fontRenderer.FONT_HEIGHT) * 2F, 32, 32);
					
					if (background.isEnabled()) {
						GlStateManager.disableTexture2D();
						GLManager.glColor(0F, 0F, 0F, opacity.getValue() / 100F);
						RenderUtils.drawBorderRect(-width / 2 - 2, -2, width / 2 + 2, 10, 2F);
						GLManager.glColor(1F, 1F, 1F, opacity.getValue() / 100F);
						GlStateManager.enableTexture2D();
					}
					mc.fontRenderer.drawStringWithShadow(renderName, -width / 2, 0, 0xFFFFFF);
			        GlStateManager.disableAlpha();
				}
				GlStateManager.disableTexture2D();
				GlStateManager.popMatrix();
			}
		}
		glLineWidth(huzuni.settings.lineSize.getValue());
	}
	
	@EventMethod
	public void onPacket(EventPacket event) {
		if (event.type == Type.READ) {
			if (event.getPacket() instanceof SPacketUpdateHealth) {
				SPacketUpdateHealth packet = (SPacketUpdateHealth) event.getPacket();
				if (packet.getHealth() <= 0.0F && mc.player.getPosition() != null) {
					huzuni.waypointManager.addDeathPoint(mc.player.getPosition());
					huzuni.waypointManager.save();
				}
			}
		}
	}
	
}
