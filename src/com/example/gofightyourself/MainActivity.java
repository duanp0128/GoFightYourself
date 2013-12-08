package com.example.gofightyourself;

import com.example.views.*;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {
	private StartView startView;
	private MainView mainView;
	private EndView endView;
	// private WinView winView;
	// private DieView dieView;
	private AboutView aboutView;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				toMainView();
				break;
			case 2:
				toEndView();
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
		startView = new StartView(this);
		setContentView(startView);
		// mainView = new MainView(this);
		// setContentView(mainView);
	}

	// show game mainview
	public void toMainView() {
		if (mainView == null) {
			mainView = new MainView(this);
			// Log.d("mainview","new");
		}
		setContentView(mainView);
		startView = null;
		endView = null;
		// winView = null;
		// dieView = null;
		aboutView = null;
	}

	// show game win view
	public void toEndView() {
		if (endView == null) {
			endView = new EndView(this);
		}
		setContentView(endView);
		mainView = null;
		startView = null;
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
		endView = null;
	}

	// end game
	public void endGame() {
		if (startView != null) {
			startView.setThreadFlag(false);
		} else if (mainView != null) {
			mainView.setThreadFlag(false);
		} else if (endView != null) {
			endView.setThreadFlag(false);
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
