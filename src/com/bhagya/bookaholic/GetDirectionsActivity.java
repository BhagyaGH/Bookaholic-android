package com.bhagya.bookaholic;

import android.os.Bundle;
import android.widget.ArrayAdapter;

import com.bhagya.bookaholic.map.BookFairMap;

public class GetDirectionsActivity extends BaseListActivity {

	// Source and destination of the route
	private String source, destination;
	// Map for the book fair
	private BookFairMap map;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initialize();
	}

	// Initialize the values and set the list view of the path
	private void initialize() {
		source = getIntent().getExtras().getString("source");
		destination = getIntent().getExtras().getString("destination");
		map = new BookFairMap(source, destination);
		
		//Create the map of building "A"
		map.createMap();
		
		//Get the shortest path
		String[] path = map.getDirections();

		// Render the shortest path in a list view
		setListAdapter(new ArrayAdapter<String>(GetDirectionsActivity.this,
				android.R.layout.simple_list_item_1, path));

	}
}
