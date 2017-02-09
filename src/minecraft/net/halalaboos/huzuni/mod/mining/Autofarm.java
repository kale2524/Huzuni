package net.halalaboos.huzuni.mod.mining;

import net.halalaboos.huzuni.api.event.EventLoadWorld;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.MineTask;
import net.halalaboos.huzuni.api.task.PlaceTask;
import net.halalaboos.huzuni.api.util.BlockLocator;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSeedFood;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

/**
 * Automatically performs farming tasks for the user.
 * */
public final class Autofarm extends BasicMod {
	
	public final Value radius = new Value("Radius", " blocks", 1F, 5F, 5F, 1F, "Radius around the player to mine blocks");

	public final Value mineDelay = new Value("Mine delay", " ms", 0F, 100F, 500F, 5F, "Delay in MS between mining of each block");

	public final Value placeDelay = new Value("Place delay", " ms", 0F, 100F, 500F, 5F, "Delay in MS between placing of each block");

	public final Toggleable distanceCheck = new Toggleable("Distance check", "Checks if the player can regularly mine the block");

	public final Toggleable silent = new Toggleable("Silent", "Adjust player rotation server-sided only");

	public final Mode<String> mode = new Mode<String>("Mode", "Switch between harvesting, planting, and using bonemeal", "Harvest", "Plant", "Bonemeal", "Hoe") {
		
		@Override
		public void setSelectedItem(int selectedItem) {
			super.setSelectedItem(selectedItem);
			placeTask.cancelPlacing();
			mineTask.cancelMining();
		}
	};
	
	private final MineTask mineTask = new MineTask(this);
	
	private final PlaceTask placeTask = new PlaceTask(this) {
		@Override
		protected boolean hasRequiredItem(ItemStack item) {
			return mode.getSelected() == 3 ? item.getItem() instanceof ItemHoe : mode.getSelected() == 2 ? (item.getItem() instanceof ItemDye && item.getMetadata() == 15) : (item.getItem() instanceof ItemSeeds || item.getItem() instanceof ItemSeedFood);
		}
		
		@Override
		public boolean shouldResetBlock() {
			IBlockState blockState = getBlockState();
			return mode.getSelected() == 3 ? !isDirt(blockState) : (mode.getSelected() == 2 ? ((BlockCrops) blockState.getBlock()).getMetaFromState(blockState) >= ((BlockCrops) blockState.getBlock()).getMaxAge() : super.shouldResetBlock());
		}
	};
	
	private final BlockLocator blockLocator = new BlockLocator() {

		@Override
		protected boolean isValidBlock(BlockPos position) {
			IBlockState blockState = mc.world.getBlockState(position);
			switch (mode.getSelected()) {
				case 0:
					if (isCrop(blockState)) {
						BlockCrops crops = (BlockCrops) blockState.getBlock();
						int age = crops.getMetaFromState(blockState);
						return age >= crops.getMaxAge();
					}
					break;
				case 1:
					return isFarmland(blockState) && !(mc.world.getBlockState(position.up()).getBlock() instanceof BlockCrops);
				case 2:
					if (isCrop(blockState)) {
						BlockCrops crops = (BlockCrops) blockState.getBlock();
						int age = crops.getMetaFromState(blockState);
						return age < crops.getMaxAge();
					}
					break;
				case 3:
					return isDirt(blockState);
			}
			return false;
		}

		@Override
		protected EnumFacing getFace(BlockPos position) {
			switch (mode.getSelected()) {
				case 0:
				case 2:
					return mc.player.getHorizontalFacing().getOpposite();
				case 1:
				case 3:
					return EnumFacing.UP;
			}
			return null;
		}
		
	};

	public Autofarm() {
		super("Auto farm", "Harvest/plant crops automagically");
		this.addChildren(radius, mineDelay, placeDelay, distanceCheck, silent, mode);
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
		huzuni.lookManager.withdrawTask(placeTask);
		blockLocator.reset();
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (huzuni.lookManager.hasPriority(this) && event.type == Type.PRE) {
			mineTask.setReset(silent.isEnabled());
			placeTask.setReset(silent.isEnabled());
			mineTask.setMineDelay((int) mineDelay.getValue());
			placeTask.setPlaceDelay((int) placeDelay.getValue());
			if (mineTask.hasBlock())
				huzuni.lookManager.requestTask(this, mineTask);
			else if (placeTask.hasBlock())
				huzuni.lookManager.requestTask(this, placeTask);
			else {
				huzuni.lookManager.withdrawTask(mineTask);
				huzuni.lookManager.withdrawTask(placeTask);
				findBlock();
			}
		}
	}

	/**
     * Finds a block using the block locator and updates the necessary tasks.
     * */
	private void findBlock() {
		blockLocator.setDistanceCheck(distanceCheck.isEnabled());
		if (blockLocator.locateClosest(radius.getValue(), false)) {
			if (mode.getSelected() == 0) {
				mineTask.setBlock(blockLocator.getPosition(), blockLocator.getFace());
				huzuni.lookManager.requestTask(this, mineTask);
			}
			if (mode.getSelected() > 0) {
				placeTask.setBlock(blockLocator.getPosition(), blockLocator.getFace());
				huzuni.lookManager.requestTask(this, placeTask);
			}
		}
	}

	@EventMethod
	public void onWorldLoad(EventLoadWorld event) {
		if (mineTask.hasBlock())
			huzuni.lookManager.withdrawTask(mineTask);
		if (placeTask.hasBlock())
			huzuni.lookManager.withdrawTask(placeTask);
	}
	
	@Override
	public String getDisplayNameForRender() {
		return super.getDisplayNameForRender();
	}

	/**
     * @return True if the block state is dirt.
     * */
	private boolean isDirt(IBlockState blockState) {
		return blockState.getBlock() != Blocks.AIR && (blockState.getBlock() instanceof BlockDirt || blockState.getBlock() instanceof BlockGrass);
	}

	/**
     * @return True if the block state is farm land.
     * */
	private boolean isFarmland(IBlockState blockState) {
		return blockState.getBlock() != Blocks.AIR && blockState.getBlock() instanceof BlockFarmland;
	}

	/**
     * @return True if the block state is a crop.
     * */
	private boolean isCrop(IBlockState blockState) {
		return blockState.getBlock() != Blocks.AIR && blockState.getBlock() instanceof BlockCrops;
	}
	
}
