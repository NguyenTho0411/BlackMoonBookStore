package com.shopme.review;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.book.BookService;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.exception.BookNotFoundException;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.customer.CustomerService;
import com.shopme.review.vote.ReviewVoteService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class ReviewController {
	private String defaultRedirectURL = "redirect:/reviews/page/1?sortField=reviewTime&sortDir=desc";
	
	@Autowired private ReviewService reviewService;
	@Autowired private BookService bookService;
	@Autowired private CustomerService customerService;
	@Autowired private ReviewVoteService voteService;
	@GetMapping("/reviews")
	public String listFirstPage(Model model) {
		return defaultRedirectURL;
	}
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
	@GetMapping("/reviews/page/{pageNum}") 
	public String listReviewsByCustomerByPage(Model model, HttpServletRequest request,
							@PathVariable(name = "pageNum") int pageNum,
							String keyword, String sortField, String sortDir) {
		Customer customer = getAuthenticatedCustomer(request);
		Page<Review> page = reviewService.listByCustomerByPage(customer, keyword, pageNum, sortField, sortDir);		
		List<Review> listReviews = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/reviews");
		
		model.addAttribute("listReviews", listReviews);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "reviews/reviews_customer";
	}
	
	@GetMapping("/reviews/detail/{id}")
	public String viewReview(@PathVariable("id") Integer id, Model model, 
			RedirectAttributes ra, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		try {
			Review review = reviewService.getByCustomerAndId(customer, id);
			model.addAttribute("review", review);
			
			return "reviews/review_detail_modal";
		} catch (ReviewNotFoundException ex) {
			ra.addFlashAttribute("message", ex.getMessage());
			return defaultRedirectURL;		
		}
	}
	
	@GetMapping("/ratings/{bookAlias}/page/{pageNum}") 
	public String listByProductByPage(Model model,
				@PathVariable(name = "bookAlias") String bookAlias,
				@PathVariable(name = "pageNum") int pageNum,
				String sortField, String sortDir,
				HttpServletRequest request) {
		
		Book book = null;
		
		try {
			book = bookService.getBook(bookAlias);
		} catch (BookNotFoundException ex) {
			return "error/404";
		}
		
		Page<Review> page = reviewService.listByBook(book, pageNum, sortField, sortDir);
		List<Review> listReviews = page.getContent();
		Customer customer =getAuthenticatedCustomer(request);
		if (customer != null) {
			voteService.markReviewsVotedForProductByCustomer(listReviews, book.getId(), customer.getId());
		}
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listReviews", listReviews);
		model.addAttribute("book", book);

		long startCount = (pageNum - 1) * ReviewService.REVIEWS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + ReviewService.REVIEWS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		model.addAttribute("pageTitle", "Reviews for " + book.getShortName());
		
		return "reviews/reviews_book";
	}
	
	@GetMapping("/ratings/{bookAlias}")
	public String listByProductFirstPage(@PathVariable(name = "bookAlias") String bookAlias, Model model,
			HttpServletRequest request) {
		return listByProductByPage(model, bookAlias, 1, "reviewTime", "desc", request);
	}	
//	
	@GetMapping("/write_review/book/{bookId}")
	public String showViewForm(@PathVariable("bookId") Integer bookId, Model model,
			HttpServletRequest request) {
		
		Review review = new Review();
		
		Book book = null;
		
		try {
			book = bookService.getBook(bookId);
		} catch (BookNotFoundException ex) {
			return "error/404";
		}
		
		Customer customer = getAuthenticatedCustomer(request);
		boolean customerReviewed = reviewService.didCustomerReviewProduct(customer, book.getId());
		
		if (customerReviewed) {
			model.addAttribute("customerReviewed", customerReviewed);
		} else {
			boolean customerCanReview = reviewService.canCustomerReviewProduct(customer, book.getId());
			
			if (customerCanReview) {
				model.addAttribute("customerCanReview", customerCanReview);				
			} else {
				model.addAttribute("NoReviewPermission", true);
			}
		}		
		
		model.addAttribute("book", book);
		model.addAttribute("review", review);
		
		return "reviews/review_form";
	}
	
	@PostMapping("/post_review")
	public String saveReview(Model model, Review review, Integer bookId, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		
		Book book = null;
		
		try {
			book = bookService.getBook(bookId);
		} catch (BookNotFoundException ex) {
			return "error/404";
		}
		
		review.setBook(book);
		review.setCustomer(customer);
		
		Review savedReview = reviewService.save(review);
		
		model.addAttribute("review", savedReview);
		
		return "reviews/review_done";
	}
}