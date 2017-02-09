package net.halalaboos.huzuni.mod.movement;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.PlaceTask;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Places blocks underneath the player to allow them to walk across large areas which have no blocks.
 * */
public class Scaffold extends BasicMod {
		
	public final Value placeDistance = new Value("Distance ", "", 1F, 3F, 4F, 1F, "Max distance you place blocks");

	public final Mode<String> mode = new Mode<String>("Mode", "Movement mode", "Horizontal", "Vertical");
	
	private final PlaceTask placeTask = new PlaceTask(this);
	
	private int count = 0;
	
	public Scaffold() {
		super("Scaffold", "Automatically places blocks when you move forward or jump.");
		addChildren(placeDistance, mode);
		setCategory(Category.MOVEMENT);
		huzuni.lookManager.registerTaskHolder(this);
		placeTask.setPlaceDelay(0);
		placeTask.setNaturalPlacement(false);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.lookManager.withdrawTask(placeTask);
	}

	@Override
	public String getDisplayNameForRender() {
		return settings.getDisplayName() + String.format(" (%s)", mode.getSelectedItem());
	}
	
	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (huzuni.lookManager.hasPriority(this) && count <= 0) {
			if (event.type == Type.PRE) {
				switch (mode.getSelected()) {
				case 0:
					if (!mc.player.movementInput.sneak && (mc.player.movementInput.moveForward != 0 || mc.player.movementInput.moveStrafe != 0)) {
						EnumFacing face = mc.player.getHorizontalFacing();
						BlockPos position = getFirstBlock(face.getDirectionVec());
						if (position != null) {
							placeTask.setBlock(position, face);
							huzuni.lookManager.requestTask(this, placeTask);
						} else
							huzuni.lookManager.withdrawTask(placeTask);
					} else
						huzuni.lookManager.withdrawTask(placeTask);
					break;
				case 1:
					if (!mc.player.onGround && mc.player.movementInput.jump && mc.player.motionY > 0) {
						BlockPos position = new BlockPos(mc.player.posX, Math.floor(mc.player.posY - 2), mc.player.posZ);
						BlockPos above = new BlockPos(mc.player.posX, Math.floor(mc.player.posY - 1), mc.player.posZ);
						if (mc.world.getBlockState(position).getBlock() != Blocks.AIR && mc.world.getBlockState(above).getBlock() == Blocks.AIR) {
							placeTask.setBlock(position, EnumFacing.UP);
							huzuni.lookManager.requestTask(this, placeTask);
						} else
							huzuni.lookManager.withdrawTask(placeTask);
					} else 
						huzuni.lookManager.withdrawTask(placeTask);
					break;
				default:
					break;
				}
			}
		} else {
			if (count > 0)
				count--;
		}
	}
	
	private BlockPos getFirstBlock(Vec3i direction) {
		for (int i = 0; i <= (int) placeDistance.getValue(); i++) {
			BlockPos position = new BlockPos(mc.player.posX + direction.getX() * i, mc.player.posY - 1, mc.player.posZ + direction.getZ() * i);
			BlockPos before = new BlockPos(mc.player.posX + direction.getX() * (i - 1), mc.player.posY - 1, mc.player.posZ + direction.getZ() * (i - 1));
			if (mc.world.getBlockState(position).getBlock() == Blocks.AIR && mc.world.getBlockState(before).getBlock() != Blocks.AIR) {
				return before;
			}
		}
		return null;
	}

}
