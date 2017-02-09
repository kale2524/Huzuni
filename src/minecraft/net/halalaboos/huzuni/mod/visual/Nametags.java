package net.halalaboos.huzuni.mod.visual;

import static org.lwjgl.opengl.GL11.glLineWidth;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

/**
 * Renders custom nametags along with extra information with the nametags.
 * */
public class Nametags extends BasicMod implements Renderer {

	public static final Nametags INSTANCE = new Nametags();

	public final Toggleable armor = new Toggleable("Armor", "Render player armor above their heads");

    public final Toggleable enchants = new Toggleable("Enchants", "Render player enchantments over their items");

    public final Toggleable ping = new Toggleable("Ping", "Render player ping above their heads");

    public final Toggleable invisibles = new Toggleable("Invisible", "Trace to invisible entities");

    public final Toggleable scale = new Toggleable("Scale", "Scale the nameplates as you are further from the player");

    public final Value opacity = new Value("Opacity", "%", 0F, 30F, 100F, 1F, "Opacity of the name plate");

    public final Value scaleValue = new Value("Scale Amount", "%", 1F, 2F, 3F, "Amount the name plates will be scaled");

    public final Mode<String> healthMode = new Mode<String>("Health", "Style the health will be rendered", "None", "Hearts", "Numerical", "Percentage");
	
	private Nametags() {
		super("Nametags", "Render custom nameplates over entities", Keyboard.KEY_P);
		this.setCategory(Category.VISUAL);
		addChildren(armor, enchants, ping, invisibles, scale, healthMode, scaleValue, opacity);
		this.settings.setDisplayable(false);
		armor.setEnabled(true);
		enchants.setEnabled(true);
		scale.setEnabled(true);
		healthMode.setSelectedItem(3);
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
		for (EntityPlayer entityPlayer : mc.world.playerEntities) {
			float renderX = (float) (MathUtils.interpolate(entityPlayer.prevPosX, entityPlayer.posX, partialTicks) - mc.getRenderManager().viewerPosX);
			float renderY = (float) (MathUtils.interpolate(entityPlayer.prevPosY, entityPlayer.posY, partialTicks) - mc.getRenderManager().viewerPosY);
			float renderZ = (float) (MathUtils.interpolate(entityPlayer.prevPosZ, entityPlayer.posZ, partialTicks) - mc.getRenderManager().viewerPosZ);
			renderNameplate(entityPlayer, renderX, renderY, renderZ, partialTicks);	
		}
		glLineWidth(huzuni.settings.lineSize.getValue());
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
	}
	
	private void renderNameplate(EntityPlayer entity, double x, double y, double z, double delta) {
		if (!Minecraft.isGuiEnabled() || entity == mc.getRenderManager().renderViewEntity || (!invisibles.isEnabled() && entity.isInvisible()))
			return;
		int color = getColor(entity, mc.player.getDistanceToEntity(entity), huzuni.friendManager.isFriend(entity.getName()), entity.isSneaking());
		double scale = (MathUtils.getInterpolatedDistance(entity, delta) / 8D) / (1.5F + (2F - scaleValue.getValue()));
		if (scale < 1D || !this.scale.isEnabled())
			scale = 1D;
		
		String text = (huzuni.friendManager.isFriend(entity.getName()) ? huzuni.friendManager.getAlias(entity.getName()) : entity.getDisplayName().getFormattedText()) + getHealth(entity) + (entity.isInvisibleToPlayer(mc.player) ? TextFormatting.BLUE + " [Invisible]" : "") + getPing(entity);
        int width = mc.fontRenderer.getStringWidth(text);
		GlStateManager.pushMatrix();
		RenderUtils.prepareBillboarding((float) x, (float) y + entity.height + 0.5F, (float) z, true);
		GlStateManager.scale(scale, scale, scale);
		if (this.scale.isEnabled())
			GlStateManager.translate(0F, -(scale), 0F);
        GLManager.glColor(0F, 0F, 0F, (25F + opacity.getValue()) / 100F);
		RenderUtils.drawBorderRect(-width / 2 - 2, -2, width / 2 + 2, 10, 2F);
		GLManager.glColor(1F, 1F, 1F, 1F);
		
        if (healthMode.getSelected() == 1)
			renderHealth(entity);
        
		mc.fontRenderer.drawStringWithShadow(text, -width / 2, 0, color);
		
		GL11.glPolygonOffset(1.0F, -2000000.0F);
		GlStateManager.enablePolygonOffset();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		if (armor.isEnabled()) 
        	renderItems(entity, healthMode.getSelected() == 1 ? -12 : -4);
		GL11.glPolygonOffset(1.0F, 2000000.0F);
		GlStateManager.disablePolygonOffset();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.popMatrix();
	}
	
	private String getHealth(EntityPlayer entity) {
		float healthPercentage = entity.getHealth() / entity.getMaxHealth();
		TextFormatting healthFormat = getFormatted(healthPercentage > 0.5 && healthPercentage < 0.75, healthPercentage > 0.25 && healthPercentage <= 0.5, healthPercentage <= 0.25);
		return healthMode.getSelected() == 2 ? " " + healthFormat + String.format("%.2f", entity.getHealth()) : healthMode.getSelected() == 3 ? " " + healthFormat + (int) (healthPercentage * 100) + "%" : "";
	}
	
	private String getPing(EntityPlayer entity) {
		NetworkPlayerInfo playerInfo = mc.getConnection().getPlayerInfo(entity.getUniqueID());
		if (playerInfo != null) {
			int ping = playerInfo.getResponseTime();
			TextFormatting pingFormat = getFormatted(ping >= 100 && ping < 150, ping >= 150 && ping < 200, ping >= 200);
			return (this.ping.isEnabled() ? " " + pingFormat + ping + "ms" : "");
		} else
			return "";
	}
	
	private int getColor(EntityPlayer entity, float distance, boolean friend, boolean sneaking) {
		if (friend)
			return huzuni.friendManager.getColor().getRGB();
		else {
			if (huzuni.settings.team.isEnabled()) {
				if (huzuni.settings.team.isTeam(entity))
					return huzuni.settings.team.getColor();
				else {
					int teamColor = huzuni.settings.team.getTeamColor(entity);
					if (teamColor != -1)
						return teamColor;
				}
			}
			return sneaking ? 0xFF0000 : 0xFFFFFF;
		}
	}
	
	private TextFormatting getFormatted(boolean yellow, boolean gold, boolean red) {
		if (yellow)
			return TextFormatting.YELLOW;
		else if (gold)
			return TextFormatting.GOLD;
		else if (red)
			return TextFormatting.RED;
		else
			return TextFormatting.GREEN;
	}
	
	private void draw3dItem(ItemStack itemStack, int x, int y, float delta) {
		if (itemStack == null)
			return;
		try {
        	mc.getRenderItem().zLevel = -150F;
			mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, x, y);
        	mc.getRenderItem().zLevel = 0F;
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}
	
	private void renderItems(EntityPlayer player, float rY) {
		int totalItems = 0;
		GlStateManager.pushMatrix();
		for (int i = 0; i < 4; i++)
			if (player.inventory.armorItemInSlot(i) != null)
				totalItems++;
		if (player.getHeldItem(EnumHand.MAIN_HAND) != null)
			totalItems++;
		int itemSize = 18, center = (-itemSize / 2), halfTotalSize = ((totalItems * itemSize) / 2 - itemSize) + (itemSize / 2), count = 0;
		if (player.getHeldItem(EnumHand.MAIN_HAND) != null) {
			draw3dItem(player.getHeldItem(EnumHand.MAIN_HAND), (center - halfTotalSize) + itemSize * count + 2, (int) rY - 16, 0);
			if (enchants.isEnabled())
				renderEnchantments(player.getHeldItem(EnumHand.MAIN_HAND), (center - halfTotalSize) + itemSize * count + 2, (int) rY - 16, 0.5F);
			count++;
		}
		for (int i = 4; i > 0; i--) {
			ItemStack armor = player.inventory.armorItemInSlot(i - 1);
			if (armor != null) {
				draw3dItem(armor, (center - halfTotalSize) + itemSize * count, (int) rY - 16, 0);
				if (enchants.isEnabled())
					renderEnchantments(armor, (center - halfTotalSize) + itemSize * count, (int) rY - 16, 0.5F);
				count++;
			}
		}
		GlStateManager.popMatrix();
	}
	
	private void renderEnchantments(ItemStack item, float x, float y, float scale) {
		float scaleInverse = 1F / scale, increment = 10F / scaleInverse;
		if (item.getEnchantmentTagList() != null) {
			NBTTagList enchantments = item.getEnchantmentTagList();
			for (int j = 0; j < enchantments.tagCount(); j++) {
				NBTTagCompound compound = enchantments.getCompoundTagAt(j);
				GlStateManager.pushMatrix();
				GlStateManager.scale(scale, scale, scale);
				if (compound != null && Enchantment.getEnchantmentByID(compound.getByte("id")) != null)
					mc.fontRenderer.drawStringWithShadow(Enchantment.getEnchantmentByID(compound.getByte("id")).getTranslatedName(compound.getByte("lvl")).substring(0, 4) + " " + compound.getByte("lvl"), x * scaleInverse, ((int) y + (increment * j)) * scaleInverse, 0xFFFFFF);
				GlStateManager.popMatrix();
			}
		}
	}
	
	private void renderHealth(EntityPlayer entity) {
		int originX = -40;
		int originY = -12;
		float maxHealth = (float) entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).getAttributeValue();
		int health = MathHelper.ceil(entity.getHealth());
		int absorptionAmount = MathHelper.ceil(entity.getAbsorptionAmount());
		int i2 = MathHelper.ceil((maxHealth + (float) absorptionAmount) / 2.0F / 10.0F);
		int j2 = Math.max(10 - (i2 - 2), 3);
		int i3 = absorptionAmount;
		int k3 = -1;
		boolean updateHealth = false;
		this.mc.getTextureManager().bindTexture(GuiIngame.ICONS);
		for (int i = MathHelper.ceil((maxHealth + (float) absorptionAmount) / 2.0F) - 1; i >= 0; --i) {
			int textureX = 16;
			if (entity.isPotionActive(MobEffects.POISON))
				textureX += 36;
			else if (entity.isPotionActive(MobEffects.WITHER))
				textureX += 72;
			
			int j4 = 0;
			if (updateHealth)
				j4 = 1;
			
			int k4 = MathHelper.ceil((float) (i + 1) / 10.0F) - 1;
			int x = originX + i % 10 * 8;
			int y = originY - k4 * j2;
			if (i3 <= 0 && i == k3) {
				y -= 2;
			}
			int textureY = 0;
			if (mc.world.getWorldInfo().isHardcoreModeEnabled())
				textureY = 5;
			mc.ingameGUI.drawTexturedModalRect(x, y, 16 + j4 * 9, 9 * textureY, 9, 9);
			if (i3 > 0) {
				if (i3 == absorptionAmount && absorptionAmount % 2 == 1) {
					mc.ingameGUI.drawTexturedModalRect(x, y, textureX + 153, 9 * textureY, 9, 9);
					--i3;
				} else {
					mc.ingameGUI.drawTexturedModalRect(x, y, textureX + 144, 9 * textureY, 9, 9);
					i3 -= 2;
				}
			} else {
				if (i * 2 + 1 < health)
					mc.ingameGUI.drawTexturedModalRect(x, y, textureX + 36, 9 * textureY, 9, 9);
				
				if (i * 2 + 1 == health)
					mc.ingameGUI.drawTexturedModalRect(x, y, textureX + 45, 9 * textureY, 9, 9);
			}
		}
	}
	
}