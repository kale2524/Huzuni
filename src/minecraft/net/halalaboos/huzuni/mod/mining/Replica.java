package net.halalaboos.huzuni.mod.mining;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.halalaboos.huzuni.RenderManager.Renderer;
import net.halalaboos.huzuni.api.event.EventMouseClick;
import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.event.EventUpdate.Type;
import net.halalaboos.huzuni.api.mod.BasicCommand;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.settings.Toggleable;
import net.halalaboos.huzuni.api.settings.Value;
import net.halalaboos.huzuni.api.task.PlaceTask;
import net.halalaboos.huzuni.api.util.MathUtils;
import net.halalaboos.huzuni.api.util.MinecraftUtils;
import net.halalaboos.huzuni.api.util.render.Box;
import net.halalaboos.huzuni.api.util.render.GLManager;
import net.halalaboos.huzuni.gui.Notification.NotificationType;
import net.halalaboos.huzuni.mod.mining.templates.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

/**
 * Places blocks based on templates.
 * */
public final class Replica extends BasicMod implements Renderer {
	
	private final PlaceTask placeTask = new PlaceTask(this);
	
	public final Value placeDelay = new Value("Place delay", " ms", 0F, 100F, 500F, 5F, "Delay in MS between placing of each block");

	public final Toggleable silent = new Toggleable("Silent", "Adjust player rotation server-sided only");
	
	public final Mode<Template> mode = new Mode<Template>("Template", "Select which template will be used", new WallsTemplate(), new RectangleTemplate(), new CylinderTemplate(), new SwastikaTemplate(), new PortalTemplate(), new PenisTemplate()) {
		
		@Override
		public void setSelectedItem(int selectedItem) {
			super.setSelectedItem(selectedItem);
			templateBuilder.setTemplate(getSelectedItem());
		}
	};

	private final TemplateBuilder templateBuilder = new TemplateBuilder();

	private final Box box;
	
	// TODO: sphere, tunnel, stairs
	public Replica() {
		super("Replica", "Replicates blocks from a template");
		addChildren(placeDelay, silent, mode);
		setCategory(Category.MINING);
		silent.setEnabled(true);
		templateBuilder.setTemplate(mode.getSelectedItem());
		huzuni.lookManager.registerTaskHolder(this);
		placeTask.setNaturalPlacement(false);
		box = new Box(new AxisAlignedBB(0F, 0F, 0F, 1F, 1F, 1F), true);
		huzuni.commandManager.addCommand(new BasicCommand("template", "loads templates from text documents") {
			
			@Override
			public void giveHelp() {
				huzuni.addChatMessage(".template \"file\"");
			}
			
			@Override
			protected void runCommand(String input, String[] args) throws Exception {
				File file = new File(huzuni.getSaveFolder(), args[0]);
				try {
					BasicTemplate template = BasicTemplate.readTemplate(file);
					if (template != null) {
						mode.add(template);
						huzuni.addChatMessage("Template loaded!");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		
		});
	}

	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
		huzuni.renderManager.addWorldRenderer(this);
		huzuni.addNotification(NotificationType.INFO, this, 5000, "Right-click a block to begin building a template!");
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.renderManager.removeWorldRenderer(this);
		huzuni.lookManager.withdrawTask(placeTask);
		templateBuilder.resetPositions();
	}
	
	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (event.type == Type.PRE) {
			if (placeTask.hasBlock() && placeTask.isWithinDistance() && !placeTask.shouldResetBlock()) {
				placeTask.setPlaceDelay((int) placeDelay.getValue());
				placeTask.setReset(silent.isEnabled());
				huzuni.lookManager.requestTask(this, placeTask);
			} else {
				huzuni.lookManager.withdrawTask(placeTask);
				BlockPos closestPosition = null;
				EnumFacing closestFace = null;
				double closestDistance = 0;
				for (int i = 0; i < templateBuilder.getPositions().size(); i++) {
					BlockPos position = templateBuilder.getPositions().get(i);
					double distance = MathUtils.getDistance(position);
					if (distance < mc.playerController.getBlockReachDistance() && !mc.player.getPosition().equals(position)) {
						EnumFacing face = MinecraftUtils.getAdjacent(position);
						if (face != null) {
							if (closestPosition != null) {
								if (distance < closestDistance) {
									closestPosition = position;
									closestDistance = distance;
									closestFace = face;
								}
							} else {
								closestPosition = position;
								closestDistance = distance;
								closestFace = face;
							}
						}
					}
				}
				if (closestPosition != null) {
					placeTask.setBlock(closestPosition.offset(closestFace), closestFace.getOpposite());
					placeTask.setPlaceDelay((int) placeDelay.getValue());
					placeTask.setReset(silent.isEnabled());
					huzuni.lookManager.requestTask(this, placeTask);
				} else
					placeTask.setBlock(null, null);
			}
		}
	}

	@EventMethod
	public void onMouseClicked(EventMouseClick event) {
		if (event.buttonId == 1) {
			if (mc.objectMouseOver != null) {
				if (mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
					if (!templateBuilder.hasPositions()) {
						templateBuilder.addSelection(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);
					}
				}
			}
		}
	}
	
	@Override
	public String getDisplayNameForRender() {
		return settings.getDisplayName() + (templateBuilder.hasPositions() ? " (" + templateBuilder.getPositions().size() + ")" : "");
	}

	@Override
	public void render(float partialTicks) {
		if (!templateBuilder.hasPositions()) {
			if (templateBuilder.canPreview() && mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
				renderPreview(mc.objectMouseOver.getBlockPos(), mc.objectMouseOver.sideHit);
			}
		} else {
			for (int i = 0; i < templateBuilder.getPositions().size(); i++) {
				BlockPos position = templateBuilder.getPositions().get(i);
				if (mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
					templateBuilder.getPositions().remove(position);
					if (templateBuilder.getPositions().isEmpty()) {
						huzuni.addNotification(NotificationType.CONFIRM, this, 5000, "Finished!");
					}
					continue;
				}
				renderBox(position);
			}
		}
	}

	/**
     * Renders a preview of the current template with the next position being the position provided.
     * */
	public void renderPreview(BlockPos position, EnumFacing face) {
		List<BlockPos> previewPositions = templateBuilder.getPreview(position, face);
		for (int i = 0; i < previewPositions.size(); i++) {
			BlockPos previewPosition = previewPositions.get(i);
			renderBox(previewPosition);
		}
	}
	
	private void renderBox(BlockPos position) {
		renderBox(position.getX(), position.getY(), position.getZ());
	}
	
	private void renderBox(int x, int y, int z) {
		float renderX = (float) (x - mc.getRenderManager().viewerPosX);
		float renderY = (float) (y - mc.getRenderManager().viewerPosY);
		float renderZ = (float) (z - mc.getRenderManager().viewerPosZ);
		GlStateManager.pushMatrix();
		GlStateManager.translate(renderX, renderY, renderZ);
		GLManager.glColor(1F, 1F, 1F, 0.2F);
		box.render();
		GlStateManager.popMatrix();
	}

	/*
    Template building code that allowed the user to design their own pattern. It is possible to change the center for the template and other aspects of it etc. but this is not implemented for the user.

    public final ItemSelector<Integer> blockSelector = new ItemSelector<>("Custom Template", "great googly moogly");

    // This goes into the constructor.

    // ItemStack item = new ItemStack(Blocks.DIRT);
    //    for (int i = 0; i < 30; i++)
    //        blockSelector.addItem(item, i);

    private class CustomTemplate implements Template {

        @Override
        public String getName() {
            return "Custom";
        }

        @Override
        public String getDescription() {
            return "Build your own template!";
        }

        @Override
        public int getMaxPoints() {
            return 1;
        }

        @Override
        public String getPointName(int point) {
            return "Point " + (point + 1);
        }

        @Override
        public boolean insideBlock(BlockPos position) {
            return false;
        }

        @Override
        public void generate(List<BlockPos> outputPositions, EnumFacing face, BlockPos... positions) {
            int xOffset = face.rotateY().getDirectionVec().getX();
            int zOffset = face.rotateY().getDirectionVec().getZ();
            Integer[] vertices = generate(2, 4);
            BlockPos origin = positions[0];
            for (int i = 0; i < vertices.length; i += 2) {
                outputPositions.add(origin.add(xOffset * vertices[i], vertices[i + 1], zOffset * vertices[i]));
            }
        }

        // Generated the vertices from the center x and y positions (generated using the function below)
        private Integer[] generate(int centerX, int centerY) {
            List<Integer> vertices = new ArrayList<>();
            for (int i = 0; i < blockSelector.getItemDatas().size(); i++) {
                ItemSelector.ItemData itemData = blockSelector.getItemDatas().get(i);
                if (itemData.isEnabled()) {
                    vertices.add((i % 6) - centerX);
                    vertices.add(-((i / 6) - centerY));
                }
            }
            return vertices.toArray(new Integer[vertices.size()]);
        }

        // Generates the center positions needed for the index provided (I hope it does at least. The y position generated may need to be y - 1.)
        private Integer[] generateCenter(int index) {
            return new Integer[] { index % 6, index / 6 };
        }
    }
    */
}
