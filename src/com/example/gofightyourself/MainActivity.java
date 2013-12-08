package com.example.gofightyourself;

import com.example.views.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.Menu;

public class MainActivity extends Activity {
	private StartView startView;
	private MainView mainView;
	private WinView winView;
	private DieView dieView;
	private AboutView aboutView;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				toMainView();
				break;
			case 2:
				toWinView();
				break;
			case 3:
				toDieView();
				break;
			case 4:
				toAboutView();
				break;
			case 5:
				endGame();
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// startView = new StartView(this);
		// setContentView(startView);
		mainView = new MainView(this);
		setContentView(mainView);
	}

	// show game mainview
	public void toMainView() {
		if (mainView == null) {
			mainView = new MainView(this);
		}
		setContentView(mainView);
		startView = null;
		winView = null;
		dieView = null;
		aboutView = null;
	}

	// show game win view
	public void toWinView() {
		if (winView == null) {
			winView = new WinView(this);
		}
		setContentView(winView);
		mainView = null;
		startView = null;
		dieView = null;
		aboutView = null;
	}

	// show game die view
	public void toDieView() {
		if (dieView == null) {
			dieView = new DieView(this);
		}
		setContentView(dieView);
		mainView = null;
		startView = null;
		winView = null;
		aboutView = null;
	}

	// show game about view
	public void toAboutView() {
		if (aboutView == null) {
			aboutView = new AboutView(this);
		}
		setContentView(aboutView);
		mainView = null;
		startView = null;
		winView = null;
		dieView = null;
	}

	// end game
	public void endGame() {
		if (startView != null) {
			startView.setThreadFlag(false);
		} else if (mainView != null) {
			mainView.setThreadFlag(false);
		} else if (winView != null) {
			winView.setThreadFlag(false);
		} else if (dieView != null) {
			dieView.setThreadFlag(false);
		}
		this.finish();
	}

	public Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		this.handler = handler;
	}
}
