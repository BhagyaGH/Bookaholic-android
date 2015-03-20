package com.bhagya.bookaholic;

import com.bhagya.bookaholic.R;

import android.app.ListActivity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

//This is the base list activity to be extended with the action bar buttons
public class BaseListActivity extends ListActivity {

    // Server URL
    static final String URLWebService = "http://10.1.10.35:80/webservice/";

    // Option menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// On selecting action bar icons
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		// Home button on the action bar gets us to the main menu
		case R.id.action_home:
			Intent intent = new Intent(BaseListActivity.this, MainMenuActivity.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

}
