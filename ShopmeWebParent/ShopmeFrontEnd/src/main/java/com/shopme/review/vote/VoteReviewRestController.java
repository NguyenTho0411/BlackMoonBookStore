package com.shopme.review.vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class VoteReviewRestController {

	@Autowired private ReviewVoteService service;
	@Autowired private CustomerService customerService;
	
	@PostMapping("/vote_review/{id}/{type}")
	public VoteResult voteReview(@PathVariable(name = "id") Integer reviewId,
			@PathVariable(name = "type") String type,
			HttpServletRequest request) {
		
		Customer customer = getAuthenticatedCustomer(request);
		
		if (customer == null) {
			return VoteResult.fail("You must login to vote the review.");
		}
		
		VoteType voteType = VoteType.valueOf(type.toUpperCase());
		return service.doVote(reviewId, customer, voteType);
	}
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
}