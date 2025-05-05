package com.shopme.question;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.exception.BookNotFoundException;
import com.shopme.common.exception.ProductNotFoundException;
import com.shopme.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class QuestionRestController {
	@Autowired private CustomerService customerService;
	@Autowired private QuestionService questionService;
	
	@PostMapping("/post_question/{bookId}")
	public ResponseEntity<?> postQuestion(@RequestBody Question question,
			@PathVariable(name = "bookId") Integer bookId,
			HttpServletRequest request) throws  IOException, ProductNotFoundException, BookNotFoundException {
	
		Customer customerUser = getAuthenticatedCustomer(request);
		if (customerUser == null) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		questionService.saveNewQuestion(question, customerUser, bookId);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
	
}