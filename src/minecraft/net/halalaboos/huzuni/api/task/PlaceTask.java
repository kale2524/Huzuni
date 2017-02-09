package net.halalaboos.huzuni.api.task;

import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.mod.movement.Freecam;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * Look task which simulates block placement server-sided.
 * */
public class PlaceTask extends LookTask {
	
	protected final Timer timer = new Timer();
	
	protected EnumFacing face;
	
	protected BlockPos position;
			
	protected int placeDelay = 100;
	
	protected boolean naturalPlacement = true;
			
	public PlaceTask(Mod mod) {
		super(mod);
	}
	
	public PlaceTask(Mod mod, BlockPos position, EnumFacing face) {
		super(mod, position.getX(), position.getY(), position.getZ());
		this.position = position;
		this.face = face;
	}
	
	@Override
	public void onPreUpdate() {
		if (timer.hasReach(placeDelay) && hasBlock() && shouldRotate() && !Freecam.INSTANCE.isEnabled()) {
			this.setRotations(position, face);
			if (shouldResetBlock()) {
				reset();
				return;
			}
			super.onPreUpdate();
		}
	}
	
	@Override
	public void onPostUpdate() {
		if (timer.hasReach(placeDelay) && hasBlock() && shouldRotate() && !Freecam.INSTANCE.isEnabled()) {
			super.onPostUpdate();
			if (isWithinDistance()) {
				mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
				if (naturalPlacement) {
					// TODO: Fix this.
					/*if (mc.playerController.processRightClickBlock(mc.player, mc.world, mc.player.inventory.getCurrentItem(), position, face, new Vec3d((float) face.getDirectionVec().getX() / 2F, (float) face.getDirectionVec().getY() / 2F, (float) face.getDirectionVec().getZ() / 2F), EnumHand.MAIN_HAND) != EnumActionResult.FAIL) {
						if (shouldResetBlock()) {
							reset();
						}
					}*/
				} else {
					mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(position, face, EnumHand.MAIN_HAND, (float) face.getDirectionVec().getX() / 2F, (float) face.getDirectionVec().getY() / 2F, (float) face.getDirectionVec().getZ() / 2F));					
				}
				timer.reset();
			}
		}
	}
	
	@Override
	public void onTaskCancelled() {
		reset();
	}
	
	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);
		if (!running)
			reset();
	}

	/**
	 * @return True if the item held is required to continue block placement.
	 * */
	protected boolean hasRequiredItem(ItemStack item) {
		return item.getItem() instanceof ItemBlock;
	}

	/**
	 * @return True if the player should face the position.
	 * */
	protected boolean shouldRotate() {
		return mc.player.getHeldItemMainhand() != null && hasRequiredItem(mc.player.getHeldItemMainhand()) && isWithinDistance();
	}
	
	protected void reset() {
		setBlock(null, null);
		timer.reset();
	}
	
	protected IBlockState getBlockState() {
		return mc.world.getBlockState(position);
	}
	
	public int getPlaceDelay() {
		return placeDelay;
	}

	public void setPlaceDelay(int placeDelay) {
		this.placeDelay = placeDelay;
	}

	/**
	 * @return True if the position is within the player's reach distance.
	 * */
	public boolean isWithinDistance() {
		return MathUtils.getDistance(position) < mc.playerController.getBlockReachDistance();
	}

	/**
	 * @return True if the block at our placement location is not air.
	 * */
	public boolean shouldResetBlock() {
		return mc.world.getBlockState(position.offset(face)).getMaterial() != Material.AIR;
	}
	
	public void cancelPlacing() {
		if (hasBlock()) {
			reset();
		}
	}
	
	public void setBlock(BlockPos position, EnumFacing face) {
		this.position = position;
		this.face = face;
		if (position != null && face != null)
			this.setRotations(position, face);
	}
	
	public boolean hasBlock() {
		return position != null && face != null;
	}

	public boolean isNaturalPlacement() {
		return naturalPlacement;
	}

	public void setNaturalPlacement(boolean naturalPlacement) {
		this.naturalPlacement = naturalPlacement;
	}
	
}
