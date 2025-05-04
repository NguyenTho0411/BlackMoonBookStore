package com.shopme.admin.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.common.entity.Book;

@RestController
public class BookRestController {

	@Autowired private BookService service;
	
	@PostMapping("/products/check_unique")
	public String checkUnique(Integer id, String name) {
		return service.checkUnique(id, name);
	}	
	
	@GetMapping("/books/get/{id}")
	public BookDTO getBookInfo(@PathVariable("id") Integer id) 
			throws BookNotFoundException {
		Book book = service.get(id);
		return new BookDTO(book.getName(), book.getMainImagePath(), 
				book.getDiscountPrice(), book.getCost());
	}
}