package net.halalaboos.huzuni.api.util;

/**
 * A position which can increment to a second position over time.
 * */
public class IncrementalPosition {

	private final Timer timer = new Timer();
	
	private float x, y, z, finalX, finalY, finalZ, xIncrement = 4F, yIncrement = 4F, zIncrement = 4F;

	private int rate = 10;
	
	private long previousIncrement = 0L;

	/**
	 * Updates the position and increments it until it reaches the final position.
	 * */
	public void updatePosition() {
		if (timer.hasReach(rate)) {
			int incrementCount = previousIncrement == 0L ? 1 : (int) Math.ceil((Timer.getSystemTime() - previousIncrement) / rate);
			x +=  MathUtils.clamp(finalX - x, incrementCount * xIncrement);
			y +=  MathUtils.clamp(finalY - y, incrementCount * yIncrement);
			z +=  MathUtils.clamp(finalZ - z, incrementCount * zIncrement);
			timer.reset();
			previousIncrement = Timer.getSystemTime();
		}
	}

	/**
	 * Sets the position and the final position.
	 * */
	public void setPosition(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.finalX = x;
		this.finalY = y;
		this.finalZ = z;
		previousIncrement = Timer.getSystemTime();
	}

	/**
	 * Sets the final position.
	 * */
	public void setFinalPosition(float x, float y, float z) {
		this.finalX = x;
		this.finalY = y;
		this.finalZ = z;
	}

	/**
	 * Sets the increment amount.
	 * */
	public void setIncrement(float x, float y, float z) {
		this.xIncrement = x;
		this.yIncrement = y;
		this.zIncrement = z;
	}

	
	public boolean hasFinished() {
		return x == finalX && y == finalY && z == finalZ;
	}

	public float getX() {
		return x;
	}
	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}
	public float getFinalX() {
		return finalX;
	}
	public float getFinalY() {
		return finalY;
	}

	public float getFinalZ() {
		return finalZ;
	}

	public float getxIncrement() {
		return xIncrement;
	}

	public void setxIncrement(float xIncrement) {
		this.xIncrement = xIncrement;
	}

	public float getyIncrement() {
		return yIncrement;
	}

	public void setyIncrement(float yIncrement) {
		this.yIncrement = yIncrement;
	}

	public float getzIncrement() {
		return zIncrement;
	}

	public void setzIncrement(float zIncrement) {
		this.zIncrement = zIncrement;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	/**
	 * @return True if the x, y, and z values are zero.
	 * */
	public boolean isZero() {
		return x == 0 && y == 0 && z == 0;
	}
}
