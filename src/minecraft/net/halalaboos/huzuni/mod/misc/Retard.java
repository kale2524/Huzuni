package net.halalaboos.huzuni.mod.misc;

import java.util.Random;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Mode;
import net.halalaboos.huzuni.api.task.LookTask;

public class Retard extends BasicMod {

    private static final int HEADBANG_RATE = 45, NO_RATE = 70;

	private final Random random = new Random();

	private final Mode<String> modeYaw = new Mode<String>("Yaw mode", "Style of expression", "None", "Random", "Say no");

	private final Mode<String> modePitch = new Mode<String>("Pitch mode", "Style of expression", "None", "Random", "Headless", "Headbang");
	
	private final LookTask lookTask = new LookTask(this);

	private int yawPosition = 0, pitchPosition;

    private boolean headbangUp = false, noLeft = false;
	
	public Retard() {
		super("Retard", "Express your inner capabilities and spin your head around server-side");
		setCategory(Category.MISC);
		addChildren(modeYaw, modePitch);
		huzuni.lookManager.registerTaskHolder(this);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
		huzuni.lookManager.withdrawTask(lookTask);
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
		if (event.type == EventUpdate.Type.PRE) {
			if (huzuni.lookManager.hasPriority(this)) {
				switch (modeYaw.getSelected()) {
				case 0:
					lookTask.setYaw(mc.player.rotationYaw);
					break;
				case 1:
					lookTask.setYaw(random.nextBoolean() ? random.nextInt(180)  : -random.nextInt(180));
					break;
				case 2:
				    yawPosition += (noLeft ? -8 : 8);
                    if (yawPosition < -NO_RATE) {
                        noLeft = false;
                        yawPosition = -NO_RATE;
                    } else if (yawPosition > NO_RATE) {
                        noLeft = true;
                        yawPosition = NO_RATE;
                    }
				    lookTask.setYaw(mc.player.rotationYaw + yawPosition);
					break;
				}
				
				switch (modePitch.getSelected()) {
				case 0:
					lookTask.setPitch(mc.player.rotationPitch);
					break;
				case 1:
					lookTask.setPitch(random.nextBoolean() ? random.nextInt(90)  : -random.nextInt(90));
					break;
				case 2:
					lookTask.setPitch(180);
					break;
                case 3:
                    pitchPosition += (headbangUp ? -8 : 8);
                    if (pitchPosition < -HEADBANG_RATE) {
                        headbangUp = false;
                        pitchPosition = -HEADBANG_RATE;
                    } else if (pitchPosition > HEADBANG_RATE) {
                        headbangUp = true;
                        pitchPosition = HEADBANG_RATE;
                    }
                    lookTask.setPitch(mc.player.rotationPitch + pitchPosition);
                    break;
				}
				huzuni.lookManager.requestTask(this, lookTask);
			}
		}
	}

}
