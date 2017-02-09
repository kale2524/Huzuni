package net.halalaboos.huzuni.api.task;

import net.halalaboos.huzuni.api.mod.Mod;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.Timer;
import net.halalaboos.huzuni.mod.movement.Freecam;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

/**
 * Look task which can take block data and simulate server-sided mining.
 * */
public class MineTask extends LookTask {
	
	protected final Timer timer = new Timer();
	
	protected EnumFacing face;
	
	protected BlockPos position;
		
	protected float curBlockDamage = 0F;
	
	protected boolean digging = false;
	
	protected int mineDelay = 100;
		
	public MineTask(Mod mod) {
		super(mod);
	}
	
	public MineTask(Mod mod, BlockPos position, EnumFacing face) {
		super(mod, position.getX(), position.getY(), position.getZ());
		this.position = position;
		this.face = face;
	}
	
	@Override
	public void onPreUpdate() {
		if (timer.hasReach(mineDelay) && hasBlock() && !Freecam.INSTANCE.isEnabled()) {
			this.setRotations(position, face);
			super.onPreUpdate();
		}
	}
	
	@Override
	public void onPostUpdate() {
		if (timer.hasReach(mineDelay) && hasBlock() && !Freecam.INSTANCE.isEnabled()) {
			super.onPostUpdate();
			if (!blockExists() || !isWithinDistance()) {
				if (digging) {
					sendPacket(1);
					onTaskFinishPremature(position, face);
				}
				reset();
				return;
			}
			IBlockState blockState = getBlockState();
			mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
			if (curBlockDamage <= 0F) {
				digging = true;
				sendPacket(0);
				if (mc.playerController.isInCreativeMode() || blockState.getPlayerRelativeBlockHardness(mc.player, mc.world, position) >= 1F) {
					mc.world.setBlockToAir(position);
					onTaskFinish(position, face);
					reset();
					return;
				}
			}
			curBlockDamage += blockState.getPlayerRelativeBlockHardness(mc.player, mc.world, this.position);
			mc.world.sendBlockBreakProgress(mc.player.getEntityId(), position, (int) (curBlockDamage * 10.0F) - 1);
			if (curBlockDamage >= 1F) {
				mc.world.setBlockToAir(position);
				sendPacket(2);
				onTaskFinish(position, face);
				reset();
			}
		}
	}
	
	@Override
	public void onTaskCancelled() {
		if (isMining()) {
			sendPacket(1);
			onTaskFinishPremature(position, face);
			reset();
		}
	}
	
	@Override
	public void setRunning(boolean running) {
		super.setRunning(running);
		if (!running && isMining()) {
			sendPacket(1);
			onTaskFinishPremature(position, face);
			reset();
		}
	}
	
	public int getMineDelay() {
		return mineDelay;
	}

	public void setMineDelay(int mineDelay) {
		this.mineDelay = mineDelay;
	}

	/**
	 * @return True if the block is within player reach distance.
	 * */
	public boolean isWithinDistance() {
		return MathUtils.getDistance(position) < mc.playerController.getBlockReachDistance();
	}

	/**
	 * @return True if the block is not air.
	 * */
	public boolean blockExists() {
		return mc.world.getBlockState(position).getMaterial() != Material.AIR;
	}
	
	public boolean isMining() {
		return digging;
	}

	/**
	 * Sends a mining packet based on the mode given.
	 * */
	private void sendPacket(int mode) {
		CPacketPlayerDigging.Action action = mode == 0 ? CPacketPlayerDigging.Action.START_DESTROY_BLOCK : (mode == 1 ? CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK : (mode == 2 ? CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK : null));
		mc.getConnection().sendPacket(new CPacketPlayerDigging(action, position, face));
	}
	
	private void onTaskFinishPremature(BlockPos position, EnumFacing face) {
		timer.reset();
	}

	private void onTaskFinish(BlockPos position, EnumFacing face) {
		timer.reset();
	}

	/**
	 * Cancels the mining simulation if the block is not air.
	 * */
	public void cancelMining() {
		if (hasBlock()) {
			if (isMining()) {
				sendPacket(1);
				onTaskFinishPremature(position, face);
			}
			reset();
		}
	}

	/**
	 * Sets the block position and face value.
	 * */
	public void setBlock(BlockPos position, EnumFacing face) {
		this.position = position;
		this.face = face;
		if (position != null && face != null)
			this.setRotations(position, face);
	}

	/**
     * @return True if the position and face values are present for this task.
     * */
	public boolean hasBlock() {
		return position != null && face != null;
	}

	/**
     * Resets the mining information.
     * */
	protected void reset() {
		setBlock(null, null);
		curBlockDamage = 0F;
		digging = false;
		timer.reset();
	}
	
	protected IBlockState getBlockState() {
		return mc.world.getBlockState(position);
	}
	
	protected boolean shouldRotate() {
		return isWithinDistance();
	}
	
}
