package com.bhagya.bookaholic.entities;

public class Book {

	int id;
	String title;
	String author;
	Double price;
	Bookshop bookshop;

	public Book() {
		super();
	}

	public Book(int id, String title) {
		super();
		this.id = id;
		this.title = title;
	}
	
	public Book(String title, String author, Double price) {
		super();
		this.title = title;
		this.author = author;
		this.price = price;
	}

	public Book(int id, String title, String author, Double price) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.price = price;
	}

	public Book(int id, String title, String author, Double price,
			Bookshop bookshop) {
		super();
		this.id = id;
		this.title = title;
		this.author = author;
		this.price = price;
		this.bookshop = bookshop;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Bookshop getBookshop() {
		return bookshop;
	}

	public void setBookshop(Bookshop bookshop) {
		this.bookshop = bookshop;
	}

}
