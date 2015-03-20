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

import com.bhagya.bookaholic.entities.Book;

public class BooksActivity extends BaseListActivity {

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	// Adapter
	private SimpleAdapter adapter;
	// Search EditText
	private EditText inputSearch;
	// JSON object to hold the received result
	private JSONObject jsonBooks;
	// Books for the list view
	private Book[] bookEntities;
	// Empty text
	private TextView tvEmpty;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.books);

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
			// If the network is available, execute the task to get books
			GetBookListTask task = new GetBookListTask();
			task.execute();
		} else {
			// If network not available then show an error message
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BooksActivity.this);
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
									BooksActivity.this.finish();
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

	// Enable search option for the list view
	private void searchEnable() {
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				BooksActivity.this.adapter.getFilter().filter(cs);
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
	private void handleBookResponse() {
		if (jsonBooks == null) {
			// If the JSON result is empty show an error dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BooksActivity.this);
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
									BooksActivity.this.finish();
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
				// Array List to hold the Hash Map values of books
				ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();
				// Get the JSON array of books from the JSON object
				JSONArray jsonBookArray = jsonBooks.getJSONArray("books");
				bookEntities = new Book[jsonBookArray.length()];

				// Iterate through the JSON array to get the book list details
				// to be viewed in the list view
				for (int i = 0; i < jsonBookArray.length(); i++) {
					// Get the details of the book
					JSONObject jsonBook = jsonBookArray.getJSONObject(i);
					int book_id = jsonBook.getInt("book_id");
					String title = jsonBook.getString("title");
					String author = jsonBook.getString("author");

					// Book entity
					Book aBook = new Book(book_id, title);
					bookEntities[i] = aBook;

					// Hash Map for a book
					HashMap<String, String> book = new HashMap<String, String>();
					book.put("title", title);
					book.put("author", author);

					// Add the book to the list
					bookList.add(book);
				}
				// Set the list view content using the adapter
				adapter = new SimpleAdapter(this, bookList,
						android.R.layout.simple_list_item_2, new String[] {
								"title", "author" }, new int[] {
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
		try {
			String title = ((HashMap<String, String>) (adapter
					.getItem(position))).get("title").toString();
			int book_id = 0;
			// Iterate through the list to find the entity with the given value
			for (int i = 0; i < bookEntities.length; i++) {
				Book b = bookEntities[i];
				if (title.equals(b.getTitle())) {
					book_id = b.getId();
					break;
				}
			}
			// Get the position of the book selected and show its details
			Class actClass = Class
					.forName("com.bhagya.bookaholic.BookDetailsActivity");
			Intent intent = new Intent(BooksActivity.this, actClass);
			// Put the book id to the intent
			intent.putExtra("book_id", book_id);
			// Start the intetnt
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
			JSONObject books = jsonParser.getJSONFromUrl(URLWebService
					+ "listBooksMain.php");
			Log.v("Books", books.toString());
			return books;
		}

		// After the task execution set the result
		protected void onPostExecute(JSONObject result) {
			jsonBooks = result;
			// Handle the result
			handleBookResponse();
		}
	}
}
