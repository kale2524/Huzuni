package net.halalaboos.huzuni.mod.combat;

import java.util.Random;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.event.EventMouseClick;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.LookTask;
import net.halalaboos.huzuni.api.util.EntityTracker;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.MinecraftUtils;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.api.util.render.RenderUtils;
import net.halalaboos.huzuni.api.util.render.Texture;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHand;

/**
 * Automatically attacks entities after passing a series of tests.
 * */
public class Killaura extends BasicMod implements Renderer {
	
	private final Texture select = new Texture("huzuni/select.png");
	
	public final Toggleable players = new Toggleable("Players", "Attack players");

	public final Toggleable mobs = new Toggleable("Mobs", "Attack mobs");

	public final Toggleable animals = new Toggleable("Animals", "Attack animals");

	public final Toggleable invisibles = new Toggleable("Invisible", "Attack invisibles");

	public final Toggleable silent = new Toggleable("Silent", "The aimbot will be silent");

	public final Toggleable randomMisses = new Toggleable("Random misses", "Randomly misses the entity");

	public final Toggleable interact = new Toggleable("Interact", "Interacts with entities rather than hitting them");

	public final Toggleable smartAttack = new Toggleable("Smart attack", "Attacks entities when the attack power exceeds or reaches their health");

	public final Toggleable selection = new Toggleable("Entity selection", "Select specific entities to attack");

	public final Toggleable checkAge = new Toggleable("Check age", "Check the age of the entity before attacking");

	public final Value strength = new Value("Strength", "", 0F, 10F, 10F, "Attack strength");

	public final Value speed = new Value("Speed", "", 1F, 8F, 15F, "Attack speed (in hits per second)");

	public final Value reach = new Value("Reach", " blocks", 3F, 3.8F, 6F, "Attack reach");

	public final Value fov = new Value("FOV", "", 10F, 60F, 180F, 1F, "FOV you will attack entities inside of");

	public final Value rotationRate = new Value("Rotation rate", "", 2F, 10F, 180F, 1F, "Maximum rate the rotation will be updated (the smaller, the smoother)");

	public final Value speedRandomization = new Value("Speed randomization", "", 0F, 0F, 4F, "Amount you want to randomize your attack speed (set to 0 for none)");

	public final Value strengthRandomization = new Value("Strength randomization", "", 0F, 0F, 4F, "Amount you want to randomize your attack strength (set to 0 for none)");

	public final Mode<String> priority = new Mode<String>("Attack Priority", "Determines which entity will be selected for attack", "Closest to crosshair", "Closest to player", "Triggerbot");
		
	private final LookTask lookTask = new LookTask(this);
	
	private final EntityTracker tracker = new EntityTracker();
	
	private final Timer timer = new Timer(), clickTimer = new Timer();
	
	private final Random random = new Random();
	
	private EntityLivingBase entity, selectedEntity, pickedEntity;
		
	private float randomizedSpeed = 0F, randomizedStrength = 0F, missChance = 0.5F;
	
	private String hitOrMiss = null;
	
	private int clicks = 0;
	
	public Killaura() {
		super("Kill aura", "Attack entities surrounding the player", Keyboard.KEY_R);
		this.addChildren(players, mobs, animals, invisibles, silent, randomMisses, interact, smartAttack, selection, checkAge, priority, strength, speed, reach, fov, rotationRate, speedRandomization, strengthRandomization);
		silent.setEnabled(true);
		players.setEnabled(true);
		mobs.setEnabled(true);
		animals.setEnabled(true);
		smartAttack.setEnabled(true);
		checkAge.setEnabled(true);
		this.setCategory(Category.COMBAT);
		huzuni.lookManager.registerTaskHolder(this);
	}

	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
		huzuni.renderManager.addWorldRenderer(this);
		calculateRandomization();
		if (selection.isEnabled()) {
			huzuni.addNotification(NotificationType.INFO, this, 5000, "Double right-click to select entities for specified attack!");
		}
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.renderManager.removeWorldRenderer(this);
		huzuni.lookManager.withdrawTask(lookTask);
		tracker.reset();
		tracker.setEntity(null);
		hitOrMiss = null;
		pickedEntity = null;
		selectedEntity = null;
		entity = null;
	}
	
	@EventMethod
	public void onUpdate(EventUpdate event) {
		switch (event.type) {
		case PRE:
			if (!huzuni.lookManager.hasPriority(this))
				return;
			if (mc.player.isSpectator() || mc.player.isDead) {
				tracker.reset();
				tracker.setEntity(null);
				huzuni.lookManager.withdrawTask(lookTask);
				return;
			}
			if (priority.getSelected() == 0) {
				entity = MinecraftUtils.getClosestEntity(reach.getValue(), 2.5F, invisibles.isEnabled(), mobs.isEnabled(), animals.isEnabled(), players.isEnabled(), checkAge.isEnabled());
			} else if (priority.getSelected() == 1) {
				entity = MinecraftUtils.getClosestEntityToPlayer(mc.player, reach.getValue(), 2.5F, invisibles.isEnabled(), mobs.isEnabled(), animals.isEnabled(), players.isEnabled(), checkAge.isEnabled());
			} else if (priority.getSelected() == 2) {
				this.pickAndVerifyEntity();
				this.triggerBot();
				huzuni.lookManager.withdrawTask(lookTask);
				return;
			}
			this.pickAndVerifyEntity();
			if (entity != null && MinecraftUtils.isWithinFOV(entity, fov.getValue())) {
				tracker.setRotationRate(rotationRate.getValue());
				tracker.setEntity(entity);
				tracker.updateRotations();
				lookTask.setRotations(tracker.getYaw(), tracker.getPitch());
	        	lookTask.setReset(silent.isEnabled());
	        	huzuni.lookManager.requestTask(this, lookTask);
			} else {
				tracker.reset();
				tracker.setEntity(null);
				huzuni.lookManager.withdrawTask(lookTask);
			}
			break;
		case POST:
			if (lookTask.isRunning()) {
				if (tracker.hasReached()) {
					if (entity != null && MinecraftUtils.isWithinDistance(entity, reach.getValue())) {
						attackEntity();
					}
				}
			}
			break;
		}
	}

	/**
     * Finds an entity within the FOV of 10 (10 degrees in both directions from the player's cursor) and assigns the entity to the selected entity if possible.
     * */
	private void pickAndVerifyEntity() {
		if (selection.isEnabled()) {
			if (hasSelectedEntity()) {
				if (!MinecraftUtils.isAliveNotUs(selectedEntity)) {
					onSelectedRemoved(selectedEntity);
					selectedEntity = null;
				} else if (MinecraftUtils.isWithinDistance(selectedEntity, reach.getValue()))
					entity = selectedEntity;
				else
					entity = null;
			} else
				pickedEntity = MinecraftUtils.getEntityWithinFOV(10, invisibles.isEnabled(), mobs.isEnabled(), animals.isEnabled(), players.isEnabled(), checkAge.isEnabled());
		}
	}

	/**
     * Attacks the facing or selected entity.
     * */
	private void triggerBot() {
		if (mc.objectMouseOver != null) {
			Entity entity = mc.objectMouseOver.entityHit;
			if (entity instanceof EntityLivingBase && MinecraftUtils.checkType(entity, invisibles.isEnabled(), mobs.isEnabled(), animals.isEnabled(), players.isEnabled()) && MinecraftUtils.isWithinDistance((EntityLivingBase) entity, reach.getValue()) && MinecraftUtils.checkTeam((EntityLivingBase) entity) && !huzuni.friendManager.isFriend(entity.getName())) {
				this.entity = (EntityLivingBase) entity;
				if (selection.isEnabled() && hasSelectedEntity()) {
					if (isSelectedEntity(this.entity))
						attackEntity();
					else
						this.entity = null;
				} else
					attackEntity();
			} else
				this.entity = null;
		} else
			this.entity = null;
	}
	
	@Override
	public String getDisplayNameForRender() {
		return settings.getDisplayName() + (hitOrMiss == null ? "" : " (" + hitOrMiss + ")");
	}

	/**
     * @return The attack speed with randomization applied.
     * */
	private float getAttackSpeed() {
		float randomizationFactor = (speedRandomization.getValue() == 0 ? 0 : (randomizedSpeed * speedRandomization.getValue()));
		return this.speed.getValue() - randomizationFactor;
	}

	/**
     * @return The attack strength with randomization applied.
     * */
	private float getAttackStrength() {
		float randomizationFactor =  (strengthRandomization.getValue() == 0 ? 0 : (randomizedStrength * strengthRandomization.getValue()));
		return ((this.strength.getValue() - randomizationFactor) / 10F);
	}

	/**
     * Creates a new randomization value for the strength and speed randomization factors.
     * */
	private void calculateRandomization() {
		randomizedSpeed = random.nextFloat();
        randomizedStrength = random.nextFloat();
	}

	private void attackEntity() {
		float cooldown = mc.player.getCooledAttackStrength(0.0F);
		if (timer.hasReach((int) (1000F / (getAttackSpeed()))) && (cooldown >= getAttackStrength() || calculateSmartAttack())) {
			if (this.randomMisses.isEnabled()) {
				if (random.nextFloat() > missChance) {
					if (interact.isEnabled())
						mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
					else
						mc.playerController.attackEntity(mc.player, entity);
                    calculateRandomization();
					hitOrMiss = "Hit";
				} else
					hitOrMiss = "Miss";
			} else {
				if (interact.isEnabled())
					mc.playerController.interactWithEntity(mc.player, entity, EnumHand.MAIN_HAND);
				else
					mc.playerController.attackEntity(mc.player, entity);
                calculateRandomization();
			}
			mc.player.swingArm(EnumHand.MAIN_HAND);
			timer.reset();
		}
	}

	/**
     * Determines if the entity currently selected can be killed using the damage the player can do.
     * */
	private boolean calculateSmartAttack() {
		if (this.smartAttack.isEnabled()) {
			float playerDamage = MinecraftUtils.calculatePlayerDamage(entity, mc.player.getHeldItemMainhand());
			if (playerDamage >= entity.getHealth())
				return true;
		}
		return false;
	}


	@Override
	public void render(float partialTicks) {
		if (selection.isEnabled()) {
			if (entity != null && isSelectedEntity(entity))
				renderEntitySelection(partialTicks, selectedEntity, 1F, 0F, 0F);
			else if (hasSelectedEntity())
				renderEntitySelection(partialTicks, selectedEntity, 0F, 1F, 0F);
			else if (pickedEntity != null)
				renderEntitySelection(partialTicks, pickedEntity, 1F, 1F, 0F);
		}
	}

	/**
     * Renders the entity selection texture.
     * */
	private void renderEntitySelection(float partialTicks, EntityLivingBase entity, float r, float g, float b) {
		GlStateManager.pushMatrix();
		float renderX = (float) (MathUtils.interpolate(entity.prevPosX, entity.posX, partialTicks) - mc.getRenderManager().viewerPosX);
		float renderY = (float) (MathUtils.interpolate(entity.prevPosY, entity.posY, partialTicks) - mc.getRenderManager().viewerPosY);
		float renderZ = (float) (MathUtils.interpolate(entity.prevPosZ, entity.posZ, partialTicks) - mc.getRenderManager().viewerPosZ);
		RenderUtils.prepareBillboarding(renderX, renderY + (entity.height / 2F), renderZ, false);
		GLManager.glColor(r, g, b, 1F);
		select.bindTexture();
		select.render(-16F, -16F, 32, 32);
		GlStateManager.disableTexture2D();
		GlStateManager.popMatrix();
	}
	
	@EventMethod
	public void onMouseClick(EventMouseClick event) {
		if (selection.isEnabled() && event.buttonId == 1) {
			if (clickTimer.hasReach(500)) {
				clicks = 1;
			}  else {
				clicks++;
				if (clicks >= 2) {
					if (selectedEntity != null) {
						onSelectedRemoved(selectedEntity);
						selectedEntity = null;
						entity = null;
					} else {
						if (pickedEntity != null) {
							selectedEntity = pickedEntity;
							onSelectedAdded(selectedEntity);
						}
					}
					clicks = 0;
				}
			}
			clickTimer.reset();
		}
	}

	/**
     * @return True if the player has a selected entity.
     * */
	private boolean hasSelectedEntity() {
		return selectedEntity != null;
	}

	/**
     * @return True if the entity is the selected entity.
     * */
	private boolean isSelectedEntity(EntityLivingBase entity) {
		return selectedEntity == entity;
	}

	/**
     * Invoked when the entity is no longer selected.
     * */
	private void onSelectedRemoved(EntityLivingBase entity) {
		if (entity != null)
			huzuni.addNotification(NotificationType.ERROR, this, 5000, entity.getName() + " is no longer selected!");
	}

    /***
     * Invoked when the entity is selected.
     */
	private void onSelectedAdded(EntityLivingBase entity) {
		huzuni.addNotification(NotificationType.CONFIRM, this, 5000, entity.getName() + " is now selected!");
	}

}
