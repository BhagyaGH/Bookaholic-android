package com.bhagya.bookaholic;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.bhagya.bookaholic.entities.Book;
import com.bhagya.bookaholic.entities.BookList;
import com.bhagya.bookaholic.entities.Has;

// DATABASE handler for the SQLite database
public class DatabaseHandler extends SQLiteOpenHelper {

	// All Static variables
	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "bookfair";

	// Contacts table name
	private static final String TABLE_BOOKLISTS = "booklists";
	private static final String TABLE_HAS = "has";

	// Booklists Table Columns names
	private static final String KEY_BL_ID = "id";
	private static final String KEY_BL_NAME = "name";
	private static final String KEY_BL_BUDGET = "budget";

	// Has Table Columns names
	private static final String KEY_HAS_ID = "id";
	private static final String KEY_HAS_BOOKLIST = "booklist";
	private static final String KEY_HAS_BOOK = "book";

	// Constructor
	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Tables
	@Override
	public void onCreate(SQLiteDatabase db) {
		// Booklist table
		String CREATE_BOOKLISTS_TABLE = "CREATE TABLE " + TABLE_BOOKLISTS + "("
				+ KEY_BL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_BL_NAME + " TEXT UNIQUE NOT NULL, " + KEY_BL_BUDGET
				+ " DOUBLE" + ")";
		// Has table
		String CREATE_HAS_TABLE = "CREATE TABLE " + TABLE_HAS + "("
				+ KEY_HAS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
				+ KEY_HAS_BOOKLIST + " INTEGER NOT NULL, " + KEY_HAS_BOOK
				+ " INTEGER NOT NULL," + " UNIQUE(" + KEY_HAS_BOOKLIST + ", "
				+ KEY_HAS_BOOK + ")" + ")";
		// Execute queries
		db.execSQL(CREATE_BOOKLISTS_TABLE);
		db.execSQL(CREATE_HAS_TABLE);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKLISTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_HAS);

		// Create tables again
		onCreate(db);
	}

	// Add a booklist to the database
	public void addBookList(BookList booklist) {
		// Get a writable database to insert
		SQLiteDatabase db = this.getWritableDatabase();

		// Set the values for the insertion
		ContentValues values = new ContentValues();
		values.put(KEY_BL_NAME, booklist.getName()); // Name
		values.put(KEY_BL_BUDGET, booklist.getBudget()); // Budget

		// Inserting Row
		db.insert(TABLE_BOOKLISTS, null, values);
		// Closing database connection
		db.close();
	}

	// Get a particular book list given the booklist_id
	public BookList getBookList(int id) {
		// Get a readable database
		SQLiteDatabase db = this.getReadableDatabase();
		// Execute the select query and get the result
		Cursor cursor = db.query(TABLE_BOOKLISTS, new String[] { KEY_BL_ID,
				KEY_BL_NAME, KEY_BL_BUDGET }, KEY_BL_ID + "=?",
				new String[] { String.valueOf(id) }, null, null, null, null);
		// If no record matches
		if (cursor != null)
			cursor.moveToFirst();
		// Booklist entity
		BookList booklist = new BookList(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getDouble(2));
		// return booklist
		return booklist;
	}

	// Get a particular book list given the book list name
	public BookList getBookList(String name) {
		// Get a readable database
		SQLiteDatabase db = this.getReadableDatabase();
		// Execute the select query and get the result
		Cursor cursor = db.query(TABLE_BOOKLISTS, new String[] { KEY_BL_ID,
				KEY_BL_NAME, KEY_BL_BUDGET }, KEY_BL_NAME + "=?",
				new String[] { name }, null, null, null, null);
		// If no record matches
		if (cursor != null)
			cursor.moveToFirst();
		// Booklist entity
		BookList booklist = new BookList(Integer.parseInt(cursor.getString(0)),
				cursor.getString(1), cursor.getDouble(2));
		// return contact
		return booklist;
	}

	// Get all the book lists in the database
	public List<BookList> getAllBookLists() {

		List<BookList> bookListList = new ArrayList<BookList>();

		// Select All Query for Book Lists
		String selectQuery = "SELECT  * FROM " + TABLE_BOOKLISTS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				BookList booklist = new BookList();
				booklist.set_id(Integer.parseInt(cursor.getString(0)));
				booklist.setName(cursor.getString(1));
				booklist.setBudget(cursor.getDouble(2));
				// Adding booklist to list
				bookListList.add(booklist);
			} while (cursor.moveToNext());
		}
		// return booklist list
		return bookListList;
	}

	// Delete a booklist
	public void deleteBookList(BookList booklist) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_BOOKLISTS, KEY_BL_ID + " = ?",
				new String[] { String.valueOf(booklist.get_id()) });
		// Closing database connection
		db.close();
	}

	public void addHas(Has has) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_HAS_BOOKLIST, has.getBookList_id());
		values.put(KEY_HAS_BOOK, has.getBook_id());

		// Inserting Row
		db.insert(TABLE_HAS, null, values);
		// Closing database connection
		db.close(); 
	}

	public void deleteHas(int booklist_id, int book_id) {
		// Given booklist id and book id then delete the entry from Has
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(
				TABLE_HAS,
				KEY_HAS_BOOKLIST + " = ? AND " + KEY_HAS_BOOK + " = ?",
				new String[] { String.valueOf(booklist_id),
						String.valueOf(book_id) });
		// Closing database connection
		db.close();
	}

	// Get all the has records 
	public List<Has> getAllHas() {
		List<Has> hasList = new ArrayList<Has>();
		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_HAS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				Has has = new Has();
				has.set_id(Integer.parseInt(cursor.getString(0)));
				has.setBookList_id(Integer.parseInt(cursor.getString(1)));
				has.setBook_id(Integer.parseInt(cursor.getString(2)));
				// Adding has record to list
				hasList.add(has);
			} while (cursor.moveToNext());
		}
		// return list of book lists
		return hasList;
	}

	// Get the book Ids of books in a given book list
	public int[] getHasList(int booklist_id) {
		// This returns the list of book ids of the given booklist
		SQLiteDatabase db = this.getReadableDatabase();

		// Select query to get the books from the list
		String QUERY = "SELECT book FROM " + TABLE_BOOKLISTS + ", " + TABLE_HAS
				+ " WHERE booklists.id = has.booklist AND has.booklist = "
				+ booklist_id;
		Cursor cursor = db.rawQuery(QUERY, null);

		// Array of book Ids
		int[] books = new int[cursor.getCount()];
		int i = 0;
		// Iterate through the result and fill the int array
		for (cursor.moveToFirst(); cursor.isAfterLast() == false; cursor
				.moveToNext()) {
			books[i] = Integer.parseInt(cursor.getString(0));
			Log.v("BOOK IDS", Integer.toString(books[i]));
			i++;
		}
		// Return the array of book Ids
		return books;
	}

}