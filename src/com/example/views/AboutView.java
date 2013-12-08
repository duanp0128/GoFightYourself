package com.example.views;

import com.example.sounds.GameSoundPool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AboutView extends BaseView {
	private Bitmap buttonBack;
	private Bitmap text;
	private Bitmap background;

	public AboutView(Context context, GameSoundPool soundPool) {
		super(context, soundPool);
		thread = new Thread(this);
	}

	@Override
	public void run() {
		while (threadFlag) {
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
		// TODO Auto-generated method stub
		super.surfaceChanged(holder, format, width, height);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		super.surfaceDestroyed(holder);
		release();
	}

	@Override
	public void initBitmap() {
		canvas = new Canvas();
		paint = new Paint();
//		background = BitmapFactory.decodeResource(getResources(),
//				R.drawable.background);
//		buttonBack = BitmapFactory.decodeResource(getResources(),
//				R.drawable.buttonBack);
//		text = BitmapFactory.decodeResource(getResources(),
//				R.drawable.aboutText);
		scaleWidth = screenWidth / background.getWidth();
		scaleHeight = screenHeight / background.getHeight();
	}

	@Override
	public void draw() {
		canvas.save();
		canvas.scale(scaleWidth, scaleHeight, 0, 0);
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.restore();
		// canvas.drawBitmap(text, buttonX, buttonY2, paint);
		// canvas.drawBitmap(buttonBack, buttonX, buttonY2, paint);
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		super.release();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
//			if (x > buttonX && x < buttonX + buttonNext.getWidth()
//					&& y > buttonY1 && y < buttonY1 + buttonNext.getHeight()) {
//				setWin(false);
//				mainActivity.getHandler().sendEmptyMessage(START_VIEW);
//				return true;
//			}
			return false;
		default:
			return false;
		}
	}
}
