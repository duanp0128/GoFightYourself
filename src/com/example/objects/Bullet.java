package com.example.objects;

public class Bullet {
	float x;
	float y;
	float speedX;
	float speedY;
	static final float width = 9;
	static final float height = 45;

	public Bullet(float headX, float headY, float speedX, float speedY) {
		this.x = headX;
		this.y = headY;
		this.speedX = speedX;
		this.speedY = speedY;
	}

	public void move() {
		x += speedX;
		y += speedY;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getSpeedX() {
		return speedX;
	}

	public void setSpeedX(float speedX) {
		this.speedX = speedX;
	}

	public float getSpeedY() {
		return speedY;
	}

	public void setSpeedY(float speedY) {
		this.speedY = speedY;
	}

}
