package com.matthewhatcher.huzuni.mods.visual;

import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.MinecraftUtils;
import net.halalaboos.huzuni.api.util.render.Box;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Basically copied Hal's ESP, but I'll be updating this as I go. Hopefully making it a bit better and cleaner.
 */
public class HuzuniESP extends BasicMod implements Renderer {
	private final Box[] espBox = new Box[0xFFFFF];
	private final Tessellator tess = Tessellator.getInstance();
	
	public final Value opacity = new Value("Opacity", "%", 0F, 50F, 100F, 1F, "Opacity of the box");
	public final Mode<String> mode = new Mode<String>("Mode", "Style of the ESP boxes.", "None", "Hitboxes", "Rectangle", "Lines");
	public final Toggleable togglePlayers = new Toggleable("Players", "Traces to players."),
							toggleMobs = new Toggleable("Mobs", "Traces to mobs."),
							toggleAnimals = new Toggleable("Animals", "Traces to animals "),
							toggleInvisibles = new Toggleable("Invisible", "Trace to invisible entities"),
							toggleLines = new Toggleable("Lines", "Traces lines to each entity"),
							toggleProperties = new Toggleable("Properties", "Ignores players without properties"),
							toggleCheckAge = new Toggleable("Check age", "Check the age of the entity before rendering");
	
	
	public HuzuniESP() {
		super("ESP", "Render boxes/lines to and around entities.", Keyboard.KEY_B);
		this.setCategory(Category.VISUAL);
		this.addChildren(togglePlayers, toggleMobs, toggleAnimals, toggleInvisibles, toggleLines, toggleProperties, toggleCheckAge, mode, opacity);
		this.settings.setDisplayable(false);
		togglePlayers.setEnabled(true);
		toggleLines.setEnabled(true);
		mode.setSelectedItem(1);
	}
	
	@Override
	public void onEnable() {
		huzuni.renderManager.addWorldRenderer(this);
		super.onEnable();
	}
	
	@Override
	public void onDisable() {
		huzuni.renderManager.removeWorldRenderer(this);
		super.onDisable();
	}

	@Override
	public void render(float partialTicks) {
		for(Object obj : mc.world.loadedEntityList) {
			if(!(obj instanceof EntityLivingBase)) {
				continue;
			}
			
			EntityLivingBase entity = (EntityLivingBase) obj;
			
			if(!isValidEntity(entity)) {
				continue;
			}
			
			int entityId = EntityList.REGISTRY.getIDForObject(entity.getClass());
			float renderX = (float) (MathUtils.interpolate(entity.prevPosX, entity.posX, partialTicks) - mc.getRenderManager().viewerPosX);
			float renderY = (float) (MathUtils.interpolate(entity.prevPosY, entity.posY, partialTicks) - mc.getRenderManager().viewerPosY);
			float renderZ = (float) (MathUtils.interpolate(entity.prevPosZ, entity.posZ, partialTicks) - mc.getRenderManager().viewerPosZ);
			float distance = mc.player.getDistanceToEntity(entity);
			boolean friend = huzuni.friendManager.isFriend(entity.getName());
			
			if(toggleLines.isEnabled()) {
				drawLine(renderX, renderY, renderZ, entity, distance, friend);
			}
			
			setColor(entity, distance, friend, false, opacity.getValue() / 100F);
			
			switch(mode.getSelected()) {
				case 1: // Hitboxes
					GlStateManager.pushMatrix();
					GlStateManager.translate(renderX, renderY, renderZ);
					GlStateManager.rotate(-entity.rotationYaw, 0F, 1F, 0F);
					generateVbo(entity, entityId);
					espBox[entityId].render();
					GlStateManager.popMatrix();
					break;
				case 2: // Rectangle
					GlStateManager.pushMatrix();
					GlStateManager.rotate(-mc.player.rotationYaw, 0F, 1F, 0F);
					float width = (float) (entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX), height = (float) (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY);
					VertexBuffer vertexBuffer = tess.getBuffer();
			    	vertexBuffer.begin(GL_LINE_LOOP, DefaultVertexFormats.POSITION);
			    	vertexBuffer.pos(-width, 0, 0F).endVertex();
			    	vertexBuffer.pos(-width, height, 0F).endVertex();
			    	vertexBuffer.pos(width, height, 0F).endVertex();
			    	vertexBuffer.pos(width, 0, 0F).endVertex();
			    	tess.draw();
					GlStateManager.popMatrix();
					break;
				case 3: // Lines
					GLManager.glColor(1F, distance / 64F, 0F, 1F);
			    	VertexBuffer renderer = tess.getBuffer();
			    	renderer.begin(GL_LINES, DefaultVertexFormats.POSITION);
			    	renderer.pos(renderX, renderY, renderZ).endVertex();
			    	renderer.pos(renderX, renderY + entity.height, renderZ).endVertex();
			    	tess.draw();
					break;
			}
		}
	}
	
	private boolean isValidEntity(EntityLivingBase entity) {
		if(entity == mc.player ||
				entity.isDead ||
				!MinecraftUtils.checkType(entity, toggleInvisibles.isEnabled(), toggleMobs.isEnabled(), toggleAnimals.isEnabled(), togglePlayers.isEnabled()) ||
				(toggleProperties.isEnabled() && !MinecraftUtils.checkProperties(entity)) ||
				(toggleCheckAge.isEnabled() && !MinecraftUtils.checkAge(entity))
				) {
			return false;
		}
		
		return true;
	}
	
	private void generateVbo(EntityLivingBase entity, int entityId) {
		if (espBox[entityId] == null) {
			double wX = entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX,
					wY = entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY,
					wZ = entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ;
			double minX = -wX/2, minY = 0, minZ = -wZ/2, maxX = wX/2, maxZ = wZ/2;
			espBox[entityId] = new Box(new AxisAlignedBB(minX, minY, minZ, maxX, wY, maxZ));
			espBox[entityId].setOpaque(false);
			espBox[entityId].render();
			huzuni.addNotification(NotificationType.INFO, this, 5000, "Generating new Vbo for entity", "\"" + entity.getName() + "\"");
		}
	}
	
	private void drawLine(float renderX, float renderY, float renderZ, EntityLivingBase entity, float distance, boolean friend) {
		float opacity = this.opacity.getValue() / 100F;
		if (friend)
			huzuni.renderManager.addLine(renderX, renderY, renderZ, huzuni.friendManager.getColor(), opacity);
		else {
			if (huzuni.settings.team.isEnabled()) {
				if (huzuni.settings.team.isTeam(entity)) {
					int color = huzuni.settings.team.getColor();
					huzuni.renderManager.addLine(renderX, renderY, renderZ, (float) (color >> 16 & 255) / 255F, (float) (color >> 8 & 255) / 255F, (float) (color & 255) / 255F, opacity);
					return;
				} else {
					int teamColor = huzuni.settings.team.getTeamColor(entity);
					if (teamColor != -1) {
						huzuni.renderManager.addLine(renderX, renderY, renderZ, (float) (teamColor >> 16 & 255) / 255F, (float) (teamColor >> 8 & 255) / 255F, (float) (teamColor & 255) / 255F, opacity);
						return;
					}
				}
			}
			huzuni.renderManager.addLine(renderX, renderY, renderZ, 1F, distance / 64F, 0F, opacity);
		}
	}
	
	private void setColor(EntityLivingBase entity, float distance, boolean friend, boolean lines, float opacity) {
		if (friend)
			GLManager.glColor(huzuni.friendManager.getColor(), opacity);
		else {
			if (huzuni.settings.team.isEnabled()) {
				if (huzuni.settings.team.isTeam(entity)) {
					GLManager.glColor(huzuni.settings.team.getColor(), opacity);
					return;
				} else {
					int teamColor = huzuni.settings.team.getTeamColor(entity);
					if (teamColor != -1) {
						GLManager.glColor(teamColor, opacity);
						return;
					}
				}
			}
			if (lines)
				GLManager.glColor(1F, distance / 64F, 0F, opacity);
			else {
				if (entity.hurtResistantTime > 0)
					GLManager.glColor(1F, 1F - ((float) entity.hurtResistantTime / ((float) entity.maxHurtResistantTime / 2F)), 0F, opacity);
				else
					GLManager.glColor(0F, 1F, 0F, opacity);
			}
		}
	}
}
