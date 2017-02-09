package net.halalaboos.huzuni.mod.visual;

import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.mod.CommandPointer;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.halalaboos.huzuni.api.util.render.Texture;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.halalaboos.huzuni.mod.movement.Freecam;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.math.BlockPos;
import static org.lwjgl.opengl.GL11.*;

/**
 * Tracks the player's position and renders bread images at each point.
 * */
public class Breadcrumb extends BasicMod implements Renderer {

	private final Texture breadIcon = new Texture("huzuni/bread.png");

	private final List<BlockPos> points = new ArrayList<BlockPos>();

	private BlockPos lastPosition = new BlockPos(0, 0, 0);
	
	public final Value opacity = new Value("Opacity", "%", 0F, 50F, 100F, 1F, "Opacity of the icon.");

    public final Value bounce = new Value("Bounce", "" ,0F, 0F, 10F, "Amount the icon will bounce.");

    public final Value distance = new Value("Distance", " blocks", 1F, 15F, 30F, 1F, "Distance between each point.");

    public final Toggleable lines = new Toggleable("Lines", "Render lines between each point.");

    public final Toggleable bread = new Toggleable("Bread", "Render the bread icon at each point.");

    public final Toggleable clearOnDeath = new Toggleable("Clear on death", "When enabled, the points will clear when the player dies.");

	public Breadcrumb() {
		super("Breadcrumb", "Retrace your steps as bread is placed in your path");
		this.setCategory(Category.VISUAL);
		addChildren(lines, bread, clearOnDeath, opacity, distance, bounce);
		lines.setEnabled(true);
		bread.setEnabled(true);
		clearOnDeath.setEnabled(true);
		huzuni.commandManager.generateCommands(this);
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
		lastPosition = new BlockPos(0, 0, 0);
		clearPoints();
	}
	
	@Override
	public void render(float partialTicks) {
		if (bread.isEnabled()) {
			for (BlockPos position : points) {
				float renderX = (float) (position.getX() - mc.getRenderManager().viewerPosX);
				float renderY = (float) (position.getY() - mc.getRenderManager().viewerPosY);
				float renderZ = (float) (position.getZ() - mc.getRenderManager().viewerPosZ);
				GLManager.glColor(1F, 1F, 1F, opacity.getValue() / 100F);
				GlStateManager.pushMatrix();
				RenderUtils.prepareBillboarding(renderX, renderY, renderZ, true);
				GlStateManager.scale(0.25, 0.25, 0.25);
				breadIcon.bindTexture();
				breadIcon.render(-32F, -32F + (bounce.getValue() == 0F ? 0F : (float) (bounce.getValue() * Math.cos(Math.toRadians(System.currentTimeMillis() % 360)))), 64, 64);
				GlStateManager.popMatrix();
			}
		}
		GlStateManager.disableTexture2D();
		if (lines.isEnabled()) {
			GLManager.glColor(1F, 1F, 1F, opacity.getValue() / 100F);
			Tessellator tessellator = Tessellator.getInstance();
	    	VertexBuffer renderer = tessellator.getBuffer();
	    	renderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
			for (BlockPos position : points) {
				float renderX = (float) (position.getX() - mc.getRenderManager().viewerPosX);
				float renderY = (float) (position.getY() - mc.getRenderManager().viewerPosY);
				float renderZ = (float) (position.getZ() - mc.getRenderManager().viewerPosZ);
		    	renderer.pos(renderX, renderY, renderZ).endVertex();
			}
	    	tessellator.draw();
		}
		if (lastPosition.distanceSq(mc.player.posX, mc.player.posY, mc.player.posZ) >= distance.getValue() && !Freecam.INSTANCE.isEnabled()) {
			lastPosition = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
			points.add(lastPosition);
		}
	}
	
	@EventMethod
	public void onPacket(EventPacket event) {
		if (clearOnDeath.isEnabled()) {
			if (event.getPacket() instanceof SPacketUpdateHealth) {
				SPacketUpdateHealth packet = (SPacketUpdateHealth)event.getPacket();
				if (packet.getHealth() <= 0F) {
					clearPoints();
				}
			}
		}
	}
	
	@CommandPointer({ "clearcrumbs" })
	public void clearPoints() {
		points.clear();
		huzuni.addNotification(NotificationType.INFO, this, 5000, "Trail cleared.");
	}
	
	
}
