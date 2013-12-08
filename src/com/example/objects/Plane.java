/**
 * 
 */
package com.example.objects;

/**
 * @author NSH
 * 
 */
public class Plane {
	float[] x;
	float[] y;
	boolean[] fire;
	int life;
	static final float width = 90;
	static final float height = 135;

	public Plane() {
		x = new float[1000];
		y = new float[1000];
		fire = new boolean[1000];
		life = 0;
	}

	public Plane(float xVal, float yVal) {
		x = new float[1000];
		y = new float[1000];
		fire = new boolean[1000];
		life = 1;
		x[0] = xVal;
		y[0] = yVal;
	}

	public void move(float xVal, float yVal, boolean fire) {
		x[life] = xVal;
		y[life] = yVal;
		this.fire[life] = fire;
		life++;
	}

	boolean getShot(Bullet bullet, int t) {
		if (Math.abs(bullet.getX() - this.getX(t)) <= ((Plane.width + Bullet.width) / 2)
				&& Math.abs(bullet.getY() - this.getY(t)) <= ((Plane.height + Bullet.height) / 2))
			return true;
		return false;
	}

	float getX(int t) {
		return x[t % life];
	}

	float getY(int t) {
		return y[t % life];
	}

	boolean isFire(int t) {
		return fire[t % life];
	}

	public void mirror(float screenHeight) {
		for (int i = 0; i < this.life; i++) {
			this.y[i] = screenHeight - this.y[i];
		}
	}
}
