package com.shopme.question.vote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;
import com.shopme.review.vote.VoteResult;
import com.shopme.review.vote.VoteType;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class QuestionVoteRestController {

	@Autowired private CustomerService customerService;
	
	@Autowired private QuestionVoteService service;
	
	@PostMapping("/vote_question/{id}/{type}")
	public VoteResult voteQuestion(@PathVariable(name = "id") Integer questionId,
			@PathVariable(name = "type") String type, HttpServletRequest request) {
		
		Customer customer = getAuthenticatedCustomer(request);
		if (customer == null) {
			return VoteResult.fail("You must login to vote the question.");
		}
		
		VoteType voteType = VoteType.valueOf(type.toUpperCase());
		
		return service.doVote(questionId, customer, voteType);		
	}
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
	
}