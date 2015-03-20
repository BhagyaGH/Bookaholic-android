package com.bhagya.bookaholic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bhagya.bookaholic.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BookshopDetailsActivity extends BaseListActivity {

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	// JSON object to hold the received result
	private JSONObject jsonObject;
	// Book ids in the bookshop
	private int[] bookIds;
	// Adapter to set the list view
	private SimpleAdapter adapter;

	// Elements in the layout
	private Button bGetSrc, bCall;
	private TextView tvBookshopName, tvBookshopStall, tvBookshopPhone;
	// Temporary variables for the bookshop details
	private String name, stall, contact;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookshopdetails);

		// Initialize
		bGetSrc = (Button) findViewById(R.id.bGetSrc);
		bCall = (Button) findViewById(R.id.bCall);

		// Check the network
		networkCheck();
		// Enable the buttons
		buttonEnable();
	}

	// Checks for network connection availability and reacts
	private void networkCheck() {
		if (isNetworkAvailable()) {
			// If the network is available then get the bookshop list from the
			// database
			GetBookListTask task = new GetBookListTask();
			task.execute();
		} else {
			// If not show an error dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookshopDetailsActivity.this);
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
									BookshopDetailsActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
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

	// Set on click listeners to the buttons
	private void buttonEnable() {
		// Get the current location button
		bGetSrc.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// Direct to the activity to get the current location of the
					// user
					Class actClass = Class
							.forName("com.bhagya.bookaholic.GetSourceActivity");
					Intent intent = new Intent(BookshopDetailsActivity.this,
							actClass);
					intent.putExtra("stall", stall);
					startActivity(intent);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		// Set the listener to make a call to the bookshop
		bCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// Ask the user if he really wants to make the call
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						BookshopDetailsActivity.this);
				// set title
				alertDialogBuilder.setTitle("Call the Bookshop");
				// set dialog message
				alertDialogBuilder
						.setMessage(
								"Do you really want to call to " + name + "?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, make the
										// call
										Intent intent = new Intent(
												Intent.ACTION_CALL);
										intent.setData(Uri.parse("tel:"
												+ contact));
										startActivity(intent);
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										dialog.cancel();
									}
								});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
			}
		});
	}

	// Handle the received JSON response
	private void handleBookshopDetailResponse() {
		// check the json object
		if (jsonObject == null) {
			// If the json object is empty then show a dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookshopDetailsActivity.this);
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
									BookshopDetailsActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
		} else {
			// If the json object has values then get the details
			try {
				// Get the values from the json result
				name = jsonObject.getString("name");
				name = Html.fromHtml(name).toString();
				stall = jsonObject.getString("stall");
				stall = Html.fromHtml(stall).toString();
				contact = jsonObject.getString("contact");
				contact = Html.fromHtml(contact).toString();

				// Initialize layout elements
				tvBookshopName = (TextView) findViewById(R.id.tvBookshopName);
				tvBookshopStall = (TextView) findViewById(R.id.tvBookshopStall);
				tvBookshopPhone = (TextView) findViewById(R.id.tvBookshopPhone);

				// Set the values of the layout elements to be displayed
				tvBookshopName.setText(name);
				tvBookshopStall.setText(stall);
				tvBookshopPhone.setText(contact);

				// Array List to hold the books in the list
				ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();
				// Get the JSON array of books from the JSON object
				JSONArray jsonBookArray = jsonObject.getJSONObject("books")
						.getJSONArray("books");
				bookIds = new int[jsonBookArray.length()];

				// Iterate through the array to get the book details
				for (int i = 0; i < jsonBookArray.length(); i++) {
					// Get the book details
					JSONObject jsonBook = jsonBookArray.getJSONObject(i);
					bookIds[i] = jsonBook.getInt("book_id");
					String title = jsonBook.getString("title");
					title = Html.fromHtml(title).toString();
					String author = jsonBook.getString("author");
					author = Html.fromHtml(author).toString();
					Double price = jsonBook.getDouble("price");
					Double discount = jsonBook.getDouble("discount");

					// For the list view
					String auth_dis = "Author: "
							+ author
							+ " | Discounted Price: Rs."
							+ String.format("%.2f", price - price * discount
									/ 100);

					// Hash map for a book
					HashMap<String, String> book = new HashMap<String, String>();
					book.put("title", title);
					book.put("auth_dis", auth_dis);

					// Add the book to the list
					bookList.add(book);
				}

				// Adapter to set the books in the list view
				adapter = new SimpleAdapter(this, bookList,
						android.R.layout.simple_list_item_2, new String[] {
								"title", "auth_dis" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				setListAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// When an item on the list view is clicked
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Get the book details of the selected book
		try {
			Class actClass = Class
					.forName("com.bhagya.bookaholic.BookDetailsActivity");
			Intent intent = new Intent(BookshopDetailsActivity.this, actClass);
			// Put the intent with the book Id
			intent.putExtra("book_id", bookIds[position]);
			startActivity(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	// Task to run in the background to access the MySQL database
	class GetBookListTask extends AsyncTask<String, String, JSONObject> {
		// Get a json of the database result
		@Override
		protected JSONObject doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			int book_id = getIntent().getIntExtra("bookshop_id", 0);
			params.add(new BasicNameValuePair("bookshop_id", Integer
					.toString(book_id)));

			// Bookshop details - basic
			JSONObject bookshopDetail1 = jsonParser.makeHttpRequest(URLWebService
					+ "bookshopDetails.php", "POST", params);
			// Books in the bookshop
			JSONObject bookshopDetail2 = jsonParser.makeHttpRequest(URLWebService
					+ "bookshopDetailsBooks.php", "POST", params);
			try {
				// Put the book details in the bookshop details JSON
				bookshopDetail1.put("books", bookshopDetail2);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return bookshopDetail1;
		}
		
		// After the execution of the task handle the response
		protected void onPostExecute(JSONObject result) {
			jsonObject = result;
			handleBookshopDetailResponse();
		}
	}

}
