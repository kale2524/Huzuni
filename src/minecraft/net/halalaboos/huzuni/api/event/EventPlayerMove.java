package net.halalaboos.huzuni.api.event;

/**
 * This event is fired when the player moves.
 * */
public class EventPlayerMove {

	private double motionX, motionY, motionZ;

	public EventPlayerMove(double motionX, double motionY, double motionZ) {
		this.motionX = motionX;
		this.motionY = motionY;
		this.motionZ = motionZ;
	}

	public double getMotionX() {
		return motionX;
	}

	public void setMotionX(double motionX) {
		this.motionX = motionX;
	}

	public double getMotionY() {
		return motionY;
	}

	public void setMotionY(double motionY) {
		this.motionY = motionY;
	}

	public double getMotionZ() {
		return motionZ;
	}

	public void setMotionZ(double motionZ) {
		this.motionZ = motionZ;
	}
	
}
