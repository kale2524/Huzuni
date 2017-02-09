package net.halalaboos.huzuni.api.util;

import java.net.Proxy;
import java.util.Collection;

import net.halalaboos.huzuni.Huzuni;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CombatRules;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Session;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import com.google.common.collect.Multimap;
import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

/**
 * Easy to use functions that calculate blah blah blah relating to Minecraft.
 * */
public final class MinecraftUtils {

	private static final Minecraft mc = Minecraft.getMinecraft();
	
	private static final Huzuni huzuni = Huzuni.INSTANCE;
	
	private MinecraftUtils() {
		
	}
	
	/**
	 * @return The address the player is currently connected to.
	 * */
	public static String getCurrentServer() {
		return mc.getCurrentServerData() == null ? "localhost" : mc.getCurrentServerData().serverIP;
	}
	
	/**
	 * Attempts to log into a Minecraft account using the username and password provided.
	 * @return a {@link Session} class with the account's new session.
	 * */
	public static Session loginToMinecraft(String username, String password) throws AuthenticationException {
		YggdrasilAuthenticationService authenticationService = new YggdrasilAuthenticationService( Proxy.NO_PROXY, "");
		YggdrasilUserAuthentication userAuthentication = (YggdrasilUserAuthentication) authenticationService .createUserAuthentication(Agent.MINECRAFT);
		userAuthentication.setUsername(username);
		userAuthentication.setPassword(password);
		userAuthentication.logIn();
		return new Session(userAuthentication.getSelectedProfile().getName(), userAuthentication.getSelectedProfile().getId().toString(), userAuthentication.getAuthenticatedToken(), "MOJANG" /* we mojang now ;)))*/);
	}
	
	/**
	 * @return True if the entity type is 
	 * */
	public static boolean checkType(Entity entity, boolean invisible, boolean mob, boolean animal, boolean player) {
		if (!entity.isEntityAlive())
			return false;
		if (entity.isInvisible() && !invisible)
			return false;
    	if (mob && entity instanceof IMob)
    		return true;
    	if (animal && entity instanceof IAnimals && !(entity instanceof IMob))
    		return true;
    	if (player && entity instanceof EntityPlayer)
    		return true;
    	return false;
    }
	
	/**
	 * @return True if the entity age is greater than or equal to 20
	 * */
	public static boolean checkAge(EntityLivingBase entity) {
		return checkAge(entity, 20);
	}
	
	/**
	 * @return True if the entity age is greater than or equal to the age specified
	 * */
	public static boolean checkAge(EntityLivingBase entity, float age) {
		return entity.ticksExisted >= age;
	}

	/**
	 * @return The closest entity to the player
	 * */
	public static EntityLivingBase getClosestEntityToPlayer(Entity toPlayer, float distance, float extender, boolean invisible, boolean mob, boolean animal, boolean player, boolean ageCheck) {
		EntityLivingBase currentEntity = null;
		for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
    		if (mc.world.loadedEntityList.get(i) instanceof EntityLivingBase) {
    			EntityLivingBase entity = (EntityLivingBase) mc.world.loadedEntityList.get(i);
    			if (isAliveNotUs(entity) && checkType(entity, invisible, mob, animal, player) && checkTeam(entity) && checkProperties(entity) && (ageCheck ? checkAge(entity) : true)) {
    				if (isFriend(entity))
    					continue;
    				if (currentEntity != null) {
    	                if (toPlayer.getDistanceToEntity(entity) < toPlayer.getDistanceToEntity(currentEntity))
        					currentEntity = entity;
    				} else {
    					if (toPlayer.getDistanceToEntity(entity) < distance + extender)
    						currentEntity = entity;
    				}
    			}
    		}
    	}
		return currentEntity;
	}
	
	/**
	 * @return The closest entity to the player that requires the least change in yaw and pitch
	 * */
	public static EntityLivingBase getClosestEntity(float distance, float extender, boolean invisible, boolean mob, boolean animal, boolean player, boolean ageCheck) {
		EntityLivingBase currentEntity = null;
		for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
    		if (mc.world.loadedEntityList.get(i) instanceof EntityLivingBase) {
    			EntityLivingBase entity = (EntityLivingBase) mc.world.loadedEntityList.get(i);
    			if (isAliveNotUs(entity) && checkType(entity, invisible, mob, animal, player) && checkTeam(entity) && checkProperties(entity) && (ageCheck ? checkAge(entity) : true)) {
    				if (isFriend(entity))
    					continue;
    				if (currentEntity != null) {
    	                if (isWithinDistance(entity, distance + extender) && isClosestToMouse(currentEntity, entity, distance, extender))
        					currentEntity = entity;
    				} else {
    					if (isWithinDistance(entity, distance + extender))
    						currentEntity = entity;
    				}
    			}
    		}
    	}
		return currentEntity;
	}
	
	/**
	 * @return The closest entity to our mouse that is within our FOV
	 * */
	public static EntityLivingBase getEntityWithinFOV(float fov, boolean invisible, boolean mob, boolean animal, boolean player, boolean ageCheck) {
		EntityLivingBase currentEntity = null;
		for (int i = 0; i < mc.world.loadedEntityList.size(); i++) {
    		if (mc.world.loadedEntityList.get(i) instanceof EntityLivingBase) {
    			EntityLivingBase entity = (EntityLivingBase) mc.world.loadedEntityList.get(i);
    			if (isAliveNotUs(entity) && checkType(entity, invisible, mob, animal, player) && checkTeam(entity) && checkProperties(entity) && (ageCheck ? checkAge(entity) : true)) {
    				if (isFriend(entity))
    					continue;
    				if (currentEntity != null) {
    	                if (isWithinFOV(entity, fov) && isClosestToMouse(currentEntity, entity, -1, 0))
        					currentEntity = entity;
    				} else {
    					if (isWithinFOV(entity, fov))
    						currentEntity = entity;
    				}
    			}
    		}
    	}
		return currentEntity;
	}

	/**
	 * @return True if the entity is another player and does not contain any properties
	 * */
	public static boolean checkProperties(EntityLivingBase entity) {
		return entity instanceof EntityPlayer ? ((EntityPlayer) entity).getGameProfile().getProperties().size() > 0 : true;
	}

	/**
	 * @return True if the entity is within the player's friends list
	 * */
	public static boolean isFriend(EntityLivingBase entity) {
		return entity instanceof EntityPlayer && huzuni.friendManager.isFriend(entity.getName());
	}
	
	/**
	 * @return True if the entity is another player and is on the player's team
	 * */
	public static boolean checkTeam(EntityLivingBase entity) {
		 if (!huzuni.settings.team.isEnabled() || !huzuni.settings.team.hasSelected())
			 return true;
		if (entity instanceof EntityPlayer)
			return !huzuni.settings.team.hasTeamColor(entity.getDisplayName().getFormattedText());
		else
			return true;
	}

	/**
     * @return The closest entity to your mouse
     */
	public static boolean isClosestToMouse(EntityLivingBase currentEntity, EntityLivingBase otherEntity, float distance, float extender) {

        // If we can't reach our current entity without the extender, but we CAN with our OTHER entity, return true.
        if (!isWithinDistance(currentEntity, distance) && isWithinDistance(otherEntity, distance))
            return true;

        float otherDist = getDistanceFromMouse(otherEntity),
        		currentDist = getDistanceFromMouse(currentEntity);
        return (otherDist < currentDist || currentDist == -1) && otherDist != -1;
    }
	
	/**
	 * @return True if the entity is within the distance specified to the player
	 * */
	public static boolean isWithinDistance(EntityLivingBase entity, float distance) {
        return distance == -1 ? true : mc.player.getDistanceToEntity(entity) < distance;
    }

	/**
	 * @return True if the entity is alive and not the player
	 * */
	public static boolean isAliveNotUs(EntityLivingBase entity) {
		if (entity.getName().equalsIgnoreCase(mc.player.getName()))
			return false;
		return (entity != null) && entity.isEntityAlive() && entity != mc.player;
	}
	
	/**
     * @return Distance the entity is from our mouse.
     */
	public static float getDistanceFromMouse(EntityLivingBase entity) {
        float[] neededRotations = getRotationsNeeded(entity);
        if (neededRotations != null) {
            float neededYaw = getYawDifference(mc.player.rotationYaw % 360F, neededRotations[0]), neededPitch = (mc.player.rotationPitch % 360F) - neededRotations[1];
			return MathHelper.sqrt(neededYaw * neededYaw + neededPitch * neededPitch);
        }
        return -1F;
    }
	
	/**
     * @return Rotations needed to face the position.
     */
	public static float[] getRotationsNeeded(double x, double y, double z) {
        double xSize = x - mc.player.posX;
        double ySize = y - (mc.player.posY + (double) mc.player.getEyeHeight());
        double zSize = z - mc.player.posZ;
        
        double theta = (double) MathHelper.sqrt(xSize * xSize + zSize * zSize);
        float yaw = (float) (Math.atan2(zSize, xSize) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(Math.atan2(ySize, theta) * 180.0D / Math.PI));
        return new float[] {
        		(mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw)) % 360F,
        		(mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)) % 360F,
        };
	}
	
	/**
     * @return Rotations needed to face the entity.
     */
	public static float[] getRotationsNeeded(EntityLivingBase entity) {
        if (entity == null)
            return null;
        return getRotationsNeeded(entity.posX, entity.posY + ((double) entity.getEyeHeight() / 2F), entity.posZ);
	}
	
	/**
     * @return Rotations needed to face the entity without constantly snapping the player's head.
     */
	public static float[] getRotationsNeededLenient(EntityLivingBase entity) {
        if (entity == null)
            return null;
        float yaw = mc.player.rotationYaw % 360F, pitch = mc.player.rotationPitch % 360F; /* Player's current yaw/pitch */
        float[] rotations = getRotationsNeeded(entity.posX, entity.posY + ((double) entity.getEyeHeight() / 2F), entity.posZ); /* Required yaw/pitch to be looking at center of the entity */
        float[] rotationCaps = getEntityCaps(entity);
        float yawDifference = getYawDifference(yaw, rotations[0]), pitchDifference = rotations[1] - pitch; /* Calculates the difference between player's yaw/pitch and the required yaw/pitch */
		
		if (yawDifference > rotationCaps[0] || yawDifference < -rotationCaps[0]) { /* If our yaw difference is outside of the maximum/minimum yaw required to be looking at an entity, let's change things up and move the yaw to the edge of the entity */
    		if (yawDifference < 0) {
    			yaw += yawDifference + rotationCaps[0];
    		} else if (yawDifference > 0) {
    			yaw += yawDifference - rotationCaps[0];
    		}
    	}
    	if (pitchDifference > rotationCaps[1] || pitchDifference < -rotationCaps[1]) {
    		if (pitchDifference < 0) {
    			pitch += pitchDifference + rotationCaps[1];
    		} else if (pitchDifference > 0) {
    			pitch += pitchDifference - rotationCaps[1];
    		}
    	}
    	return new float[] { yaw, pitch };
	}
	
	/**
	 * @return the yaw and pitch constraints needed to keep the player's head within an entity
	 * */
	public static float[] getEntityCaps(EntityLivingBase entity) {
    	return getEntityCaps(entity, 6.5F);
	}
	
	/**
	 * @return Maximum/minimum rotation leniency allowed to still be considered 'inside' of a given entity.
	 * */
	public static float[] getEntityCaps(EntityLivingBase entity, float distance) {
		float distanceRatio = distance / mc.player.getDistanceToEntity(entity); /* I honestly do not remember my logic behind this and I don't want to bring out a notebook and figure out why this works, but it seems to work */
        float entitySize = 5F; /* magic number */
    	return new float[] { distanceRatio * entity.width * entitySize, distanceRatio * entity.height * entitySize };
	}
	
	/**
	 * @return the difference between two yaw values
	 * */
	public static float getYawDifference(float currentYaw, float neededYaw) {
		float yawDifference = neededYaw - currentYaw;
		if (yawDifference > 180)
			yawDifference = -((360F - neededYaw) + currentYaw);
		else if (yawDifference < -180)
			yawDifference = ((360F - currentYaw) + neededYaw);
		
		return yawDifference;
	}

	/**
	 * Compares the needed yaw to the player's yaw and returns whether or not it is less than the fov.
	 * @return True if the entity is within the FOV specified.
	 * */
	public static boolean isWithinFOV(EntityLivingBase entity, float fov) {
        float[] rotations = getRotationsNeeded(entity);
        float yawDifference = getYawDifference(mc.player.rotationYaw % 360F, rotations[0]);
        return yawDifference < fov && yawDifference > -fov;
	}
	
	/**
	 * Raytraces to find a face on the block that can be seen by the player.
	 * */
	public static EnumFacing findFace(BlockPos position) {
		if (mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
			for (EnumFacing face : EnumFacing.values()) {
				Vec3d playerVec = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
				Vec3d blockVec = new Vec3d(position.getX() + 0.5F + (float) (face.getDirectionVec().getX()) / 2F, position.getY() + 0.5F + (float) (face.getDirectionVec().getY()) / 2F, position.getZ() + 0.5F + (float) (face.getDirectionVec().getZ()) / 2F);
				RayTraceResult raytraceResult = mc.world.rayTraceBlocks(playerVec, blockVec);
				if (raytraceResult == null || raytraceResult.typeOfHit == RayTraceResult.Type.MISS) {
					return face;
				}
			}
		}
		return null;
	}
	
	/**
	 * Finds the face of the first adjacent block that can be seen by the player.
	 * */
	public static EnumFacing getAdjacent(BlockPos position) {
		for (EnumFacing face : EnumFacing.values()) {
			BlockPos otherPosition = position.offset(face);
			if (mc.world.getBlockState(otherPosition).getBlock() != Blocks.AIR) {
				EnumFacing otherFace = face.getOpposite();
				Vec3d playerVec = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
				Vec3d blockVec = new Vec3d(otherPosition.getX() + 0.5F + (float) (otherFace.getDirectionVec().getX()) / 2F, otherPosition.getY() + 0.5F + (float) (otherFace.getDirectionVec().getY()) / 2F, otherPosition.getZ() + 0.5F + (float) (otherFace.getDirectionVec().getZ()) / 2F);
				RayTraceResult raytraceResult = mc.world.rayTraceBlocks(playerVec, blockVec);
				if (raytraceResult == null || raytraceResult.typeOfHit == RayTraceResult.Type.MISS) {
					return face;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return The potential damage done to the {@link EntityLivingBase} specified with the given {@link ItemStack}.
	 * */
	public static float calculatePlayerDamage(EntityLivingBase entity, ItemStack item) {
		return calculatePlayerDamage(entity, item, mc.player.getCooledAttackStrength(0.5F));
	}
	
	/**
	 * @return The potential damage done to the {@link EntityLivingBase} specified with the given {@link ItemStack}.
	 * */
	public static float calculatePlayerDamage(EntityLivingBase entity, ItemStack item, float cooldown) {
		float attackAttribute = (float) mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getBaseValue();
		if (item != null) {
			Multimap<String, AttributeModifier> attributes = item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
			// TODO: Fix this.
			/*Collection<AttributeModifier> attackModifier = attributes.get(SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName());
			for (AttributeModifier modifier : attackModifier) {
				attackAttribute += modifier.getAmount();
			}
			
			float enchantModifier = EnchantmentHelper.getModifierForCreature(item, entity.getCreatureAttribute());
			attackAttribute *= (0.2F + cooldown * cooldown * 0.8F);
			enchantModifier *= cooldown;
			if (attackAttribute > 0.0F || enchantModifier > 0.0F) {
	            boolean hasKnockback = false;
	            boolean hasCritical = false;
	            hasCritical = hasKnockback && mc.player.fallDistance > 0.0F && !mc.player.onGround && !mc.player.isOnLadder() && !mc.player.isInWater() && !mc.player.isPotionActive(MobEffects.BLINDNESS) && !mc.player.isRiding();
	            hasCritical = hasCritical && !mc.player.isSprinting();
	            if (hasCritical) {
	            	attackAttribute *= 1.5F;
	            }
	            attackAttribute += enchantModifier;
	            
	            attackAttribute = CombatRules.getDamageAfterMagicAbsorb(attackAttribute, (float) entity.getTotalArmorValue());
	            attackAttribute = Math.max(attackAttribute - entity.getAbsorptionAmount(), 0.0F);
	    		return attackAttribute;
			} else */
				return 0F;
		}
		return attackAttribute;
	}
	
	/**
	 * @return The potential damage done to the {@link EntityLivingBase} specified with the given {@link ItemStack} and take into consideration the attack speed.
	 * */
	public static float calculatePlayerDamageWithAttackSpeed(EntityLivingBase entity, ItemStack item) {
		float attackAttribute = calculatePlayerDamage(entity, item, 1.0F);
		if (item != null) {
			Multimap<String, AttributeModifier> attributes = item.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
			// TODO: Fix this.
			/*Collection<AttributeModifier> speedModifier = attributes.get(SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName());
			float speedAttribute = (float) mc.player.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
			for (AttributeModifier modifier : speedModifier) {
				speedAttribute += (float) modifier.getAmount();
				attackAttribute *= speedAttribute;
			}*/
		}
		return attackAttribute;
	}
	
}
