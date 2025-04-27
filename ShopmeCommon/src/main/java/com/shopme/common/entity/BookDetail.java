package com.shopme.common.entity;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "book_details")
public class BookDetail {
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(nullable = false, length = 255)
	private String name;
	
	@Column(nullable = false, length = 255)
	private String value;
	
	@ManyToOne
	@JoinColumn(name = "book_id")
	private Book book;

	public BookDetail() {
	}
	
	public BookDetail(Integer id, String name, String value, Book book) {
		super();
		this.id = id;
		this.name = name;
		this.value = value;
		this.book = book;
	}

	public BookDetail(String name, String value,  Book book) {
		this.name = name;
		this.value = value;
		this.book = book;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}



}