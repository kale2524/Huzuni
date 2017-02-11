package net.halalaboos.huzuni.mod.visual;

import static org.lwjgl.opengl.GL11.*;

import java.util.List;

import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemSnowball;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.structure.StructureBoundingBox;

/**
 * Renders the trajectory of any throwable item held by the player along with the projectiles within the air.
 * */
public class Projectiles extends BasicMod implements Renderer {

	private final Tessellator tessellator = Tessellator.getInstance();
	
	private final Cylinder cylinder = new Cylinder();

	public final Toggleable lines = new Toggleable("Lines", "Render lines showing the projectile of "),
			landing = new Toggleable("Landing", "Render a landing position of each projectile"),
			arrows = new Toggleable("Arrows", "Show other arrows/items trajectories from other players");
	
	public final Value landingSize = new Value("Landing size", "", 0.5F, 0.5F, 2F, "Adjust the size of the landing pad");
	
	public Projectiles() {
		super("Projectiles", "Render a trajectory showing the path of a projectile");
		setCategory(Category.VISUAL);
		addChildren(lines, landing, arrows, landingSize);
		lines.setEnabled(true);
		landing.setEnabled(true);
		arrows.setEnabled(true);
	}

	@Override
	public void onEnable() {
		huzuni.renderManager.addWorldRenderer(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.renderManager.removeWorldRenderer(this);
	}
	
	@Override
	public void render(float partialTicks) {
		ItemStack item = getApplicableItem();
		if (item != null) {
			int mode = 0;
			float velocity;

			if (item.getItem() instanceof ItemBow)
                mode = 1;
            else if (item.getItem() instanceof ItemSplashPotion)
            	mode = 2;
			
			double posX = mc.getRenderManager().viewerPosX - (double) (MathHelper.cos(mc.player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F),
	                posY = (mc.getRenderManager().viewerPosY + (double) mc.player.getEyeHeight()) - 0.10000000149011612D,
	                posZ = mc.getRenderManager().viewerPosZ - (double) (MathHelper.sin(mc.player.rotationYaw / 180.0F * (float) Math.PI) * 0.16F),
	                motionX = (double) (-MathHelper.sin(mc.player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(mc.player.rotationPitch / 180.0F * (float) Math.PI)) * (mode == 1 ? 1.0 : 0.4),
	                motionY = (double) (-MathHelper.sin(mc.player.rotationPitch / 180.0F * (float) Math.PI)) * (mode == 1 ? 1.0 : 0.4),
	                motionZ = (double) (MathHelper.cos(mc.player.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(mc.player.rotationPitch / 180.0F * (float) Math.PI)) * (mode == 1 ? 1.0 : 0.4);
			if (mc.player.getItemInUseCount() <= 0 && mode == 1)
				velocity = 1F;
			else
				velocity = ItemBow.getArrowVelocity(72000 - mc.player.getItemInUseCount());
			renderProjectile(mode, velocity, posX, posY, posZ, motionX, motionY, motionZ, null);
		}
		if (arrows.isEnabled()) {
			for (Object o : mc.world.loadedEntityList) {
				if (o instanceof EntityArrow) {
					EntityArrow entity = (EntityArrow) o;
					if (entity.isDead || entity.onGround)
						continue;
					renderProjectile(1, -1, entity.posX, entity.posY, entity.posZ, entity.motionX, entity.motionY, entity.motionZ, entity.shootingEntity != null ? entity.shootingEntity.getName() : null);
				}
			}
		}
	}
	
	private void renderProjectile(int mode, float velocity, double x, double y, double z, double motionX, double motionY, double motionZ, String text) {
		if (velocity != -1) {
			float theta = MathHelper.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
	        motionX /= (double) theta;
	        motionY /= (double) theta;
	        motionZ /= (double) theta;
	        motionX *= (mode == 1 ? (velocity * 2) : 1) * getMult(mode);
	        motionY *= (mode == 1 ? (velocity * 2) : 1) * getMult(mode);
	        motionZ *= (mode == 1 ? (velocity * 2) : 1) * getMult(mode);
		}
        boolean hasLanded = false, isEntity = false;
        RayTraceResult collision = null;
        float size = mode == 1 ? 0.3F : 0.25F;
        float gravity = getGravity(mode);
        
        if (text != null) {
        	GlStateManager.pushMatrix();
        	RenderUtils.prepareBillboarding((float) (x - mc.getRenderManager().viewerPosX), (float) (y - mc.getRenderManager().viewerPosY), (float) (z - mc.getRenderManager().viewerPosZ), true);
        	GlStateManager.enableTexture2D();
        	mc.fontRendererObj.drawStringWithShadow(text, -mc.fontRendererObj.getStringWidth(text) / 2, 1, 0xFFFFFFF);
        	GlStateManager.disableTexture2D();
            GlStateManager.popMatrix();
        }
        GLManager.glColor(0F, 1F, 0F, 1F);
    	VertexBuffer renderer = tessellator.getBuffer();
        if (lines.isEnabled())
    		renderer.begin(GL_LINE_STRIP, DefaultVertexFormats.POSITION);
    	for (; !hasLanded && y > 0;) {
    		Vec3d present = new Vec3d(x, y, z);
            Vec3d future = new Vec3d(x + motionX, y + motionY, z + motionZ);
            RayTraceResult possibleCollision = mc.world.rayTraceBlocks(present, future, false, true, false);
            if (possibleCollision != null) {
                hasLanded = true;
                collision = possibleCollision;
            }
            AxisAlignedBB boundingBox = new AxisAlignedBB(x - size, y - size, z - size, x + size, y + size, z + size);

            List<Entity> entities = mc.world.getEntitiesWithinAABBExcludingEntity(mc.player, boundingBox.addCoord(motionX, motionY, motionZ).expand(1.0D, 1.0D, 1.0D));
            for (int index = 0; index < entities.size(); ++index) {
                Entity entity = entities.get(index);

                if (entity.canBeCollidedWith() && entity != mc.player) {
                    AxisAlignedBB entityBoundingBox = entity.getEntityBoundingBox().expand(0.3D, 0.3D, 0.3D);
                    RayTraceResult entityCollision = entityBoundingBox.calculateIntercept(present, future);
                    if (entityCollision != null) {
                        hasLanded = true;
                        isEntity = true;
                        collision = entityCollision;
                    }
                }
            }
            
	    	x += motionX;
            y += motionY;
            z += motionZ;
            float motionAdjustment = 0.99F;
            if (isInMaterial(boundingBox, Material.WATER))
                 motionAdjustment = 0.8F;

            motionX *= motionAdjustment;
            motionY *= motionAdjustment;
            motionZ *= motionAdjustment;
            motionY -= gravity;
            if (lines.isEnabled())
            	renderer.pos(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ).endVertex();
    	}
    	 if (lines.isEnabled())
    		 tessellator.draw();
    	if (landing.isEnabled()) {
	        GlStateManager.pushMatrix();
	        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
	        if (collision != null) {
	            switch (collision.sideHit.getIndex()) {
	                case 2:
	                	GlStateManager.rotate(90, 1, 0, 0);
	                    break;
	                case 3:
	                	GlStateManager.rotate(90, 1, 0, 0);
	                    break;
	                case 4:
	                	GlStateManager.rotate(90, 0, 0, 1);
	                    break;
	                case 5:
	                	GlStateManager.rotate(90, 0, 0, 1);
	                    break;
	                default:
	                    break;
	            }
	             if (isEntity)
	             	GLManager.glColor(1, 0, 0, 1F);
	        }
	        renderPoint();
	        GlStateManager.popMatrix();
    	}
	}
	
	/**
	 * Finds an item, checking both hands
	 *
	 * todo - maybe have a check if the player has arrows? if no arrows, ignore bow
	 */
	private ItemStack getApplicableItem() {
		if (mc.player.getActiveItemStack() != null && isThrowable(mc.player.getActiveItemStack().getItem())) return mc.player.getActiveItemStack();
		ItemStack main = mc.player.getHeldItem(EnumHand.MAIN_HAND);
		ItemStack off = mc.player.getHeldItem(EnumHand.OFF_HAND);
		if (main != null && isThrowable(main.getItem())) return main;
		if (off != null && isThrowable(off.getItem())) return off;
		return null;
	}
	
	private boolean isThrowable(Item item) {
		return item instanceof ItemBow || item instanceof ItemSnowball || item instanceof ItemEnderPearl || item instanceof ItemEgg || item instanceof ItemSplashPotion;
	}
	
	private float getMult(int mode) {
		return mode == 2 ? 0.5F : 1.5F;
    }

    private float getGravity(int mode) {
    	return mode >= 1 ? 0.05F : 0.03F;
    }

	private void renderPoint() {
		Tessellator tessellator = Tessellator.getInstance();
    	VertexBuffer renderer = tessellator.getBuffer();
    	renderer.begin(GL_LINES, DefaultVertexFormats.POSITION);
    	renderer.pos(-landingSize.getValue(), 0, 0).endVertex();
    	renderer.pos(0, 0, 0).endVertex();
    	renderer.pos(0, 0, -landingSize.getValue()).endVertex();
    	renderer.pos(0, 0, 0).endVertex();
    	
    	renderer.pos(landingSize.getValue(), 0, 0).endVertex();
    	renderer.pos(0, 0, 0).endVertex();
    	renderer.pos(0, 0, landingSize.getValue()).endVertex();
    	renderer.pos(0, 0, 0).endVertex();
    	tessellator.draw();

    	GlStateManager.rotate(-90, 1, 0, 0);
        cylinder.setDrawStyle(GLU.GLU_LINE);
        cylinder.draw(landingSize.getValue(), landingSize.getValue(), 0.1f, 24, 1);
    }
	
	private boolean isInMaterial(AxisAlignedBB axisalignedBB, Material material) {
        int chunkMinX = MathHelper.floor(axisalignedBB.minX);
        int chunkMaxX = MathHelper.floor(axisalignedBB.maxX + 1.0D);
        int chunkMinY = MathHelper.floor(axisalignedBB.minY);
        int chunkMaxY = MathHelper.floor(axisalignedBB.maxY + 1.0D);
        int chunkMinZ = MathHelper.floor(axisalignedBB.minZ);
        int chunkMaxZ = MathHelper.floor(axisalignedBB.maxZ + 1.0D);

		StructureBoundingBox structureBoundingBox = new StructureBoundingBox(chunkMinX, chunkMinY, chunkMinZ, chunkMaxX, chunkMaxY, chunkMaxZ);
        if (!mc.world.isAreaLoaded(structureBoundingBox)) {
            return false;
        } else {
            boolean isWithin = false;
            for (int x = chunkMinX; x < chunkMaxX; ++x) {
                for (int y = chunkMinY; y < chunkMaxY; ++y) {
                    for (int z = chunkMinZ; z < chunkMaxZ; ++z) {
						IBlockState blockState = mc.world.getBlockState(new BlockPos(x, y, z));
						Block block = blockState.getBlock();
                        if (block != null && blockState.getMaterial() == material) {
                            double liquidHeight = (double) ((float) (y + 1) - BlockLiquid.getLiquidHeightPercent((Integer)blockState.getValue(BlockLiquid.LEVEL)));
                            if ((double) chunkMaxY >= liquidHeight)
                                isWithin = true;
                        }
                    }
                }
            }
            return isWithin;
        }
    }
}
