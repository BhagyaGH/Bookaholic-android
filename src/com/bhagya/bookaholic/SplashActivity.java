package com.bhagya.bookaholic;

import com.bhagya.bookaholic.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

// For the starting page of the application
public class SplashActivity extends Activity {

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		// Thread to run at the beginning of the application 
		Thread timer = new Thread() {
			// Run method for the thread
			@Override
			public void run() {
				try {
					// Sleep for a certain period
					sleep(3000);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// Start the main menu activity
					Intent intent = new Intent(SplashActivity.this, TabBar.class);
					startActivity(intent);
				}
			}
		};
		// Thread start
		timer.start();
	}
	
	// When paused the activity is finished
	@Override
	protected void onPause() {
		super.onPause();
		SplashActivity.this.finish();
	}
}
