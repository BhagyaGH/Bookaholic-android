package com.bhagya.bookaholic;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.bhagya.bookaholic.R;
import com.bhagya.bookaholic.entities.BookList;

public class MyBookListsActivity extends BaseActivity {

	// Book list Ids
	private int[] booklistIds;
	// Id of the book list
	private int booklist_id;
	// DB handler for the SQLite database
	private DatabaseHandler db;
	// Elements in the layout
	private Button bAddNewList;
	private ListView list;
	private TextView tvEmpty;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mybooklists);

		// Initialize
		list = (ListView) findViewById(R.id.list);
		tvEmpty = (TextView) findViewById(android.R.id.empty);
		db = new DatabaseHandler(this);

		// Check the network connectivity
		networkCheck();

		// Button initialize and set the on click listener
		bAddNewList = (Button) findViewById(R.id.bAddNewBookList);
		buttonEnable();
	}

	// Checks for network connection availability and reacts
	private void networkCheck() {
		if (isNetworkAvailable()) {
			// If the network is available, access the SQLite database
			accessDatabase();
		} else {
			// If network not available then show an error message
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					MyBookListsActivity.this);
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
									MyBookListsActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
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

	// Set the on click listener to the button
	private void buttonEnable() {
		bAddNewList.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					// Add new book list to the memory from starting another
					// activity
					Class actClass = Class
							.forName("com.bhagya.bookaholic.AddNewBookListActivity");
					Intent intent = new Intent(MyBookListsActivity.this,
							actClass);
					// Start the activity
					startActivity(intent);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// Access the database to get the book lists in the memory
	private void accessDatabase() {
		// Get all the books in the database
		List<BookList> allBookLists = db.getAllBookLists();

		// Keep the names in the array for the list view
		String[] names = new String[allBookLists.size()];
		booklistIds = new int[allBookLists.size()];

		// Iterate through the list to get the book list data
		for (int i = 0; i < allBookLists.size(); i++) {
			BookList booklist = allBookLists.get(i);
			names[i] = booklist.getName();
			booklistIds[i] = booklist.get_id();
		}
		// Set the array adapter to set the content of the list view
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, names);
		list.setAdapter(adapter);
		// Register the list items for the context menu
		registerForContextMenu(list);
	}

	// Set the option menu content
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.list) {
			String[] menuItems = getResources().getStringArray(
					R.array.options_booklist);
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

		// get the id of the selected book list in the list
		booklist_id = booklistIds[info.position];
		Class actClass;

		try {
			// Switch to check for the selected item
			switch (menuItemIndex) {
			case 0:
				// Give details of the book list
				actClass = Class
						.forName("com.bhagya.bookaholic.BookListDetailsActivity");
				Intent intent = new Intent(MyBookListsActivity.this, actClass);
				intent.putExtra("booklist_id", booklist_id);
				startActivity(intent);
				break;
			case 1:
				// Delete the book list from memory
				// Ask the user if he really want to delete
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MyBookListsActivity.this);
				// set title
				alertDialogBuilder.setTitle("Are you sure?");
				// set dialog message
				alertDialogBuilder
						.setMessage(
								"Do you really want to delete your book list?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity

										// Delete booklist
										deleteFromDatabase(booklist_id);
									}
								})
						.setNegativeButton("No",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										dialog.cancel();
									}
								});
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				// show it
				alertDialog.show();
				break;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return true;
	}

	// Delete the book list from the database
	private void deleteFromDatabase(int booklist_id) {
		BookList booklist = new BookList();
		booklist.set_id(booklist_id);
		// Delete the book list record
		db.deleteBookList(booklist);
		
		// Show a success message from a dialog
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MyBookListsActivity.this);
		// set title
		alertDialogBuilder.setTitle("Successfully Deleted");
		// set dialog message
		alertDialogBuilder
				.setMessage("Your book list is successfully deleted!")
				.setCancelable(false)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						try {
							Class actClass = Class
									.forName("com.bhagya.bookaholic.MyBookListsActivity");
							Intent intent = new Intent(
									MyBookListsActivity.this, actClass);
							startActivity(intent);
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}
}
