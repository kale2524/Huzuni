package net.halalaboos.huzuni.gui.widgets;

import net.halalaboos.huzuni.api.gui.WidgetManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;

public class BiomeWidget extends BackgroundWidget {

	public BiomeWidget(WidgetManager menuManager) {
		super("Biome", "Renders the biome you are currently inside", menuManager);
	}

	@Override
	public void renderMenu(int x, int y, int width, int height) {
		super.renderMenu(x, y, width, height);
        Chunk currentChunk = mc.world.getChunkFromBlockCoords(new BlockPos(MathHelper.floor(mc.player.posX), ((int) mc.player.posY), MathHelper.floor(mc.player.posZ)));
        String biome = "Biome: " + currentChunk.getBiome(new BlockPos(MathHelper.floor(mc.player.posX) & 15, mc.player.posY, MathHelper.floor(mc.player.posZ) & 15), mc.world.getBiomeProvider()).getBiomeName();
		theme.drawStringWithShadow(biome, x, y, 0xFFFFFF);
		this.setWidth(theme.getStringWidth(biome) + 2);
		this.setHeight(theme.getStringHeight(biome));
	}
}
