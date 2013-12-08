package com.example.views;

import java.util.LinkedList;

import com.example.objects.Game;
import com.example.objects.Plane;
import com.example.sounds.GameSoundPool;

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
	private boolean isNewGame;
	// bitmap resource
	private Bitmap background;
	private Bitmap ownPlane;
	private Bitmap enemyPlane;
	private Bitmap bullet;
	private Bitmap buttonFire;
	private Bitmap buttonNoFire;
	private int currentFrame; //

	public MainView(Context context, GameSoundPool soundPool) {
		super(context, soundPool);
		// initial game resource
		isFire = false;
		isPlaneTouched = false;
		isNewGame = true;
		currentFrame = 0;

		thread = new Thread(this);
	}

	@Override
	public void initBitmap() {
		game = new Game(screenWidth, screenHeight, isNewGame);
		planeX = (float) (screenWidth * 0.5);
		planeY = (float) (screenHeight * 0.8);
		setWin(false);

		canvas = new Canvas();
		paint = new Paint();
//		background = BitmapFactory.decodeResource(getResources(),
//				R.drawable.background);
//		ownPlane = BitmapFactory.decodeResource(getResources(),
//				R.drawable.human_own);
//		enemyPlane = BitmapFactory.decodeResource(getResources(),
//				R.drawable.human_front);
//		bullet = BitmapFactory.decodeResource(getResources(), R.drawable.biao);
//		buttonFire = BitmapFactory.decodeResource(getResources(),
//				R.drawable.button_fire);
		scaleWidth = screenWidth / background.getWidth();
		scaleHeight = screenHeight / background.getHeight();
		// buttonNoFire = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_nofire);
	}

	@Override
	public void draw() {
		// keep human inside the screen
		if (planeX < Plane.width / 2)
			planeX = Plane.width / 2;
		if (planeX + Plane.width / 2 >= screenWidth)
			planeX = scaleWidth - Plane.width / 2;
		if (planeY < Plane.height / 2)
			planeY = Plane.height / 2;
		if (planeY + Plane.height / 2 >= screenHeight)
			planeY = scaleHeight - Plane.height / 2;
		gameStatus = game.update(planeX, planeY, isFire);
		/** win **/
		if (gameStatus == 1) {
			setWin(true);
			Log.d("win", "win");
			mainActivity.getHandler().sendEmptyMessage(END_VIEW);
		}

		/** die **/
		if (gameStatus == 2) {
			setWin(false);
			mainActivity.getHandler().sendEmptyMessage(END_VIEW);
		}
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
		canvas.drawText("Level: " + String.valueOf(game.getLevel()),
				screenWidth / 3, 60, paint);
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
			int x = (int) (currentFrame * bullet.getWidth()); // 获得当前帧相对于位图的X坐标
			canvas.save();
			canvas.clipRect(left, top, left + bullet.getWidth(),
					top + bullet.getHeight());
			canvas.drawBitmap(bullet, left + x, top, paint);
			canvas.restore();
			currentFrame++;
			if (currentFrame > 2) {
				currentFrame = 0;
			}
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
		if (thread.isAlive()) {
			initBitmap();
			thread.start();
		} else {
			thread = new Thread(this);
			initBitmap();
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
		int pointCount = event.getPointerCount();
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// first finger press
		case MotionEvent.ACTION_DOWN:
			float x = event.getX(0);
			float y = event.getY(0);
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
			break;
		case MotionEvent.ACTION_UP:
			isFire = isPlaneTouched = false;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			float x0 = event.getX();
			float y0 = event.getY();
			if (x0 > 5 && x0 < 5 + buttonFire.getWidth() && y0 > 5
					&& y0 < 5 + buttonFire.getHeight()) {
				isFire = true;
				isPlaneTouched = false;
			} else {
				isFire = false;
				isPlaneTouched = true;
			}
			break;
		// second finger press
		case MotionEvent.ACTION_POINTER_DOWN:
			float x1 = event.getX(1);
			float y1 = event.getY(1);
			if (!isFire) {
				if (x1 > 5 && x1 < 5 + buttonFire.getWidth() && y1 > 5
						&& y1 < 5 + buttonFire.getHeight()) {
					isFire = true;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (isPlaneTouched) {
				planeX = event.getX(0) + dx;
				planeY = event.getY(0) + dy;
			} else if (pointCount >= 2) {
				planeX = event.getX(1) + dx;
				planeY = event.getY(1) + dy;
			}
			break;
		default:
			return false;
		}
		return true;
		// int pointerCount = event.getPointerCount();
		// // one touch point
		// if (pointerCount == 1) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_MOVE:
		// // calculate current plane coordinate
		// if (isPlaneTouched) {
		// planeX = event.getX() + dx;
		// planeY = event.getY() + dy;
		// return outsideScreen(planeX, planeY, ownPlane);
		// return true;
		// }
		// case MotionEvent.ACTION_UP:
		// isPlaneTouched = false;
		// isFire = false;
		// return true;
		// case MotionEvent.ACTION_DOWN:
		// float x = event.getX();
		// float y = event.getY();
		// // judge whether click fire button
		// if (x > 5 && x < 5 + buttonFire.getWidth() && y > 5
		// && y < 5 + buttonFire.getHeight()) {
		// isPlaneTouched = false;
		// isFire = true;
		// } else {
		// isPlaneTouched = true;
		// isFire = false;
		// // calculate the distance between plane and touch point
		// dx = planeX - x;
		// dy = planeY - y;
		// }
		// return true;
		// default:
		// return false;
		// }
		// }
		// // two touch point
		// if (pointerCount == 2) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_POINTER_INDEX_MASK:
		//
		// break;
		// case MotionEvent.act
		// case MotionEvent.ACTION_POINTER_2_DOWN:
		// default:
		// break;
		// }
		// }
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
