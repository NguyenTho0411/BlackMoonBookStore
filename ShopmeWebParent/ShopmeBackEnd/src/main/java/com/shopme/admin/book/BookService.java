package com.shopme.admin.book;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Book;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookService {
	@Autowired
	private BookRepository repo;
	
	public List<Book> listAll(){
		return repo.findAll();
	}
	
	public Book save(Book book) {
		if(book.getId() == null) {
			book.setCreatedTime(new Date());
		}
		
		if(book.getAlias() == null || book.getAlias().isEmpty()) {
			String defaultAlias = book.getName().replaceAll(" ","-");
			book.setAlias(defaultAlias);
		}else {
			book.setAlias(book.getAlias().replaceAll(" ", "-"));
		}
		
		book.setUpdateTime(new Date());
		return repo.save(book);
	}
	
	public void delete(Integer id) throws BookNotFoundException {
		Long countById = repo.countById(id);
		
		if (countById == null || countById == 0) {
			throw new BookNotFoundException("Could not find any book with ID " + id);			
		}
		
		repo.deleteById(id);
	}	
	
	public Book get(Integer id) throws BookNotFoundException {
		try {
			return repo.findById(id).get();
		} catch(NoSuchElementException ex) {
			throw new BookNotFoundException("Could not find a book with ID "+id);
		}

	}
}
