package com.bhagya.bookaholic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.bhagya.bookaholic.R;
import com.bhagya.bookaholic.entities.Book;
import com.bhagya.bookaholic.entities.BookList;
import com.bhagya.bookaholic.entities.Bookshop;

public class BookListDetailsActivity extends BaseActivity {

	// JSON parser class
	private JSONParser jsonParser = new JSONParser();
	// JSON object to hold the received result
	private JSONObject jsonBooks;
	// DB handler for SQLite database
	private DatabaseHandler db;
	// list of books
	private List<Book> books;
	private ArrayList<String> book_arraylist, bookshop_arraylist;
	// Integer values to be used
	private int booklist_id, book_id;
	private int count = -1;
	// Id's of books in the book list
	private int[] bookIds;
	private ArrayList<String> bookshopStalls;
	// Booklist entity
	private BookList booklist;

	// Elements in the layout
	private TextView tvEmpty;
	private Button bAddNewBook, bGetPath, bTravel;
	private ListView list;
	private TextView tvName, tvBudget, tvEstimate;

	// Called at the beginning
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.booklistdetails);

		// Initialize
		list = (ListView) findViewById(R.id.listBooks);
		tvEmpty = (TextView) findViewById(android.R.id.empty);
		tvName = (TextView) findViewById(R.id.tvBookTitle);
		tvBudget = (TextView) findViewById(R.id.tvBookPrice);
		tvEstimate = (TextView) findViewById(R.id.tvBookAuthor);

		// DB handler
		db = new DatabaseHandler(this);

		// Check for ntwork connection
		networkCheck();

		// Buttons initialize and set on click listener
		bAddNewBook = (Button) findViewById(R.id.bAddBook);
		bTravel = (Button) findViewById(R.id.bTravel);
		buttonEnable();
	}

	// Set on click listeners to the buttons
	private void buttonEnable() {

		// Add a new book to the list
		bAddNewBook.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					// Add a new book
					Class actClass = Class
							.forName("com.bhagya.bookaholic.AddNewBookActivity");
					Intent intent = new Intent(BookListDetailsActivity.this,
							actClass);
					intent.putExtra("booklist_id", booklist_id);
					startActivity(intent);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

		// Get the travel details - bookshop and the books
		bTravel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(BookListDetailsActivity.this,
						TravelDetailsActivity.class);
				intent.putStringArrayListExtra("books", book_arraylist);
				intent.putStringArrayListExtra("bookshops", bookshop_arraylist);
				startActivity(intent);
			}
		});

	}

	// Checks for network connection availability and reacts
	private void networkCheck() {
		if (isNetworkAvailable()) {
			// If the network is available then access the database
			accessDatabase();
		} else {
			// If not show the error dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookListDetailsActivity.this);
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
									BookListDetailsActivity.this.finish();
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

	// Access the database
	private void accessDatabase() {

		// Get the book list id
		booklist_id = getIntent().getExtras().getInt("booklist_id");
		booklist = db.getBookList(booklist_id);
		// Get an integer array of books in the book list
		bookIds = db.getHasList(booklist_id);

		// Gets a json object with book details
		GetBookListDetailsTask booklistTask = new GetBookListDetailsTask();
		booklistTask.execute();
	}

	// Handle the received json object
	private void handleBookListDetailsResponse() {
		// check the json object
		if (jsonBooks == null) {
			// If the json object is empty then show a dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookListDetailsActivity.this);
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
									BookListDetailsActivity.this.finish();
								}
							});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			// show it
			alertDialog.show();
			tvEmpty = (TextView) list.getEmptyView();
			tvEmpty.setText(getString(R.string.no_items));
		} else {
			// If the json object has values then get the details
			try {
				// books array list to hold the books in the list
				books = new ArrayList<Book>();
				// for the list view
				ArrayList<HashMap<String, String>> bookListForList = new ArrayList<HashMap<String, String>>();

				// count is -1 if there are no books in the book list
				if (count == -1) {
					// Then set the name, budget as given
					tvName.setText(booklist.getName());
					if (booklist.getBudget() == null) {
						tvBudget.setText("Not given.");
					} else {
						tvBudget.setText("Rs."
								+ String.format("%.2f", booklist.getBudget()));
					}
					// Set estimate not known when n books in the list
					tvEstimate.setText("Not Available.");
				} else {
					// To keep the list of bookshops to be visited
					bookshopStalls = new ArrayList<String>();
					// To be passed to the intent
					book_arraylist = new ArrayList<String>();
					bookshop_arraylist = new ArrayList<String>();

					// Iterate through the json array to get the details
					for (int i = 0; i < count + 1; i++) {
						JSONObject json = jsonBooks.getJSONObject("book" + i);
						JSONArray jsonArray = json.getJSONArray("book_details");

						JSONObject jsonObject = jsonArray.getJSONObject(0);

						String title = jsonObject.getString("title");
						String author = jsonObject.getString("author");
						Double price = Double.parseDouble(jsonObject
								.getString("price"));

						// Book entity
						Book book = new Book(title, author, price);
						books.add(book);

						// Bookshop details (where the book is lowest in cost)
						int bookshop_id = jsonObject.getInt("bookshop_id");
						String name = jsonObject.getString("bookshop_name");
						Double discount = jsonObject.getDouble("discount");
						String stall = jsonObject.getString("stall");

						// If the bookshop already exist in the list do not add
						// it
						if (!bookshopStalls.contains(stall)) {
							bookshopStalls.add(stall);
						}

						// Bookshop entity
						Bookshop bookshop = new Bookshop(bookshop_id, name,
								discount);

						// Set the bookshop in the book
						books.get(i).setBookshop(bookshop);

						// For the list view
						String author_price = "Author: "
								+ author
								+ " | Lowest Price: "
								+ String.format("%.2f", price - price
										* discount / 100) + " | Bookshop: "
								+ name;

						HashMap<String, String> bookdetail = new HashMap<String, String>();
						bookdetail.put("title", title);
						bookdetail.put("author_price", author_price);
						// Add tne book details to the list
						bookListForList.add(bookdetail);

						book_arraylist.add(title);
						bookshop_arraylist.add(name);
					}

					// Set the book list details on the layout view
					tvName.setText(booklist.getName());
					if (booklist.getBudget() == null) {
						tvBudget.setText("Not given.");
					} else {
						tvBudget.setText("Rs."
								+ String.format("%.2f", booklist.getBudget()));
					}
					// Estimate is calculated from the available book list
					tvEstimate.setText("Rs."
							+ String.format("%.2f", calculateEstimate()));

					// Set the list on the view
					SimpleAdapter adapter = new SimpleAdapter(this,
							bookListForList,
							android.R.layout.simple_list_item_2, new String[] {
									"title", "author_price" }, new int[] {
									android.R.id.text1, android.R.id.text2 });
					list.setAdapter(adapter);
					// Register the items in the list for the context menu
					registerForContextMenu(list);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	// Calculate the extimated price of the booklist
	private double calculateEstimate() {
		Double total = new Double(0);

		// Iterate through the list to get the total
		for (int i = 0; i < books.size(); i++) {
			Double price = books.get(i).getPrice();
			Double discount = books.get(i).getBookshop().getDiscount();
			total += price - price * discount / 100;
		}

		// If there is no enough money show dialog
		if (total > booklist.getBudget()) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookListDetailsActivity.this);
			// set title
			alertDialogBuilder.setTitle("No Enough Budget");
			// set dialog message
			alertDialogBuilder
					.setMessage("Sorry! You have no enough budget!")
					.setCancelable(false)
					.setPositiveButton("OK",
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
			// If he has enough money show dialog
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					BookListDetailsActivity.this);
			// set title
			alertDialogBuilder.setTitle("More Budget");
			// set dialog message
			alertDialogBuilder
					.setMessage(
							"You can buy more books upto Rs."
									+ String.format("%.2f",
											booklist.getBudget() - total) + "!")
					.setCancelable(false)
					.setPositiveButton("OK",
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
		// return the total value of the book list cost
		return total;
	}

	// Option menu creation for the list items
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.listBooks) {
			String[] menuItems = getResources().getStringArray(
					R.array.options_booksinlist);
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	// Option to be selected when one is selected
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
				.getMenuInfo();
		int menuItemIndex = item.getItemId();

		// Selected book id
		book_id = bookIds[info.position];

		switch (menuItemIndex) {
		case 0:
			// Get the book details
			try {
				Class actClass = Class
						.forName("com.bhagya.bookaholic.BookDetailsActivity");
				Intent intent = new Intent(BookListDetailsActivity.this,
						actClass);
				// Put the book id
				intent.putExtra("book_id", book_id);
				startActivity(intent);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			break;
		case 1:
			// Delete the book from the list
			deleteFromDatabase();
			break;
		}
		return true;
	}

	// Delete the book from the book list
	private void deleteFromDatabase() {
		// Ensure that the user wants to delete the record for sure
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				BookListDetailsActivity.this);
		// set title
		alertDialogBuilder.setTitle("Are you sure?");
		// set dialog message
		alertDialogBuilder
				.setMessage(
						"Do you really want to delete from "
								+ booklist.getName() + "?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity

								// Delete the has entry with the booklist and
								// book
								db.deleteHas(booklist_id, book_id);

								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										BookListDetailsActivity.this);
								// set title
								alertDialogBuilder
										.setTitle("Successfully Deleted");
								// set dialog message
								alertDialogBuilder
										.setMessage(
												"Your book is successfully removed from "
														+ booklist.getName()
														+ "!")
										.setCancelable(false)
										.setPositiveButton(
												"OK",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														// if this button is
														// clicked, close
														// current activity
														try {
															Class actClass = Class
																	.forName("com.bhagya.bookaholic.BookListDetailsActivity");
															Intent intent = new Intent(
																	BookListDetailsActivity.this,
																	actClass);
															intent.putExtra(
																	"booklist_id",
																	booklist_id);
															startActivity(intent);
														} catch (ClassNotFoundException e) {
															e.printStackTrace();
														}
													}
												});
								// create alert dialog
								AlertDialog alertDialog = alertDialogBuilder
										.create();
								// show it
								alertDialog.show();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						dialog.cancel();
					}
				});
		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		// show it
		alertDialog.show();
	}

	// Task to get the book details from the MySQL database
	class GetBookListDetailsTask extends AsyncTask<String, String, JSONObject> {

		@Override
		protected JSONObject doInBackground(String... args) {
			// JSON object to store the details
			JSONObject jsonBookDetail = new JSONObject();

			for (int i = 0; i < bookIds.length; i++) {

				// Building Parameters
				List<NameValuePair> params = new ArrayList<NameValuePair>();

				// Add the para
				params.add(new BasicNameValuePair("book_id", Integer
						.toString(bookIds[i])));

				// get JSON
				JSONObject booklistDetail = jsonParser.makeHttpRequest(URLWebService
						+ "bookListDetail.php", "POST", params);

				try {
					jsonBookDetail.put("book" + i, booklistDetail);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// Update book count
				count = i;
			}
			return jsonBookDetail;
		}

		// After task execution set the result into the JSON object
		protected void onPostExecute(JSONObject result) {
			jsonBooks = result;
			// Handle the JSON
			handleBookListDetailsResponse();
		}
	}
}
