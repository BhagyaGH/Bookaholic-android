package com.bhagya.bookaholic.entities;

public class Bookshop {

	int id;
	String name;
	Double discount;
	
	public Bookshop() {
		super();
	}
	public Bookshop(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public Bookshop(String name, Double discount) {
		super();
		this.name = name;
		this.discount = discount;
	}
	public Bookshop(int id, String name, Double discount) {
		super();
		this.id = id;
		this.name = name;
		this.discount = discount;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getDiscount() {
		return discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}
	
}
