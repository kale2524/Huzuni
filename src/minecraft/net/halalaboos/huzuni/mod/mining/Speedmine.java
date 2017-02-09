package net.halalaboos.huzuni.mod.mining;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.event.EventPacket;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Value;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayerDigging;

/**
 * Modifies player mine speed on all blocks.
 * */
public class Speedmine extends BasicMod {

	public final Value speed = new Value("Mine speed", "", 1F, 1F, 2F, "Mine speed modifier");
	
	private boolean digging = false;
	
	private float curBlockDamage = 0;

	private EnumFacing facing;

	private BlockPos position;

	public Speedmine() {
		super("Speedmine", "Mines blocks at a faster rate", Keyboard.KEY_V);
		this.setCategory(Category.MINING);
		this.addChildren(speed);
	}

	@Override
	protected void onEnable() {
		huzuni.eventManager.addListener(this);
	}

	@Override
	protected void onDisable() {
		huzuni.eventManager.removeListener(this);
	}
	
	@EventMethod
	public void onPacket(EventPacket event) {
		if (event.type == EventPacket.Type.SENT && mc.playerController != null && !mc.playerController.isInCreativeMode()) {
			if (event.getPacket() instanceof CPacketPlayerDigging) {
				CPacketPlayerDigging packet = (CPacketPlayerDigging) event.getPacket();
				if (packet.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK) {
					digging = true;
					this.position = packet.getPosition();
					this.facing = packet.getFacing();
					this.curBlockDamage = 0;
				} else if (packet.getAction() == CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK || packet.getAction() == CPacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
					digging = false;
					this.position = null;
					this.facing = null;
				}
			}
		}
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (event.type == EventUpdate.Type.PRE) {
			if (mc.playerController.isInCreativeMode()) {
				// TODO: Fix this.
				//mc.playerController.blockHitDelay = 0;
				return;
			}
			if (digging) {
				IBlockState blockState = this.mc.world.getBlockState(position);
				curBlockDamage += blockState.getPlayerRelativeBlockHardness(this.mc.player, this.mc.world, this.position) * (speed.getValue());
				if (curBlockDamage >= 1.0F) {
					mc.world.setBlockState(position, Blocks.AIR.getDefaultState(), 11);
					mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.position, this.facing));
					curBlockDamage = 0F;
					digging = false;
				}
			}
		}
	}

}
