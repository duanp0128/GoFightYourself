package com.example.views;

import com.example.gofightyourself.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class StartView extends BaseView {
	private Bitmap background;
	private Bitmap title;
	private Bitmap buttonStart;
	private Bitmap buttonStart2;
	private Bitmap buttonEnd;
	private Bitmap buttonEnd2;
	private Bitmap buttonAbout;
	private Bitmap buttonAbout2;
	private boolean isPressStart;
	private boolean isPressEnd;
	private boolean isPressAbout;
	private float titleX; // x-coordinate of title
	private float titleY; // y-coordinate of title
	private float buttonX; // x-coordinate of button
	private float buttonYStart; // y-coordinate of buttonStart
	private float buttonYEnd;
	private float buttonYAbout;
	private float buttonInterval; // interval between buttons

	public StartView(Context context) {
		super(context);
		isPressStart = isPressEnd = isPressAbout = false;
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
	public void draw() {
		canvas.save();
		canvas.scale(scaleWidth, scaleHeight, 0, 0);
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.restore();
		canvas.drawBitmap(title, titleX, titleY, paint);
		if (!isPressStart) {
			canvas.drawBitmap(buttonStart, buttonX, buttonYStart, paint);
		} else {
			canvas.drawBitmap(buttonStart2, buttonX, buttonYStart, paint);
		}
		if (!isPressEnd) {
			canvas.drawBitmap(buttonEnd, buttonX, buttonYEnd, paint);
		} else {
			canvas.drawBitmap(buttonEnd2, buttonX, buttonYEnd, paint);
		}
		if (!isPressAbout) {
			canvas.drawBitmap(buttonAbout, buttonX, buttonYAbout, paint);
		} else {
			canvas.drawBitmap(buttonAbout2, buttonX, buttonYAbout, paint);
		}
	}

	@Override
	public void initBitmap() {
		canvas = new Canvas();
		paint = new Paint();
		background = BitmapFactory.decodeResource(getResources(),
				R.drawable.background_start);
		title = BitmapFactory.decodeResource(getResources(), R.drawable.title);
		buttonStart = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_start);
		// buttonStart2 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_start2);
		buttonEnd = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_end);
		// buttonEnd2 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_end2);
		buttonAbout = BitmapFactory.decodeResource(getResources(),
				R.drawable.button_about);
		// buttonAbout2 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_about2);
		scaleWidth = screenWidth / background.getWidth();
		scaleHeight = screenHeight / background.getHeight();
		titleX = screenWidth / 2 - title.getWidth() / 2;
		titleY = 50;
		buttonInterval = 30;
		buttonX = screenWidth / 2 - buttonStart.getWidth() / 2;
		buttonYStart = titleY + title.getHeight() + buttonInterval;
		buttonYEnd = buttonYStart + buttonStart.getHeight() + buttonInterval;
		buttonYAbout = buttonYEnd + buttonEnd.getHeight() + buttonInterval;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			if (x > buttonX && x < buttonX + buttonStart.getWidth()
					&& y > buttonYStart
					&& y < buttonYStart + buttonStart.getHeight()) {
				isPressStart = true;
				// draw();
				mainActivity.getHandler().sendEmptyMessage(this.MAIN_VIEW);
				return true;
			} else if (x > buttonX && x < buttonX + buttonEnd.getWidth()
					&& y > buttonYEnd && y < buttonYEnd + buttonEnd.getHeight()) {
				isPressEnd = true;
				// draw();
				mainActivity.getHandler().sendEmptyMessage(this.END_GAME);
				return true;
			} else if (x > buttonX && x < buttonX + buttonAbout.getWidth()
					&& y > buttonYAbout
					&& y < buttonYStart + buttonAbout.getHeight()) {
				isPressAbout = true;
				// draw();
				mainActivity.getHandler().sendEmptyMessage(this.ABOUT_VIEW);
				return true;
			}
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			isPressAbout = isPressEnd = isPressStart = false;
			return true;
		}
		return false;
	}

	@Override
	public void release() {
		// TODO Auto-generated method stub
		if (!background.isRecycled()) {
			background.recycle();
		} else if (!title.isRecycled()) {
			title.recycle();
		} else if (!buttonStart.isRecycled()) {
			buttonStart.recycle();
		} else if (buttonEnd.isRecycled()) {
			buttonEnd.recycle();
		} else if (buttonAbout.isRecycled()) {
			buttonAbout.recycle();
		}
	}
}
