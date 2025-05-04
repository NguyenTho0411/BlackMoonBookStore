package com.shopme.review;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.book.BookRepository;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Review;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.exception.ReviewNotFoundException;
import com.shopme.order.OrderDetailRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ReviewService {
	public static final int REVIEWS_PER_PAGE = 5;
	
	@Autowired private ReviewRepository reviewRepo;
	@Autowired private OrderDetailRepository orderDetailRepo;
	@Autowired private BookRepository bookRepo;

	public Page<Review> listByCustomerByPage(Customer customer, String keyword, int pageNum, 
			String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		if (keyword != null) {
			return reviewRepo.findByCustomer(customer.getId(), keyword, pageable);
		}
		
		return reviewRepo.findByCustomer(customer.getId(), pageable);
	}
	
	public Review getByCustomerAndId(Customer customer, Integer reviewId) throws ReviewNotFoundException {
		Review review = reviewRepo.findByCustomerAndId(customer.getId(), reviewId);
		if (review == null) 
			throw new ReviewNotFoundException("Customer doesn not have any reviews with ID " + reviewId);
		
		return review;
	}
	
	public Page<Review> list3MostVotedReviewsByBook	(Book book) {
		Sort sort = Sort.by("votes").descending();
		Pageable pageable = PageRequest.of(0, 3, sort);
		
		return reviewRepo.findByBook(book, pageable);		
	}
	
	public Page<Review> listByBook(Book book, int pageNum, String sortField, String sortDir) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending(); 
		Pageable pageable = PageRequest.of(pageNum - 1, REVIEWS_PER_PAGE, sort);
		
		return reviewRepo.findByBook(book, pageable);
	}

	public boolean didCustomerReviewProduct(Customer customer, Integer bookId) {
		Long count = reviewRepo.countByCustomerAndProduct(customer.getId(), bookId);
		return count > 0;
	}
	
	public boolean canCustomerReviewProduct(Customer customer, Integer bookId) {
		Long count = orderDetailRepo.countByProductAndCustomerAndOrderStatus(bookId, customer.getId(), OrderStatus.DELIVERED);
		return count > 0;
	}
	
	public Review save(Review review) {
		review.setReviewTime(new Date());
		Review savedReview = reviewRepo.save(review);
		
		Integer bookId = savedReview.getBook().getId();		
		bookRepo.updateReviewCountAndAverageRating(bookId);
		
		return savedReview;
	}
}