package net.halalaboos.huzuni.mod.mining;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.MineTask;
import net.halalaboos.huzuni.api.util.BlockLocator;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Breaks all one-hit blocks.
 * */
public class Smasher extends BasicMod {
	
	public final Value radius = new Value("Radius", " blocks", 1F, 5F, 5F, 1F, "Radius around the player to mine blocks");

    public final Value mineDelay = new Value("Mine delay", " ms", 0F, 100F, 500F, 5F, "Delay in MS between mining of each block");

    public final Toggleable silent = new Toggleable("Silent", "Adjust player rotation server-sided only");

	private final MineTask mineTask = new MineTask(this);
	
	private final BlockLocator blockLocator = new BlockLocator() {

		@Override
		protected boolean isValidBlock(BlockPos position) {
			IBlockState blockState = mc.world.getBlockState(position);
			return blockState.getBlock() != Blocks.AIR && blockState.getPlayerRelativeBlockHardness(mc.player, mc.world, position) >= 1F && MathUtils.getDistance(position) < mc.playerController.getBlockReachDistance();
		}

		@Override
		protected EnumFacing getFace(BlockPos position) {
			return EnumFacing.UP;
		}
		
	};
	
	public Smasher() {
		super("Smasher", "Annihilates one-hit blocks within a radius surrounding the player");
		this.addChildren(radius, mineDelay, silent);
		this.setCategory(Category.MINING);
		silent.setEnabled(true);
		huzuni.lookManager.registerTaskHolder(this);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.lookManager.withdrawTask(mineTask);
		blockLocator.reset();
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (huzuni.lookManager.hasPriority(this) && event.type == Type.PRE) {
			mineTask.setReset(silent.isEnabled());
			mineTask.setMineDelay((int) mineDelay.getValue());
			if (mineTask.hasBlock())
				huzuni.lookManager.requestTask(this, mineTask);
			else {
				huzuni.lookManager.withdrawTask(mineTask);
				findBlock();
			}
		}
	}

	/**
     * Uses the block locator to find a block and update the mine task.
     * */
	private void findBlock() {
		if (blockLocator.locateClosest(this.radius.getValue(), true)) {
			mineTask.setBlock(blockLocator.getPosition(), blockLocator.getFace());
			huzuni.lookManager.requestTask(this, mineTask);
		}
	}
	
}
