package com.bhagya.bookaholic.entities;

public class Has {
	int _id;
	int bookList_id;
	int book_id;

	public Has() {
		
	}

	public Has(int bookList_id, int book_id) {
		this.bookList_id = bookList_id;
		this.book_id = book_id;
	}

	public Has(int _id, int bookList_id, int book_id) {
		this._id = _id;
		this.bookList_id = bookList_id;
		this.book_id = book_id;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public int getBookList_id() {
		return bookList_id;
	}

	public void setBookList_id(int bookList_id) {
		this.bookList_id = bookList_id;
	}

	public int getBook_id() {
		return book_id;
	}

	public void setBook_id(int book_id) {
		this.book_id = book_id;
	}

}
