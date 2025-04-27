package com.shopme.admin.book;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	public Long countById(Integer id);
}
