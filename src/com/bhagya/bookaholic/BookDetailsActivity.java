package com.bhagya.bookaholic;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class BookDetailsActivity extends BaseListActivity {

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	// JSON object to hold the received result
	private JSONObject jsonObject;
	// Bookshop ids in the list view
	private int[] bookshopIds;
	// Adapter to set the list view
	private SimpleAdapter adapter;
	// Elements in the layout
	private TextView tvBookTitle, tvBookAuthor, tvBookPrice;
	private String src;
	private ImageView iv;
	private Bitmap bitmap;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bookdetails);

		// Image view initialize
		iv = (ImageView) findViewById(R.id.ivBook);

		// Check network connectivity
		networkCheck();
	}

	// Checks for network connection availability and reacts
	private void networkCheck() {
		if (isNetworkAvailable()) {
			// If the network is available then get the bookshop list from the
			// database
			GetBookshopListTask task = new GetBookshopListTask();
			task.execute();
		} else {
			// If not show an error dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookDetailsActivity.this);
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
									BookDetailsActivity.this.finish();
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

	// Set the front cover of the book
	private void handleBookFrontResponse() {
		if (bitmap != null) {
			// If the bitmap has a value
			iv.setImageBitmap(bitmap);
		} else {
			// If there is no value
			iv.setImageResource(R.drawable.no_photo);
		}
	}

	// Handles the JSON object
	private void handleBookDetailResponse() {
		// check the json object
		if (jsonObject == null) {
			// If the json object is empty then show a dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookDetailsActivity.this);
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
									BookDetailsActivity.this.finish();
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
				String title = jsonObject.getString("title");
				String author = jsonObject.getString("author");
				Double price = jsonObject.getDouble("price");
				src = jsonObject.getString("front");

				// Get the book front cover
				GetBookFrontTask frontTask = new GetBookFrontTask();
				frontTask.execute();

				// set the text views in the layout
				tvBookTitle = (TextView) findViewById(R.id.tvBookTitle);
				tvBookAuthor = (TextView) findViewById(R.id.tvBookAuthor);
				tvBookPrice = (TextView) findViewById(R.id.tvBookPrice);

				tvBookTitle.setText(title);
				tvBookAuthor.setText(author);
				tvBookPrice.setText("Rs." + String.format("%.2f", price));

				// Bookshop list for the list view
				ArrayList<HashMap<String, String>> bookshopList = new ArrayList<HashMap<String, String>>();
				// JSON array of bookshops
				JSONArray jsonBookshopArray = jsonObject.getJSONObject(
						"bookshops").getJSONArray("bookshops");
				bookshopIds = new int[jsonBookshopArray.length()];

				// Iterate and get the details
				for (int i = 0; i < jsonBookshopArray.length(); i++) {
					JSONObject jsonBookshop = jsonBookshopArray
							.getJSONObject(i);
					// Bookshop details are stored
					bookshopIds[i] = jsonBookshop.getInt("bookshop_id");
					String name = jsonBookshop.getString("name");
					name = Html.fromHtml(name).toString();
					String stall = jsonBookshop.getString("stall");
					stall = Html.fromHtml(stall).toString();
					Double discount = jsonBookshop.getDouble("discount");

					// For the list view
					String stall_dis = "Stall No: "
							+ stall
							+ " | Discounted Price: Rs."
							+ String.format("%.2f", price - price * discount
									/ 100);

					// For the list view
					HashMap<String, String> bookshop = new HashMap<String, String>();
					bookshop.put("name", name);
					bookshop.put("stall_dis", stall_dis);

					// Add the book to the list
					bookshopList.add(bookshop);
				}

				// Set the list view content
				adapter = new SimpleAdapter(this, bookshopList,
						android.R.layout.simple_list_item_2, new String[] {
								"name", "stall_dis" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				setListAdapter(adapter);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// When an item in the list is selected
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		// Bookshop details are viewed when a bookshop is selected
		try {
			Class actClass = Class
					.forName("com.bhagya.bookaholic.BookshopDetailsActivity");
			Intent intent = new Intent(BookDetailsActivity.this, actClass);
			intent.putExtra("bookshop_id", bookshopIds[position]);
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

			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			int book_id = getIntent().getIntExtra("book_id", 0);
			params.add(new BasicNameValuePair("book_id", Integer
					.toString(book_id)));

			// Book details
			JSONObject bookDetail1 = jsonParser.makeHttpRequest(URLWebService
					+ "bookDetails.php", "POST", params);
			JSONObject bookDetail2 = jsonParser.makeHttpRequest(URLWebService
					+ "bookDetailsBookshops.php", "POST", params);
			try {
				bookDetail1.put("bookshops", bookDetail2);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return bookDetail1;
		}
		
		// After the task execution set the result 
		protected void onPostExecute(JSONObject result) {
			jsonObject = result;
			// Handle the result
			handleBookDetailResponse();
		}
	}
	
	// Access the server to get the front cover of the book
	class GetBookFrontTask extends AsyncTask<String, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... args) {
			try {
				// URL is set
				URL url = new URL(URLWebService + "bookfront/" + src);
				// Connection is built
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setDoInput(true);
				connection.connect();
				// Get the input stream
				InputStream input = connection.getInputStream();
				// Get the bitmap
				Bitmap bitmap = BitmapFactory.decodeStream(input);
				return bitmap;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		// After executing the task set the result and handle the response
		protected void onPostExecute(Bitmap result) {
			bitmap = result;
			handleBookFrontResponse();
		}

	}

}
