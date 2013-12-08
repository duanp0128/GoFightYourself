package com.example.views;

import com.example.gofightyourself.MainActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class BaseView extends SurfaceView implements SurfaceHolder.Callback,
		Runnable {
	public SurfaceHolder sfh;
	public Paint paint;
	public Canvas canvas;
	public Thread thread;
	public boolean threadFlag; // flag thread run status
	public float screenWidth;
	public float screenHeight;
	public static final int TIME_IN_FRAME = 20;

	public MainActivity mainActivity;
	public final int MAIN_VIEW = 1;
	public final int WIN_VIEW = 2;
	public final int DIE_VIEW = 3;
	public final int ABOUT_VIEW = 4;
	public final int END_GAME = 5;

	public BaseView(Context context) {
		super(context);
		sfh = this.getHolder();
		sfh.addCallback(this);
		mainActivity = (MainActivity) context;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		threadFlag = true;
		screenWidth = this.getWidth();
		screenHeight = this.getHeight();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		threadFlag = false;
	}

	// set thread status
	public void setThreadFlag(boolean threadFlag) {
		this.threadFlag = threadFlag;
	}

	// initial bitmap resource
	public void initBitmap() {

	}

	// draw bitmap resource
	public void draw() {
	}

	// release bitmap resource
	public void release() {
	}
}
