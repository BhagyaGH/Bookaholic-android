package com.bhagya.bookaholic.entities;

public class BookList {
	int _id;
	String name;
	Double budget;

	public BookList() {
		super();
	}
	
	public BookList(int _id) {
		super();
		this._id = _id;
	}

	public BookList(String name, Double budget) {
		super();
		this.name = name;
		this.budget = budget;
	}

	public BookList(int _id, String name, Double budget) {
		super();
		this._id = _id;
		this.name = name;
		this.budget = budget;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getBudget() {
		return budget;
	}

	public void setBudget(Double budget) {
		this.budget = budget;
	}

}
