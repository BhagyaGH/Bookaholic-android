package com.bhagya.bookaholic;

import com.bhagya.bookaholic.R;

import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends BaseActivity {

	// Called at the beginning and this will set the layout view to the map of
	// building "A"
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
	}

}
