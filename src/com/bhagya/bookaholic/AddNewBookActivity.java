package com.bhagya.bookaholic;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bhagya.bookaholic.entities.Book;
import com.bhagya.bookaholic.entities.Has;

public class AddNewBookActivity extends Activity {

	// URL of the server
	private static final String URL = "http://10.1.10.35:80/webservice/";
	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	// Search EditText
	private EditText inputSearch;
	// Adapter for the ListView
	private SimpleAdapter adapter;
	// json object of books received from the server
	private JSONObject jsonBooks;
	// books in the list view
	private Book[] bookEntities;
	// Database Handler to access the SQLite database
	private DatabaseHandler db;

	// Elements in the layout
	private TextView tvEmpty;
	private ListView list;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnewbook);

		// Initialize the variables
		list = (ListView) findViewById(R.id.list);
		tvEmpty = (TextView) findViewById(android.R.id.empty);
		inputSearch = (EditText) findViewById(R.id.inputSearch);

		// Check for network connectivity
		networkCheck();

		// Enable the search filter option
		searchEnable();
	}

	// On selecting action bar icons for HOME and BACK
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Take appropriate action for each action item click
		switch (item.getItemId()) {
		// Home button
		case R.id.action_home:
			Intent intent = new Intent(AddNewBookActivity.this,
					MainMenuActivity.class);
			startActivity(intent);
			return true;
			// Back button
		case R.id.action_back:
			Intent intent_back = new Intent(AddNewBookActivity.this,
					BookListDetailsActivity.class);
			int booklist_id = getIntent().getIntExtra("booklist_id", 0);
			intent_back.putExtra("booklist_id", booklist_id);
			startActivity(intent_back);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	// Checks for network connection availability and reacts
	private void networkCheck() {
		if (isNetworkAvailable()) {
			// If the network is Available then call the task to execute
			GetBookListTask task = new GetBookListTask();
			task.execute();
		} else {
			// If the network is not available then show a dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					AddNewBookActivity.this);
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
									AddNewBookActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			// Set the empty text view
			tvEmpty = (TextView) list.getEmptyView();
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
	
	// Enable the search filter of the list
	private void searchEnable() {
		// When the text is changed
		inputSearch.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				// When user changed the Text
				AddNewBookActivity.this.adapter.getFilter().filter(cs);
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

	// The json object received from task is handled
	private void handleBookResponse() {
		if (jsonBooks == null) {
			// If the json object is not assigned a value (null) then show an error dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					AddNewBookActivity.this);
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
									AddNewBookActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			// Set the empty text
			tvEmpty = (TextView) list.getEmptyView();
			tvEmpty.setText(getString(R.string.no_items));
		} else {
			// If the json object has some value
			try {
				// ArrayList of HashMap to be sent to the list view
				ArrayList<HashMap<String, String>> bookList = new ArrayList<HashMap<String, String>>();
				// json array from the json object
				JSONArray jsonBookArray = jsonBooks.getJSONArray("books");
				bookEntities = new Book[jsonBookArray.length()];

				// Iterate through the json array and get the details required
				for (int i = 0; i < jsonBookArray.length(); i++) {
					JSONObject jsonBook = jsonBookArray.getJSONObject(i);
					int book_id = jsonBook.getInt("book_id");
					String title = jsonBook.getString("title");
					String author = jsonBook.getString("author");

					// Book entity
					Book aBook = new Book(book_id, title);
					bookEntities[i] = aBook;
					
					// HashMap to be added to the ArrayList
					HashMap<String, String> book = new HashMap<String, String>();
					book.put("title", title);
					book.put("author", author);

					// Add the book to the list
					bookList.add(book);
				}
				// Adapter sets the list view content
				adapter = new SimpleAdapter(this, bookList,
						android.R.layout.simple_list_item_2, new String[] {
								"title", "author" }, new int[] {
								android.R.id.text1, android.R.id.text2 });
				list.setAdapter(adapter);
				// Register the items in the list for the context menu
				registerForContextMenu(list);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// Option menu creation for the list items
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main_back_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	// Set the option menu content
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// Set the option menu 
		if (v.getId() == R.id.list) {
			String[] menuItems = getResources().getStringArray(
					R.array.options_addbook);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	// The options to be selected when an option in the menu is selected
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();

		// get the id of the selected book in the list
		String title = ((HashMap<String, String>) (adapter
				.getItem(info.position))).get("title").toString();
		int book_id = 0;
		// Iterate through the list to find the entity with the given value
		for (int i = 0; i < bookEntities.length; i++) {
			Book b = bookEntities[i];
			if (title.equals(b.getTitle())) {
				book_id = b.getId();
				break;
			}
		}

		// Class for the intent
		Class actClass;

		try {
			switch (menuItemIndex) {
			case 0:
				// Give the book details if this is selected
				actClass = Class
						.forName("com.bhagya.bookaholic.BookDetailsActivity");
				Intent intent = new Intent(AddNewBookActivity.this, actClass);
				intent.putExtra("book_id", book_id);
				startActivity(intent);
				break;
			case 1:
				// Add this book to the book list if this is selected
				actClass = Class
						.forName("com.bhagya.bookaholic.BookListDetailsActivity");
				saveInDatabase(book_id);
				break;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	// Add the book to this book list (check for duplicates as well)
	private void saveInDatabase(int book_id) {
		// Get the book list id from extra
		int booklist_id = getIntent().getIntExtra("booklist_id", 0);
		// DB handler
		db = new DatabaseHandler(AddNewBookActivity.this);
		// Get the list of books currently in the list
		int[] list = db.getHasList(booklist_id);

		// Iterate through the books to see for duplicates
		for (int i = 0; i < list.length; i++) {
			if (list[i] == book_id) {
				// If a duplicate os found, show a dialog
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						AddNewBookActivity.this);
				// set title
				alertDialogBuilder.setTitle("Book Already Exists");
				// set dialog message
				alertDialogBuilder
						.setMessage("You already have this book in your list!")
						.setCancelable(false)
						.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										AddNewBookActivity.this.finish();
									}
								});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				return;
			}
		}

		// HAS entity to store the book
		Has has = new Has(booklist_id, book_id);
		// Add the book
		db.addHas(has);

		// Dialog for success
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				AddNewBookActivity.this);
		// set title
		alertDialogBuilder.setTitle("Successfully Added");
		// set dialog message
		alertDialogBuilder
				.setMessage("The book was successfully added to your booklist")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						int booklist_id = getIntent().getExtras().getInt(
								"booklist_id");
						Intent intent = new Intent(AddNewBookActivity.this,
								BookListDetailsActivity.class);
						intent.putExtra("booklist_id", booklist_id);
						startActivity(intent);
					}
				});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();

	}

	// Task to be executed in the environment to get the json object of database data
	class GetBookListTask extends AsyncTask<String, String, JSONObject> {

		// Do this in background
		@Override
		protected JSONObject doInBackground(String... args) {
			JSONObject books = jsonParser.getJSONFromUrl(URL
					+ "listBooksMain.php");
			return books;
		}

		// After execution assign the result to the json
		protected void onPostExecute(JSONObject result) {
			jsonBooks = result;
			// Handle the json object
			handleBookResponse();
		}
	}
}
