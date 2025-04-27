package com.shopme.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "book_images")
public class BookImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false)
	private String name;
	
	@ManyToOne
	@JoinColumn(name= "book_id")
	private Book book;

	
	
	public BookImage(Integer id, String name, Book book) {
		super();
		this.id = id;
		this.name = name;
		this.book = book;
	}

	public BookImage(String name, Book book) {
		this.name = name;
		this.book = book;
	}

	public BookImage() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}
	
	
	@Transient
	public String getImagePath() {
		return "/book-images/"+book.getId()+"/extras/"+this.name;
	}
	
}
