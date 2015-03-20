package com.bhagya.bookaholic;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

// Main menu of the application
public class MainMenuActivity extends BaseListActivity {

	// Array of options
	private String[] listItems = { "Books", "Bookshops", "Map",
			"My Book Lists", "Exit" };

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Set the content of the list view
		setListAdapter(new ArrayAdapter<String>(MainMenuActivity.this,
				android.R.layout.simple_list_item_1, listItems));
	}

	// When an item in the list is clicked direct the application to the appropriate activity
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		try {
			Class actClass;
			Intent intent;
			// Check for user selcetion
			if (position == 3) {
				// If the user selects the Book Lists option
				actClass = Class.forName("com.bhagya.bookaholic.MyBookListsActivity");
				intent = new Intent(MainMenuActivity.this, actClass);
				startActivity(intent);
			} else if (position == 4) {
				// If the user selects Exit option
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainMenuActivity.this);
				// set title
				alertDialogBuilder.setTitle("Exit Bookaholic");
				// set dialog message
				alertDialogBuilder
						.setMessage("Do you really want to exit from Bookaholic?")
						.setCancelable(false)
						.setPositiveButton("Yes",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										Intent intent1 = new Intent(
												Intent.ACTION_MAIN);
										intent1.addCategory(Intent.CATEGORY_HOME);
										intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
										startActivity(intent1);
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
			} else {
				// If any other option is selected the corresponding activity is started
				actClass = Class.forName("com.bhagya.bookaholic."
						+ listItems[position] + "Activity");
				intent = new Intent(MainMenuActivity.this, actClass);
				// Start the activity
				startActivity(intent);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
