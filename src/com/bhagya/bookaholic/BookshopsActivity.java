package com.bhagya.bookaholic;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bhagya.bookaholic.entities.Bookshop;

public class BookshopsActivity extends BaseListActivity {

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	// Adapter
	private SimpleAdapter adapter;
	// Search EditText
	private EditText inputSearch;
	// JSON object to hold the received result private
	JSONObject jsonBookshops;
	// bookshop ids for the list view
	private Bookshop[] bookshopEntities;
	// Empty text
	private TextView tvEmpty;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookshops);

		// Initialize
		tvEmpty = (TextView) findViewById(android.R.id.empty);
		inputSearch = (EditText) findViewById(R.id.inputSearch);

		// Check for network connectivity
		networkCheck();
		// Enable search and filter
		searchEnable();
	}

	// Check the network connectivity and reacts
	private void networkCheck() {
		if (isNetworkAvailable()) {
			// If the network is available, execute the task to get bookshops
			GetBookshopListTask task = new GetBookshopListTask();
			task.execute();
		} else {
			// If network not available then show an error message
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookshopsActivity.this);
			// set title
			alertDialogBuilder.setTitle("Network Failure");
			// set dialog message
			alertDialogBuilder
					.setMessage("Sorry! There was a network failure!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									BookshopsActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			tvEmpty = (TextView) getListView().getEmptyView();
			tvEmpty.setText(getString(R.string.no_items));
		}
	}

	// Checks the network connection availability and return a boolean
	private boolean isNetworkAvailable() {
		// Connectivity manager to get the network information of the device
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();
		boolean isAvailable = false;
		if (info != null && info.isConnected()) {
			isAvailable = true;
		}
		// return the availability
		return isAvailable;
	}

	// Enable search option for the list view
	private void searchEnable() {
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				try {
					BookshopsActivity.this.adapter.getFilter().filter(cs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	// Handle the received JSON response to get data
	private void handleBookshopResponse() {
		if (jsonBookshops == null) {
			// If the JSON result is empty show an error dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookshopsActivity.this);
			// set title
			alertDialogBuilder.setTitle("Database Error");
			// set dialog message
			alertDialogBuilder
					.setMessage(
							"Sorry! There was an error reading data from the database!")
					.setCancelable(false)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									BookshopsActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			tvEmpty = (TextView) getListView().getEmptyView();
			tvEmpty.setText(getString(R.string.no_items));
		} else {
			// If the JSON result has value then get data
			try {
				// Array List to hold the Hash Map values of bookshops
				ArrayList<HashMap<String, String>> bookshopList = new ArrayList<HashMap<String, String>>();
				// Get the JSON array of bookshops from the JSON object
				JSONArray jsonBookshopArray = jsonBookshops
						.getJSONArray("bookshops");
				bookshopEntities = new Bookshop[jsonBookshopArray.length()];

				// Iterate through the JSON array to get the bookshop details
				// to be viewed in the list view
				for (int i = 0; i < jsonBookshopArray.length(); i++) {
					// Get the details of the bookhops
					JSONObject jsonBookshop = jsonBookshopArray
							.getJSONObject(i);
					int bookshop_id = jsonBookshop.getInt("bookshop_id");
					String name = jsonBookshop.getString("bookshop_name");
					String stall = jsonBookshop.getString("stall_no");

					Bookshop aBookshop = new Bookshop(bookshop_id, name);
					bookshopEntities[i] = aBookshop;
					
					// Hash Map for a bookshop
					HashMap<String, String> bookshop = new HashMap<String, String>();
					bookshop.put("name", name);
					bookshop.put("stall", stall);

					// Add the bookshop to the list
					bookshopList.add(bookshop);
				}

				// Set the list view content using the adapter
				adapter = new SimpleAdapter(this, bookshopList,
						android.R.layout.simple_list_item_2, new String[] {
								"name", "stall" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				setListAdapter(adapter);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// When an item in the list is clicked this gets called
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		// Get the bookshop id from the selected item
		String name = ((HashMap<String, String>) (adapter
				.getItem(position))).get("name").toString();
		int bookshop_id = 0;
		// Iterate through the list to find the entity with the given value
		for (int i = 0; i < bookshopEntities.length; i++) {
			Bookshop b = bookshopEntities[i];
			if (name.equals(b.getName())) {
				bookshop_id = b.getId();
				break;
			}
		}
		
		try {
			Class actClass = Class
					.forName("com.bhagya.bookaholic.BookshopDetailsActivity");
			// Get the position of the book selected and show its details and
			// put into the intent
			Intent intent = new Intent(BookshopsActivity.this, actClass);
			intent.putExtra("bookshop_id", bookshop_id);
			// Start the activity
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Task to run in the background to access the MySQL database
	class GetBookshopListTask extends AsyncTask<String, String, JSONObject> {

		// Get a json of the database result
		@Override
		protected JSONObject doInBackground(String... args) {
			JSONObject bookshops = jsonParser.getJSONFromUrl(URLWebService
					+ "listBookshopsMain.php");
			Log.v("Bookshops", bookshops.toString());
			return bookshops;
		}

		// After the task execution set the result
		protected void onPostExecute(JSONObject result) {
			jsonBookshops = result;
			// Handle the result
			handleBookshopResponse();
		}
	}
}
