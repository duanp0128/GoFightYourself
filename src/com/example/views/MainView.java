package com.example.views;

import java.util.LinkedList;

import com.example.gofightyourself.R;
import com.example.objects.Game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainView extends BaseView {
	// game resource
	private Game game;
	private boolean isPlaneTouched;
	private boolean isFire;
	private int gameStatus;
	private float planeX;
	private float planeY;
	private float dx;
	private float dy;
	private int level;
	private boolean isNewGame;
	// bitmap resource
	private Bitmap background;
	private Bitmap ownPlane;
	private Bitmap enemyPlane;
	private Bitmap bullet;
	private Bitmap buttonFire;
	private Bitmap buttonNoFire;

	public MainView(Context context) {
		super(context);
		// initial game resource
		level = 1;
		isFire = false;
		isPlaneTouched = false;
		isNewGame = true;

		thread = new Thread(this);
	}

	// plane whether move outside screen
	private boolean outsideScreen(float x, float y, Bitmap object) {
		float object_left = x - object.getWidth() / 2;
		float object_top = y - object.getHeight() / 2;
		float object_right = x + object.getWidth() / 2;
		float object_bottom = y + object.getHeight() / 2;

		if (object_left < 0 || object_right > screenWidth || object_top < 0
				|| object_bottom > screenHeight) {
			return true;
		}
		return false;
	}

	@Override
	public void initBitmap() {
		game = new Game(screenWidth, screenHeight, isNewGame);
		planeX = (float) (screenWidth * 0.5);
		planeY = (float) (screenHeight * 0.8);
		gameStatus = game.update(planeX, planeY, isFire);

		canvas = new Canvas();
		paint = new Paint();
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);
		ownPlane = BitmapFactory.decodeResource(getResources(),
				R.drawable.plane);
		enemyPlane = BitmapFactory.decodeResource(getResources(),
				R.drawable.plane_enemy);
		bullet = BitmapFactory
				.decodeResource(getResources(), R.drawable.bullet);
		buttonFire = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_fire);
		// buttonNoFire = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_nofire);
	}

	@Override
	public void draw() {
		gameStatus = game.update(planeX, planeY, isFire);
		// draw background
		canvas.save();
		canvas.scale(scaleWidth, scaleHeight, 0, 0);
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.restore();
		// draw fire button
		canvas.save();
		if (isFire) {
			canvas.drawBitmap(buttonFire, 5, 5, paint);
		} else {
			canvas.drawBitmap(buttonFire, 5, 5, paint);
		}
		canvas.restore();
		// draw level status
		canvas.save();
		paint.setTextSize(30);
		paint.setColor(Color.rgb(235, 161, 1));
		canvas.drawText("Level: " + String.valueOf(level), screenWidth / 3, 5,
				paint);
		canvas.restore();
		// draw own plane
		canvas.save();
		canvas.drawBitmap(ownPlane, planeX - ownPlane.getWidth() / 2, planeY
				- ownPlane.getHeight() / 2, paint);
		canvas.restore();
		// draw enemy plane
		LinkedList<float[]> enemyList = game.getEnemyList();
		for (int i = 0; i < enemyList.size(); ++i) {
			float left = enemyList.get(i)[0] - enemyPlane.getWidth() / 2;
			float top = enemyList.get(i)[1] - enemyPlane.getHeight() / 2;
			canvas.save();
			canvas.drawBitmap(enemyPlane, left, top, paint);
			canvas.restore();
		}
		// draw bullet
		LinkedList<float[]> bulletList = game.getBulletList();
		for (int i = 0; i < bulletList.size(); ++i) {
			float left = bulletList.get(i)[0] - bullet.getWidth() / 2;
			float top = bulletList.get(i)[1] - bullet.getHeight() / 2;
			canvas.save();
			canvas.drawBitmap(bullet, left, top, paint);
			canvas.restore();
		}

		/** win **/
		if (gameStatus == 1) {
			level++;
			game.newLevel();
			setWin(true);
			isNewGame = false;
			mainActivity.getHandler().sendEmptyMessage(END_VIEW);
		}

		/** die **/
		if (gameStatus == 2) {
			game.reset();
			setWin(false);
			isNewGame = true;
			mainActivity.getHandler().sendEmptyMessage(END_VIEW);
		}
	}

	@Override
	public void run() {
		while (threadFlag) {
			// Log.d("main", "run.....");
			long startTime = System.currentTimeMillis();
			synchronized (sfh) {
				canvas = sfh.lockCanvas();
				draw();
				sfh.unlockCanvasAndPost(canvas);
			}
			long endTime = System.currentTimeMillis();
			int intervalTime = (int) (endTime - startTime);
			// make sure every update time is 30 frame
			while (intervalTime <= TIME_IN_FRAME) {
				intervalTime = (int) (System.currentTimeMillis() - startTime);
				Thread.yield();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		initBitmap();
		if (thread.isAlive()) {
			thread.start();
		} else {
			thread = new Thread(this);
			thread.start();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		super.surfaceDestroyed(holder);
		release();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			// calculate current plane coordinate
			if (isPlaneTouched) {
				planeX = event.getX() + dx;
				planeY = event.getY() + dy;
				return true;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			isPlaneTouched = false;
			isFire = false;
			return true;
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			// judge whether click fire button
			if (x > 5 && x < 5 + buttonFire.getWidth() && y > 5
					&& y < 5 + buttonFire.getHeight()) {
				isPlaneTouched = false;
				isFire = true;
			} else {
				isPlaneTouched = true;
				isFire = false;
				// calculate the distance between plane and touch point
				dx = planeX - x;
				dy = planeY - y;
			}
			return true;
		}
		return false;
	}

	@Override
	public void release() {
		if (!buttonFire.isRecycled()) {
			buttonFire.recycle();
		} else if (!buttonNoFire.isRecycled()) {
			buttonNoFire.recycle();
		} else if (!bullet.isRecycled()) {
			bullet.recycle();
		} else if (!ownPlane.isRecycled()) {
			ownPlane.recycle();
		} else if (!enemyPlane.isRecycled()) {
			enemyPlane.recycle();
		}
	}

}
