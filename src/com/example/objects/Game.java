package com.example.objects;

import java.util.Iterator;
import java.util.LinkedList;

import android.util.Log;

public class Game {
	static int level;
	int t;
	int status;
	float width;
	float height;
	boolean fired;
	LinkedList<Plane> enemyPlaneList;
	LinkedList<Bullet> enemyBulletList;
	LinkedList<Bullet> ownBulletList;
	static LinkedList<Plane> planeList;
	Plane ownPlane;

	public Game(float width, float height, boolean newGame) {
		this.t = 0;
		this.status = 0;
		this.width = width;
		this.height = height;
		this.fired = false;
		this.enemyPlaneList = new LinkedList<Plane>();
		this.enemyBulletList = new LinkedList<Bullet>();
		this.ownBulletList = new LinkedList<Bullet>();
		this.ownPlane = new Plane(Game.level, this.width / 2,
				(float) (this.height * 0.8));

		if (newGame) {
			Game.level = 0;
			Game.planeList = new LinkedList<Plane>();
			this.ownPlane = new Plane(Game.level, this.width / 2,
					(float) (this.height * 0.8));
			this.enemyPlaneList = new LinkedList<Plane>();
			newLevel(true);
		}
		newLevel(false);
	}

	@SuppressWarnings("unchecked")
	public void newLevel(boolean levelup) {

		this.t = 0;
		this.status = 0;
		this.fired = false;
		if (levelup) {
			ownPlane.mirror(this.height);
			Game.planeList.add(ownPlane);
			Game.level++;
		}
		this.ownPlane = new Plane(Game.level, this.width / 2,
				(float) (this.height * 0.8));
		this.enemyPlaneList.clear();
		this.enemyPlaneList = (LinkedList<Plane>) Game.planeList.clone();
		Log.d("game",
				Game.planeList.size() + "clone" + this.enemyPlaneList.size());
		this.enemyBulletList = new LinkedList<Bullet>();
		this.ownBulletList = new LinkedList<Bullet>();
		this.ownPlane = new Plane(Game.level, this.width / 2,
				(float) (this.height * 0.8));

		// create initial bullets.
		for (float i = Bullet.width; i < this.width; i += (Bullet.width * 3)) {
			enemyBulletList.add(new Bullet(i, 0, 0, this.height
					/ ((Game.level * 2 + 8) * 30)));
		}
	}

	public int update(float xVal, float yVal, boolean fire) {
		Log.d("game", this.t + "update");
		if (fire) {
			if (fired) {
				fire = false;
			} else {
				fired = true;
			}
		} else if (fired) {
			fired = false;
		}

		t++;
		ownPlane.move(xVal, yVal, fire);

		if (fire) {
			ownFire(xVal, yVal);
		}

		updateBulletList(ownBulletList);
		updateBulletList(enemyBulletList);
		updateEnemyPlaneList();
		updateOwnPlane();
		if (status == 1) {
			this.newLevel(true);
			return 1;
		}
		if (status == 2) {
			this.newLevel(false);
			return 2;
		}
		return 0;
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
		Iterator<Bullet> iterBullet = bulletList.iterator();
		while (iterBullet.hasNext()) {
			Bullet bullet = iterBullet.next();
			bullet.move();
			if (bullet.x < 0 || bullet.x > this.width || bullet.y < 0
					|| bullet.y > this.height) {
				iterBullet.remove();
			}
		}
	}

	public void updateEnemyPlaneList() {
		boolean dead;
		Iterator<Plane> iterPlane = enemyPlaneList.iterator();
		Log.d("game", "#" + enemyPlaneList.size());
		while (iterPlane.hasNext()) {
			Plane plane = iterPlane.next();
			dead = false;
			Iterator<Bullet> iterBullet = ownBulletList.iterator();
			while (iterBullet.hasNext()) {
				Bullet bullet = iterBullet.next();
				if (plane.getShot(bullet, t)) {
					iterPlane.remove();
					iterBullet.remove();
					dead = true;
					break;
				}
			}
			if (dead)
				continue;
			if (plane.isFire(t)) {
				enemyFire(plane.level, plane.getX(t), plane.getY(t));
			}
		}
		Log.d("game", "##" + enemyPlaneList.size());
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
		Log.d("game", "win");
	}

	private void gameover() {
		this.status = 2;
	}

	public int getLevel() {
		return Game.level;
	}

	private void ownFire(float xVal, float yVal) {
		int level = Game.level;
		int direction = -1;
		switch (level % 3) {
		case 1:
			ownBulletList.add(new Bullet(xVal, yVal, 0, direction * this.height
					/ 30));
			break;
		case 2:
			ownBulletList.add(new Bullet(xVal, yVal, -this.width / 60,
					direction * this.height / 30));
			ownBulletList.add(new Bullet(xVal, yVal, this.width / 60, direction
					* this.height / 30));
			break;
		case 0:
			ownBulletList.add(new Bullet(xVal, yVal, 0, direction * this.height
					/ 30));
			ownBulletList.add(new Bullet(xVal, yVal, -this.width / 60,
					direction * this.height / 30));
			ownBulletList.add(new Bullet(xVal, yVal, this.width / 60, direction
					* this.height / 30));
		}
	}

	private void enemyFire(int level, float xVal, float yVal) {
		int direction = 1;
		switch (level % 3) {
		case 1:
			enemyBulletList.add(new Bullet(xVal, yVal, 0, direction
					* this.height / 30));
			break;
		case 2:
			enemyBulletList.add(new Bullet(xVal, yVal, -this.width / 60,
					direction * this.height / 30));
			enemyBulletList.add(new Bullet(xVal, yVal, this.width / 60,
					direction * this.height / 30));
			break;
		case 0:
			enemyBulletList.add(new Bullet(xVal, yVal, 0, direction
					* this.height / 30));
			enemyBulletList.add(new Bullet(xVal, yVal, -this.width / 60,
					direction * this.height / 30));
			enemyBulletList.add(new Bullet(xVal, yVal, this.width / 60,
					direction * this.height / 30));
		}
	}

}
