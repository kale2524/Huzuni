package net.halalaboos.huzuni.mod.visual;

import java.util.Arrays;

import org.lwjgl.input.Keyboard;

import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.ItemSelector;
import net.halalaboos.huzuni.api.settings.Value;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

/**
 * Shows blocks hidden behind other blocks.
 * */
public class Xray extends BasicMod {
	
	public static final Xray INSTANCE = new Xray();
	
	public final ItemSelector<Block> blockList = new ItemSelector<Block>("Xray blocks", "Select blocks that you want enabled.");
	
	public final Value opacity = new Value("Opacity", "%", 0F, 30F, 100F, 1F, "Opacity blocks are rendered with.");

    private final int ignoredBlocks[] = {
            6, 31, 32, 37, 38, 83, 106, 111, 175
    };

    private float brightness = 0;

    private Xray() {
		super("Xray", "See stuff", Keyboard.KEY_X);
		this.setCategory(Category.VISUAL);
		this.addChildren(opacity, blockList);
		blockList.addItem(new ItemStack(Items.WATER_BUCKET), Blocks.WATER, Blocks.FLOWING_WATER);
		blockList.addItem(new ItemStack(Items.LAVA_BUCKET), Blocks.LAVA, Blocks.FLOWING_LAVA);
		blockList.addItem(new ItemStack(Blocks.DIAMOND_ORE), Blocks.DIAMOND_ORE);
		blockList.addItem(new ItemStack(Blocks.EMERALD_ORE), Blocks.EMERALD_ORE);
		blockList.addItem(new ItemStack(Blocks.LAPIS_ORE), Blocks.LAPIS_ORE);
		blockList.addItem(new ItemStack(Blocks.GOLD_ORE), Blocks.GOLD_ORE);
		
		blockList.addItem(new ItemStack(Blocks.IRON_ORE), Blocks.IRON_ORE);
		blockList.addItem(new ItemStack(Blocks.REDSTONE_ORE), Blocks.REDSTONE_ORE, Blocks.LIT_REDSTONE_ORE);
		blockList.addItem(new ItemStack(Blocks.COAL_ORE), Blocks.COAL_ORE);
		blockList.addItem(new ItemStack(Blocks.QUARTZ_ORE), Blocks.QUARTZ_ORE);
		blockList.addItem(new ItemStack(Blocks.DIAMOND_BLOCK), Blocks.DIAMOND_BLOCK);
		blockList.addItem(new ItemStack(Blocks.EMERALD_BLOCK), Blocks.EMERALD_BLOCK);
		
		blockList.addItem(new ItemStack(Blocks.LAPIS_BLOCK), Blocks.LAPIS_BLOCK);
		blockList.addItem(new ItemStack(Blocks.GOLD_BLOCK), Blocks.GOLD_BLOCK);
		blockList.addItem(new ItemStack(Blocks.IRON_BLOCK), Blocks.IRON_BLOCK);
		blockList.addItem(new ItemStack(Blocks.REDSTONE_BLOCK), Blocks.REDSTONE_BLOCK);
		blockList.addItem(new ItemStack(Blocks.COAL_BLOCK), Blocks.COAL_BLOCK);
		blockList.addItem(new ItemStack(Blocks.QUARTZ_BLOCK), Blocks.QUARTZ_BLOCK);

		blockList.addItem(new ItemStack(Blocks.OBSIDIAN), Blocks.OBSIDIAN);
		blockList.addItem(new ItemStack(Blocks.MOSSY_COBBLESTONE), Blocks.MOSSY_COBBLESTONE);
		blockList.addItem(new ItemStack(Blocks.MOB_SPAWNER), Blocks.MOB_SPAWNER);
		blockList.addItem(new ItemStack(Blocks.END_BRICKS), Blocks.END_BRICKS);
		blockList.addItem(new ItemStack(Blocks.END_STONE), Blocks.END_STONE);
		blockList.addItem(new ItemStack(Blocks.NETHER_BRICK), Blocks.NETHER_BRICK);
		
		blockList.addItem(new ItemStack(Blocks.NETHERRACK), Blocks.NETHERRACK);
		blockList.addItem(new ItemStack(Blocks.SOUL_SAND), Blocks.SOUL_SAND);
		blockList.addItem(new ItemStack(Blocks.PRISMARINE), Blocks.PRISMARINE);
		blockList.addItem(new ItemStack(Blocks.PURPUR_BLOCK), Blocks.PURPUR_BLOCK);
		blockList.addItem(new ItemStack(Blocks.SLIME_BLOCK), Blocks.SLIME_BLOCK);
		blockList.addItem(new ItemStack(Blocks.SPONGE), Blocks.SPONGE);
		
		blockList.addItem(new ItemStack(Blocks.BOOKSHELF), Blocks.BOOKSHELF);
		blockList.addItem(new ItemStack(Blocks.BRICK_BLOCK), Blocks.BRICK_BLOCK);
		blockList.addItem(new ItemStack(Blocks.COBBLESTONE), Blocks.COBBLESTONE);
		blockList.addItem(new ItemStack(Blocks.HAY_BLOCK), Blocks.HAY_BLOCK);
		blockList.addItem(new ItemStack(Blocks.PUMPKIN), Blocks.PUMPKIN, Blocks.LIT_PUMPKIN);
		blockList.addItem(new ItemStack(Blocks.LOG), Blocks.LOG, Blocks.LOG2);
		
		blockList.addItem(new ItemStack(Blocks.SNOW), Blocks.SNOW);
		blockList.addItem(new ItemStack(Blocks.WOOL), Blocks.WOOL);
		// blockList.addItem(new ItemStack(Blocks.ICE), Blocks.ICE, Blocks.FROSTED_ICE, Blocks.PACKED_ICE);
		blockList.addItem(new ItemStack(Blocks.BEDROCK), Blocks.BEDROCK);
		blockList.addItem(new ItemStack(Blocks.CLAY), Blocks.CLAY);
		blockList.addItem(new ItemStack(Blocks.HARDENED_CLAY),Blocks.HARDENED_CLAY, Blocks.STAINED_HARDENED_CLAY);
		
		blockList.addItem(new ItemStack(Blocks.SAND), Blocks.SAND);
		blockList.addItem(new ItemStack(Blocks.GRAVEL), Blocks.GRAVEL);
		blockList.addItem(new ItemStack(Blocks.SANDSTONE), Blocks.SANDSTONE);
		blockList.addItem(new ItemStack(Blocks.RED_SANDSTONE), Blocks.RED_SANDSTONE);
		// blockList.addItem(new ItemStack(Blocks.LEAVES), Blocks.LEAVES, Blocks.LEAVES2);
		// blockList.addItem(new ItemStack(Blocks.GLASS), Blocks.GLASS);
		this.settings.setDisplayable(false);
	}
	
	@Override
	protected void onEnable() {
		brightness = mc.gameSettings.gammaSetting;
		mc.gameSettings.gammaSetting = 1000F;
	}
	

	@Override
	protected void onDisable() {
		mc.gameSettings.gammaSetting = brightness;
	}
	
	@Override
	public void toggle() {
		super.toggle();
		Minecraft.getMinecraft().renderGlobal.loadRenderers();
	}
	
	public boolean isEnabled(Block block) {
		return blockList.isEnabledObject(block);
	}

	public boolean shouldIgnore(Block block) {
		return Arrays.binarySearch(ignoredBlocks, Block.getIdFromBlock(block)) >= 0;
	}

	public int getOpacity() {
		return (int) ((opacity.getValue() / 100F) * 255F);
	}
	
	public boolean hasOpacity() {
		return getOpacity() > 20;
	}
}
