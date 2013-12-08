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
		// Log.d("startview", "creat..");
	}

	@Override
	public void run() {
		// super.run();
		while (threadFlag) {
			// Log.d("startview", "flag..");
			long startTime = System.currentTimeMillis();
			synchronized (sfh) {
				canvas = sfh.lockCanvas();
				// Log.d("startview", "thread..");
				// draw();

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

		// initBitmap();

		// Log.d("startview", "thread  start..");
		if (thread.isAlive()) {
			thread.start();
		} else {
			thread = new Thread(this);
			thread.start();
		}

		// Log.d("startview", "thread  start..");
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
		canvas.drawBitmap(background, 0, 0, paint);
		canvas.restore();
		canvas.save();
		canvas.drawBitmap(title, titleX, titleY, paint);
		canvas.restore();
		if (isPressStart) {
			canvas.save();
			canvas.drawBitmap(buttonStart, buttonX, buttonYStart, paint);
			canvas.restore();
		} else {
			canvas.save();
			canvas.drawBitmap(buttonStart2, buttonX, buttonYStart, paint);
			canvas.restore();
		}
		if (isPressEnd) {
			canvas.save();
			canvas.drawBitmap(buttonEnd, buttonX, buttonYEnd, paint);
			canvas.restore();
		} else {
			canvas.save();
			canvas.drawBitmap(buttonEnd2, buttonX, buttonYEnd, paint);
			canvas.restore();
		}
		if (isPressAbout) {
			canvas.save();
			canvas.drawBitmap(buttonAbout, buttonX, buttonYAbout, paint);
			canvas.restore();
		} else {
			canvas.save();
			canvas.drawBitmap(buttonAbout2, buttonX, buttonYAbout, paint);
			canvas.restore();
		}
	}

	@Override
	public void initBitmap() {
		// TODO Auto-generated method stub
		canvas = new Canvas();
		paint = new Paint();
		// background = BitmapFactory.decodeResource(getResources(),
		// R.drawable.background_start);
		// title = BitmapFactory.decodeResource(getResources(),
		// R.drawable.title);
		buttonStart = BitmapFactory.decodeResource(getResources(),
				R.drawable.pause);
		// buttonStart2 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_start2);
		// buttonEnd = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_end);
		// buttonEnd2 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_end2);
		// buttonAbout = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_about);
		// buttonAbout2 = BitmapFactory.decodeResource(getResources(),
		// R.drawable.button_about2);

		titleX = 0;
		titleY = 0;
		buttonInterval = 10;
		buttonX = this.screenWidth / 3;
		buttonYStart = titleY + title.getHeight() + buttonInterval;
		buttonYEnd = buttonYStart + buttonStart.getHeight() + buttonInterval;
		buttonYAbout = buttonYEnd + buttonEnd.getHeight() + buttonInterval;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == event.ACTION_DOWN) {
			float x = event.getX();
			float y = event.getY();
			if (x > buttonX && x < buttonX + buttonStart.getWidth()
					&& y > buttonYStart
					&& y < buttonYStart + buttonStart.getHeight()) {
				isPressStart = true;
				draw();
				mainActivity.getHandler().sendEmptyMessage(this.MAIN_VIEW);
				return true;
			} else if (x > buttonX && x < buttonX + buttonEnd.getWidth()
					&& y > buttonYEnd && y < buttonYEnd + buttonEnd.getHeight()) {
				isPressEnd = true;
				draw();
				mainActivity.getHandler().sendEmptyMessage(this.END_GAME);
				return true;
			} else if (x > buttonX && x < buttonX + buttonAbout.getWidth()
					&& y > buttonYAbout
					&& y < buttonYStart + buttonAbout.getHeight()) {
				isPressAbout = true;
				draw();
				mainActivity.getHandler().sendEmptyMessage(this.ABOUT_VIEW);
				return true;
			}
		} else if (event.getAction() == event.ACTION_UP) {
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
