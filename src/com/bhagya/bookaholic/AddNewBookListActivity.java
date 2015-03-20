package com.bhagya.bookaholic;

import java.util.List;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bhagya.bookaholic.R;
import com.bhagya.bookaholic.entities.BookList;

public class AddNewBookListActivity extends BaseActivity {

	// Database handler for the SQLite database
	private DatabaseHandler db;
	
	// Elements in the layout
	private Button bSave;
	private EditText etName, etBudget;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addnewbooklist);

		// Initialize
		etName = (EditText) findViewById(R.id.etName);
		etBudget = (EditText) findViewById(R.id.etBudget);
		
		// Set type of the budget edit field to number 
		etBudget.setInputType(InputType.TYPE_CLASS_NUMBER);

		// DB handler
		db = new DatabaseHandler(AddNewBookListActivity.this);
		
		// Button to save
		bSave = (Button) findViewById(R.id.bSave);
		// Set the on click listener
		buttonEnable();
	}

	// Enable the button to set the on click listener
	private void buttonEnable() {
		bSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// Edit texts
				String name = etName.getText().toString();
				Double budget = null;
				// Check for empty book list name
				if (etName.getText().toString().matches("")) {
					// If book list name empty show a dialog
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							AddNewBookListActivity.this);
					// set title
					alertDialogBuilder
							.setTitle("Book List Name Filed Blank");
					// set dialog message
					alertDialogBuilder
							.setMessage(
									"Please enter a name for your book list.")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// if this button is clicked, close
											// current activity
											Intent intent = new Intent(AddNewBookListActivity.this, AddNewBookListActivity.class);
											startActivity(intent);
										}
									});
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
					// show it
					alertDialog.show();
				}

				// If the budget is empty
				if (!(etBudget.getText().toString()).matches("")) {
					try {
						budget = Double.parseDouble(etBudget.getText()
								.toString());
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}
				}

				// List of all the book lists
				List<BookList> list = db.getAllBookLists();
				
				// Check if the name is duplicated and if it is then rename automatically (random number)
				boolean isDuplicate = false;
				for (int i = 0; i < list.size(); i++) {
					// Check for duplicates
					if (list.get(i).getName().equals(name)) {
						// Change the name
						name = name
								+ Integer.toString(new Random().nextInt(10));
						isDuplicate = true;
						break;
					}
				}

				// Persist the new book list in the database
				BookList booklist = new BookList(name, budget);
				db.addBookList(booklist);
				
				// Get the book list entity
				booklist = db.getBookList(name);

				// Id of the book list
				final int booklist_id = booklist.get_id();

				// If the book list name was not unique then show a dialog 
				if (isDuplicate) {
					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
							AddNewBookListActivity.this);
					// set title
					alertDialogBuilder
							.setTitle("Book List Name Already Exists");
					// set dialog message
					alertDialogBuilder
							.setMessage(
									"Your book list name has been changed as "
											+ name + ".")
							.setCancelable(false)
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog, int id) {
											// if this button is clicked, close
											// current activity
											try {
												// Show the book list details 
												Class actClass = Class
														.forName("com.bhagya.bookaholic.BookListDetailsActivity");
												Intent intent = new Intent(
														AddNewBookListActivity.this,
														actClass);
												intent.putExtra("booklist_id",
														booklist_id);
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
				} else {
					try {
						// Show the book list details newly created
						Class actClass = Class
								.forName("com.bhagya.bookaholic.BookListDetailsActivity");
						Intent intent = new Intent(AddNewBookListActivity.this,
								actClass);
						intent.putExtra("booklist_id", booklist.get_id());
						startActivity(intent);
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		});

	}

}
