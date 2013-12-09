package com.example.views;

import com.example.gofightyourself.MainActivity;
import com.example.gofightyourself.R;
import com.example.sounds.GameSoundPool;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class EndView extends BaseView {
	private Bitmap background;
	private Bitmap title1; // win title
	private Bitmap title2; // die title
	private Bitmap buttonExit; // exit button
	private Bitmap buttonNext; // next level button
	private Bitmap buttonAgain; // play again
	private float titleX;
	private float titleY;
	private float buttonX;
	private float buttonY1;
	private float buttonY2;

	public EndView(Context context, GameSoundPool soundPool) {
		super(context, soundPool);
		this.mainActivity = (MainActivity) context;
		thread = new Thread(this);
	}

	@Override
	public void run() {
		while (threadFlag) {
			long startTime = System.currentTimeMillis();
			draw();
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
		// TODO Auto-generated method stub
		super.surfaceDestroyed(holder);
		release();
	}

	@Override
	public void initBitmap() {
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.background);
		title1 = BitmapFactory.decodeResource(getResources(), R.drawable.win);
		title2 = BitmapFactory.decodeResource(getResources(), R.drawable.lose);
		buttonNext = BitmapFactory.decodeResource(getResources(),
				R.drawable.next);
		buttonExit = BitmapFactory.decodeResource(getResources(),
				R.drawable.menu);
		buttonAgain = BitmapFactory.decodeResource(getResources(),
				R.drawable.retry);
		scaleWidth = screenWidth / background.getWidth();
		scaleHeight = screenHeight / background.getHeight();
		titleX = screenWidth / 2 - title1.getWidth() / 2;
		titleY = 50;
		buttonX = screenWidth / 2 - buttonNext.getWidth() / 2;
		buttonY1 = screenHeight / 2 - buttonNext.getHeight() / 2;
		buttonY2 = buttonY1 + buttonNext.getHeight() + 50;
	}

	@Override
	public void draw() {
		try {
			canvas = sfh.lockCanvas();
			canvas.save();
			canvas.scale(scaleWidth, scaleHeight, 0, 0);
			canvas.drawBitmap(background, 0, 0, paint);
			canvas.restore();
			if (isWin) {
				canvas.drawBitmap(title1, titleX, titleY, paint);
				canvas.drawBitmap(buttonNext, buttonX, buttonY1, paint);
			} else {
				canvas.drawBitmap(title2, titleX, titleY, paint);
				canvas.drawBitmap(buttonAgain, buttonX, buttonY1, paint);
			}
			canvas.drawBitmap(buttonExit, buttonX, buttonY2, paint);
		} catch (Exception err) {
			err.printStackTrace();
		} finally {
			if (canvas != null)
				sfh.unlockCanvasAndPost(canvas);
		}
	}

	@Override
	public void release() {
		if (!title1.isRecycled()) {
			title1.recycle();
		} else if (!title2.isRecycled()) {
			title2.recycle();
		} else if (!buttonNext.isRecycled()) {
			buttonNext.recycle();
		} else if (!buttonAgain.isRecycled()) {
			buttonAgain.recycle();
		} else if (!buttonExit.isRecycled()) {
			buttonExit.recycle();
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX();
			float y = event.getY();
			if (x > buttonX && x < buttonX + buttonNext.getWidth()
					&& y > buttonY1 && y < buttonY1 + buttonNext.getHeight()) {
				setWin(false);
				mainActivity.getHandler().sendEmptyMessage(MAIN_VIEW);
				return true;
			} else if (x > buttonX && x < buttonX + buttonExit.getWidth()
					&& y > buttonY2 && y < buttonY2 + buttonExit.getHeight()) {
				setWin(false);
				mainActivity.getHandler().sendEmptyMessage(START_VIEW);
				return true;
			}
			return false;
		default:
			return false;
		}
	}
}