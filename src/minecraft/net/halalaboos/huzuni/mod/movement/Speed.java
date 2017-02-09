package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.settings.Nameable;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventPlayerMove;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Allows the player to move at a faster rate.
 * */
public class Speed extends BasicMod {
		
	public final Mode<SpeedMode> mode = new Mode<SpeedMode>("Mode", "Speed mode", new NoneMode(), new SprintMode());

    public final Toggleable bunnyHop = new Toggleable("Bunny hop", "Hops like a bunny");

    public final Toggleable stairs = new Toggleable("Stairs", "Automagically jumps up stairs");

    public final Value groundSpeed = new Value("Ground speed", "", 1F, 1F, 10F, "Movement speed on the ground");

    public final Value airSpeed = new Value("Air speed", "", 1F, 1F, 10F, "Movement speed in air");
	
	public Speed() {
		super("Speed", "Adjust player movement speed", Keyboard.KEY_M);
		setCategory(Category.MOVEMENT);
		addChildren(bunnyHop, stairs, mode, groundSpeed, airSpeed);
		mode.setSelectedItem(1);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		if (mc.player != null)
			mc.player.setSprinting(false);
	}
	
	@Override
	public String getDisplayNameForRender() {
		return settings.getDisplayName() + String.format(" (%s)", mode.getSelectedItem());
	}
	
	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (event.type == Type.PRE) {
			boolean modifyMovement = shouldModifyMovement();
			mode.getSelectedItem().onUpdate(this, mc, event);
			if (modifyMovement && mc.player.onGround) {
				if ((stairs.isEnabled() && isUnderStairs()) || bunnyHop.isEnabled()) {
					mc.player.jump();
				}
			}
		}
	}
	
	@EventMethod
	public void onPlayerMove(EventPlayerMove event) {
		event.setMotionX(event.getMotionX() * (mc.player.onGround ? groundSpeed.getValue() : airSpeed.getValue()));
		event.setMotionZ(event.getMotionZ() * (mc.player.onGround ? groundSpeed.getValue() : airSpeed.getValue()));
        mode.getSelectedItem().onPlayerMove(this, mc, event);
	}

	/**
     * @return True if the player is underneath stairs.
     * */
	private boolean isUnderStairs() {
		EnumFacing face = mc.player.getHorizontalFacing();
		IBlockState blockState = mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ));
		IBlockState nextBlockState = mc.world.getBlockState(new BlockPos(mc.player.posX + face.getDirectionVec().getX(), mc.player.posY, mc.player.posZ + face.getDirectionVec().getZ()));

		if (BlockStairs.isBlockStairs(blockState) && BlockStairs.isBlockStairs(nextBlockState)) {
			EnumFacing blockFace = blockState.getValue(BlockStairs.FACING);
			EnumFacing nextBlockFace = blockState.getValue(BlockStairs.FACING);
			return face.equals(blockFace) && face.equals(nextBlockFace);
		} else
			return false;
	}

	/**
     * @return True if the player's given circumstances are ideal for modifying movement.
     * */
	public boolean shouldModifyMovement() {
        return !mc.player.isSwingInProgress && !mc.player.capabilities.isFlying && mc.player.moveForward > 0 && !mc.player.isSneaking() && !mc.player.isCollidedHorizontally && mc.player.getFoodStats().getFoodLevel() > 6 && !mc.player.isInWater() && !mc.player.isInLava();
    }

    /**
     * The sprint mode forces the player to sprint when ideal.
     * */
    public static class SprintMode extends SpeedMode {

        public SprintMode() {
            super("Sprint", "Forces the player to sprint.");
        }

        @Override
        public void onUpdate(Speed speed, Minecraft mc, EventUpdate event) {
            mc.player.setSprinting(speed.shouldModifyMovement());
        }

        @Override
        public void onPlayerMove(Speed speed, Minecraft mc, EventPlayerMove event) {

        }
    }

    /**
     * The default speed mode used, will apply no effects.
     * */
    public static class NoneMode extends SpeedMode {

        public NoneMode() {
            super("None", "An empty speed mode, using only the movement speed modifiers.");
        }

        @Override
        public void onUpdate(Speed speed, Minecraft mc, EventUpdate event) {

        }

        @Override
        public void onPlayerMove(Speed speed, Minecraft mc, EventPlayerMove event) {

        }
    }

    /**
     * Used within the mode of the speed mod. Allows for custom speed types.
     * */
	public static abstract class SpeedMode implements Nameable {

        private final String name, description;

        public SpeedMode(String name, String description) {
            this.name = name;
            this.description = description;
        }

        /**
         * Invoked before and after sending motion updates.
         * */
        public abstract void onUpdate(Speed speed, Minecraft mc, EventUpdate event);

        /**
         * Invoked when the player moves.
         * */
        public abstract void onPlayerMove(Speed speed, Minecraft mc, EventPlayerMove event);

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
