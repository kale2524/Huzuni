package net.halalaboos.huzuni.mod.mining;

import net.halalaboos.huzuni.api.event.EventUpdate;
import net.halalaboos.huzuni.api.event.EventManager.EventMethod;
import net.halalaboos.huzuni.api.mod.BasicMod;
import net.halalaboos.huzuni.api.mod.Category;
import net.halalaboos.huzuni.api.settings.Value;

/**
 * Modifies players placement speed.
 * */
public class Fastplace extends BasicMod {
	
	public final Value speed = new Value("Speed", "", 1F, 2F, 4F, 1F, "Speed you will place blocks at");
	
	public Fastplace() {
		super("Fast place", "Places blocks at a faster rate");
		this.setCategory(Category.MINING);
		this.addChildren(speed);
	}
	
	@Override
	public void onEnable() {
		huzuni.eventManager.addListener(this);
	}
	
	@Override
	public void onDisable() {
		huzuni.eventManager.removeListener(this);
	}

	@EventMethod
	public void onUpdate(EventUpdate event) {
    	float speed = this.speed.getValue();
    	// TODO: Fix this.
        //if (mc.rightClickDelayTimer > (4 - (byte) speed))
            //mc.rightClickDelayTimer = (4 - (byte) speed);
    }

}
