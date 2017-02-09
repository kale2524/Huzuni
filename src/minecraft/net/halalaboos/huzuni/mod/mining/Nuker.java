package net.halalaboos.huzuni.mod.mining;

import net.halalaboos.huzuni.api.event.EventLoadWorld;
import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventMouseClick;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.MineTask;
import net.halalaboos.huzuni.api.util.BlockLocator;
import net.halalaboos.huzuni.api.util.MinecraftUtils;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;

public final class Nuker extends BasicMod {
	
	private final Value radius = new Value("Radius", " blocks", 1F, 5F, 5F, 1F, "Radius around the player to mine blocks");
	
	private final Value mineDelay = new Value("Mine delay", " ms", 0F, 100F, 500F, 5F, "Delay in MS between mining of each block");

	private final Toggleable distanceCheck = new Toggleable("Distance check", "Checks if the player can regularly mine the block");
	
	private final Toggleable silent = new Toggleable("Silent", "Adjust player rotation server-sided only");

	private final MineTask mineTask = new MineTask(this);
	
	private final BlockLocator blockLocator = new BlockLocator() {

		@Override
		protected boolean isValidBlock(BlockPos position) {
			IBlockState blockState = mc.world.getBlockState(position);
			return blockState.getBlock() != Blocks.AIR && blockState.getBlock() == type;
		}

		@Override
		protected EnumFacing getFace(BlockPos position) {
			return MinecraftUtils.findFace(position);
		}
		
	};
	
	private Block type;
	
	public Nuker() {
		super("Nuker", "Annihilates blocks within a radius surrounding the player", Keyboard.KEY_L);
		this.addChildren(radius, mineDelay, distanceCheck, silent);
		this.setCategory(Category.MINING);
		silent.setEnabled(true);
		huzuni.lookManager.registerTaskHolder(this);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
		huzuni.addNotification(NotificationType.INFO, this, 5000, "Select a block type by right-clicking!");
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.lookManager.withdrawTask(mineTask);
		blockLocator.reset();
		this.type = null;
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

	private void findBlock() {
		blockLocator.setDistanceCheck(distanceCheck.isEnabled());
		if (blockLocator.locateClosest(this.radius.getValue(), false)) {
			mineTask.setBlock(blockLocator.getPosition(), blockLocator.getFace());
			huzuni.lookManager.requestTask(this, mineTask);
		}
	}

	@EventMethod
	public void loadWorld(EventLoadWorld event) {
		if (mineTask.hasBlock())
			huzuni.lookManager.withdrawTask(mineTask);
	}
	
	@EventMethod
	public void onMouseClicked(EventMouseClick event) {
		if (event.buttonId == 1) {
			if (mc.objectMouseOver != null) {
				if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
					IBlockState blockState = mc.world.getBlockState(mc.objectMouseOver.getBlockPos());
					this.type = blockState.getBlock();
					mineTask.cancelMining();
				}
			}
		}
	}
	
	@Override
	public String getDisplayNameForRender() {
		return super.getDisplayNameForRender() + (type == null ? "" : String.format(" (%s)", getBlockName(type)));
	}
	
	private String getBlockName(Block block) {
		Item item = Item.getItemFromBlock(block);
		return item != null ? ("" + I18n.translateToLocal(item.getUnlocalizedName() + ".name")).trim() : Block.REGISTRY.getNameForObject(block).toString();
	}
	
}
