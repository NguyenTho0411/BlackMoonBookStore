package com.shopme.common.entity.order;

import com.shopme.common.entity.Book;
import com.shopme.common.entity.Category;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_details")
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private int quantity;
	private float bookCost;
	private float shippingCost;
	private float unitPrice;
	private float subtotal;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public float getBookCost() {
		return bookCost;
	}

	public void setBookCost(float bookCost) {
		this.bookCost = bookCost;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;
	
	@ManyToOne
	@JoinColumn(name = "order_id")
	private Order order;
	
	public OrderDetail() {
	}

	public OrderDetail(String categoryName, int quantity, float bookCost, float shippingCost, float subtotal) {
		this.book = new Book();
		this.book.setCategory(new Category(categoryName));
		this.quantity = quantity;
		this.bookCost = bookCost * quantity;
		this.shippingCost = shippingCost;
		this.subtotal = subtotal;
	}
	
	public OrderDetail(int quantity, String bookName, float bookCost, float shippingCost, float subtotal) {
		this.book = new Book(bookName);
		this.quantity = quantity;
		this.bookCost = bookCost * quantity;
		this.shippingCost = shippingCost;
		this.subtotal = subtotal;
	}	

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}



	public float getShippingCost() {
		return shippingCost;
	}

	public void setShippingCost(float shippingCost) {
		this.shippingCost = shippingCost;
	}

	public float getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice;
	}

	public float getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(float subtotal) {
		this.subtotal = subtotal;
	}



	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	
}