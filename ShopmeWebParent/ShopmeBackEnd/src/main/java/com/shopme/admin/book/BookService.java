package com.shopme.admin.book;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.common.entity.Book;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class BookService {
	public static final int BOOKS_PER_PAGE = 5;
	@Autowired
	private BookRepository repo;
	
	public List<Book> listAll(){
		return repo.findAll();
	}
	
	public Book save(Book book) {
		if(book.getId() == null) {
			book.setCreatedTime(new Date());
		}
		else {
			repo.updateReviewCountAndAverageRating(book.getId());
		}
		
		if(book.getAlias() == null || book.getAlias().isEmpty()) {
			String defaultAlias = book.getName().replaceAll(" ","-");
			book.setAlias(defaultAlias);
		}else {
			book.setAlias(book.getAlias().replaceAll(" ", "-"));
		}
		
		Book updatedProduct = repo.save(book);
		repo.updateReviewCountAndAverageRating(updatedProduct.getId());
		
		return updatedProduct;
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
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Book bookByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (bookByName != null) return "Duplicate";
		} else {
			if (bookByName != null && bookByName.getId() != id) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
	
	public void searchBooks(int pageNum, PagingAndSortingHelper helper) {
		Pageable pageable = helper.createPageable(BOOKS_PER_PAGE, pageNum);
		String keyword = helper.getKeyword();		
		Page<Book> page = repo.searchBooksByName(keyword, pageable);		
		helper.updateModelAttributes(pageNum, page);
	}
}
