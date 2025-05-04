package com.shopme.common.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cart_items")
public class CartItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name= "customer_id")
	private Customer customer;
	private int quantity;
	
	@Transient
	private float shippingCost;
	@ManyToOne
	@JoinColumn(name= "book_id")
	private Book book;
	
	
	
	
	public CartItem() {
	}
	public CartItem(Integer id, Customer customer, int quantity, Book book) {
		super();
		this.id = id;
		this.customer = customer;
		this.quantity = quantity;
		this.book = book;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	@Transient
	public float getShippingCost() {
		return shippingCost;
	}
	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}
	@Override
	public String toString() {
		return "CartItem [id=" + id + ", customer=" + customer + ", quantity=" + quantity + ", book=" + book + "]";
	}
	@Transient
	public float getSubtotal() {
		return book.getDiscountPrice()*quantity;
	}
	
}
