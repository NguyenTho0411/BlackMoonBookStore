package com.shopme.book;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.category.CategoryService;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.Category;
import com.shopme.common.exception.BookNotFoundException;
import com.shopme.common.exception.CategoryNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class BookController {
	@Autowired
	private CategoryService categoryService;
	@Autowired
	private BookService bookService;
	
	@GetMapping("/c/{category_alias}")
	public String viewCategoryFirstPage(@PathVariable("category_alias") String alias,Model model) throws CategoryNotFoundException {
		return viewCategoryByPage(alias,1,model);
	}
	@GetMapping("/c/{category_alias}/page/{pageNum}")
	public String viewCategoryByPage(@PathVariable("category_alias") String alias,
			@PathVariable("pageNum") int pageNum,
			Model model) throws CategoryNotFoundException {
		try {
			Category category = categoryService.getCategory(alias);		
			List<Category> listCategoryParents = categoryService.getCategoryParents(category);
			
			Page<Book> pageBooks = bookService.listByCategory(pageNum, category.getId());
			List<Book> listBooks = pageBooks.getContent();
			
			long startCount = (pageNum - 1) * BookService.BOOKS_PER_PAGE + 1;
			long endCount = startCount + BookService.BOOKS_PER_PAGE - 1;
			if (endCount > pageBooks.getTotalElements()) {
				endCount = pageBooks.getTotalElements();
			}
			
			
			model.addAttribute("currentPage", pageNum);
			model.addAttribute("totalPages", pageBooks.getTotalPages());
			model.addAttribute("startCount", startCount);
			model.addAttribute("endCount", endCount);
			model.addAttribute("totalItems", pageBooks.getTotalElements());
			model.addAttribute("pageTitle", category.getName());
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("listBooks", listBooks);
			model.addAttribute("category", category);
			
			return "books_by_category";
		} catch (CategoryNotFoundException ex) {
			return "error/404";
		}
	}
	
	@GetMapping("/b/{book_alias}")
	public String viewProductDetail(@PathVariable("book_alias") String alias, Model model,
			HttpServletRequest request) throws BookNotFoundException {
			Book book = bookService.getBook(alias);
			List<Category> listCategoryParents = categoryService.getCategoryParents(book.getCategory());
			
			
			
			model.addAttribute("listCategoryParents", listCategoryParents);
			model.addAttribute("book", book);
			model.addAttribute("pageTitle", book.getShortName());
		    request.getSession(true); // Tạo session nếu chưa có
			return "book_detail";
	}
	
	@GetMapping("/search")
	public String searchFirstPage(@Param("keyword") String keyword, Model model) {
		return searchByPage(keyword,1,model);
	}
	
	@GetMapping("/search/page/{pageNum}")
	public String searchByPage(@Param("keyword") String keyword,@PathVariable("pageNum") int pageNum, Model model) {
		Page<Book> pageBooks = bookService.search(keyword, pageNum);
		List<Book> listResult = pageBooks.getContent();
		model.addAttribute("keyword", keyword);
		model.addAttribute("listResult", listResult);
		
		long startCount = (pageNum - 1) * BookService.BOOKS_PER_PAGE + 1;
		long endCount = startCount + BookService.BOOKS_PER_PAGE - 1;
		if (endCount > pageBooks.getTotalElements()) {
			endCount = pageBooks.getTotalElements();
		}
		model.addAttribute("currentPage", pageNum);
		
		model.addAttribute("totalPages", pageBooks.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", pageBooks.getTotalElements());
		model.addAttribute("pageTitle", keyword + " - Search Result");
		return "search_result";
	}
}
