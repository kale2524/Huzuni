package net.halalaboos.huzuni.mc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.world.World;

public class HuzuniPlayerController extends PlayerControllerMP {

	private final Minecraft mc;
	
	private final NetHandlerPlayClient netHandler;
	
	public HuzuniPlayerController(Minecraft mc, NetHandlerPlayClient netHandler) {
		super(mc, netHandler);
		this.mc = mc;
		this.netHandler = netHandler;
		
	}
	
	@Override
	public EntityPlayerSP createClientPlayer(World worldIn, StatisticsManager statFile) {
		return new HuzuniEntityClient(this.mc, worldIn, netHandler, statFile);
	}
}
