package com.shopme.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.common.exception.OrderNotFoundException;
import com.shopme.customer.CustomerService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class OrderRestController {

	@Autowired
	private OrderService orderService;
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/orders/return")
	public ResponseEntity<?> handleReturnRequest(@RequestBody OrderReturnRequest returnRequest, HttpServletRequest servletRequest){
		Customer customer = null;
		try {
			customer = getAuthenticatedCustomer(servletRequest);
		} catch (CustomerNotFoundException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>("Authentication required",HttpStatus.BAD_REQUEST);
		}
		try {
			orderService.setOrderReturnRequest(returnRequest, customer);
		} catch (OrderNotFoundException e) {
			// TODO Auto-generated catch block
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<>(new OrderReturnResponse(returnRequest.getOrderId()),HttpStatus.OK);
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if(email == null) {
			throw new CustomerNotFoundException("No authenticaterd customer");
		}
		return customerService.getCustomerByEmail(email);
	}
}
