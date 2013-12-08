package com.example.objects;

import java.util.LinkedList;

public class Game {
	int level;
	int t;
	int status;
	float width;
	float height;
	LinkedList<Plane> enemyPlaneList;
	LinkedList<Bullet> enemyBulletList;
	LinkedList<Bullet> ownBulletList;
	LinkedList<Plane> planeList;
	Plane ownPlane;

	public Game(float width, float height) {
		this.level = 0;
		this.t = 0;
		this.status = 0;
		this.width = width;
		this.height = height;
		this.enemyPlaneList = new LinkedList<Plane>();
		this.enemyBulletList = new LinkedList<Bullet>();
		this.ownBulletList = new LinkedList<Bullet>();
		this.planeList = new LinkedList<Plane>();
		this.ownPlane = new Plane(this.width/2,(float) (this.height*0.8));
		newLevel();
	}

	public void newLevel() {
		this.level++;
		this.t = 0;
		this.status = 0;
		ownPlane.mirror(this.height);
		this.planeList.add(ownPlane);
		this.enemyPlaneList.clear();
		this.enemyPlaneList = (LinkedList<Plane>) planeList.clone();
		this.enemyBulletList = new LinkedList<Bullet>();
		this.ownBulletList = new LinkedList<Bullet>();
		this.ownPlane = new Plane(this.width/2,(float) (this.height*0.8));

		// create initial bullets.
		for (float i = Bullet.width; i < this.width; i += (Bullet.width * 2)) {
			enemyBulletList.add(new Bullet(i, 0, 0, this.height
					/ ((this.level * 2 + 8) * 30)));
		}
	}
	
	public void reset() {
		this.t = 0;
		this.status = 0;
		this.enemyPlaneList.clear();
		this.enemyPlaneList = (LinkedList<Plane>) planeList.clone();
		this.enemyBulletList = new LinkedList<Bullet>();
		this.ownBulletList = new LinkedList<Bullet>();
		this.ownPlane = new Plane();

		// create initial bullets.
		for (float i = Bullet.width; i < this.width; i += (Bullet.width * 2)) {
			enemyBulletList.add(new Bullet(i, 0, 0, this.height
					/ ((this.level * 2 + 8) * 30)));
		}
	}

	public int update(float xVal, float yVal, boolean fire) {
		ownPlane.move(xVal, yVal, fire);
		if (fire) {
			ownBulletList.add(new Bullet(xVal, yVal, 0, -this.height / 30));
		}

		updateBulletList(ownBulletList);
		updateBulletList(enemyBulletList);
		updateEnemyPlaneList();
		updateOwnPlane();
		return status;
	}

	private void updateOwnPlane() {
		for (Bullet bullet : enemyBulletList) {
			if (ownPlane.getShot(bullet, t)) {
				gameover();
				return;
			}
		}
	}

	private void updateBulletList(LinkedList<Bullet> bulletList) {
		for (Bullet bullet : bulletList) {
			bullet.move();
			if (bullet.x < 0 || bullet.x > this.width || bullet.y < 0
					|| bullet.y > this.height) {
				bulletList.remove(bullet);
			}
		}
	}

	public void updateEnemyPlaneList() {
		boolean dead;
		for (Plane plane : enemyPlaneList) {
			dead = false;
			for (Bullet bullet : ownBulletList) {
				if (plane.getShot(bullet, t)) {
					enemyPlaneList.remove(plane);
					ownBulletList.remove(bullet);
					dead = true;
					break;
				}
			}
			if (dead)
				continue;
			if (plane.isFire(t)) {
				enemyBulletList.add(new Bullet(plane.getX(t), plane.getY(t), 0,
						this.height / 30));
			}
		}
		if (enemyPlaneList.size() == 0) {
			win();
			return;
		}
	}

	public LinkedList<float[]> getEnemyList() {
		LinkedList<float[]> result = new LinkedList<float[]>();
		for (Plane plane : enemyPlaneList) {
			float[] point = new float[2];
			point[0] = plane.getX(t);
			point[1] = plane.getY(t);
			result.add(point);
		}
		return result;
	}

	public LinkedList<float[]> getBulletList() {
		LinkedList<float[]> result = new LinkedList<float[]>();
		for (Bullet bullet : enemyBulletList) {
			float[] point = new float[2];
			point[0] = bullet.getX();
			point[1] = bullet.getY();
			result.add(point);
		}
		for (Bullet bullet : ownBulletList) {
			float[] point = new float[2];
			point[0] = bullet.getX();
			point[1] = bullet.getY();
			result.add(point);
		}
		return result;
	}

	private void win() {
		this.status = 1;
	}

	private void gameover() {
		this.status = 2;
	}
}
