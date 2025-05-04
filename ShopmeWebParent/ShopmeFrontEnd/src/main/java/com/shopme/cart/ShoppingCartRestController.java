package com.shopme.cart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.Utility;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;
import com.shopme.common.exception.ShoppingCartException;
import com.shopme.customer.CustomerService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ShoppingCartRestController {
	@Autowired
	private ShoppingCartService cartService;
	
	@Autowired
	private CustomerService customerService;
	
	@PostMapping("/cart/add/{bookId}/{quantity}")
	public String addBookToCart(@PathVariable(name="bookId") Integer bookId, @PathVariable(name="quantity")Integer quantity, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			Integer updatedQuantity = cartService.addBook(bookId, quantity, customer);
			return updatedQuantity + " item(s)  of this book were added to your shopping cart!";
		} catch (CustomerNotFoundException e) {
			// TODO Auto-generated catch block
			return "You must login to add this product to cart";
		} catch (ShoppingCartException e) {
			// TODO Auto-generated catch block
			return e.getMessage();
		}
	}
	
	private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);
		if(email == null) {
			throw new CustomerNotFoundException("No authenticaterd customer");
		}
		return customerService.getCustomerByEmail(email);
	}
	
	@PostMapping("/cart/update/{bookId}/{quantity}")
	public String updateQuantity(@PathVariable(name="bookId") Integer bookId, @PathVariable(name="quantity")Integer quantity, 
			HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			float subtotal = cartService.updateQuantity(bookId, quantity, customer);
			return String.valueOf(subtotal);
		} catch (CustomerNotFoundException e) {
			// TODO Auto-generated catch block
			return "You must login to change quantity of book";
		}
	}
	
	
	@DeleteMapping("/cart/remove/{bookId}")
	public String removeBook(@PathVariable(name="bookId") Integer bookId, HttpServletRequest request) {
		try {
			Customer customer = getAuthenticatedCustomer(request);
			cartService.removeBook(bookId, customer);
			
			return "The book has been removed from your shopping cart!";
		} catch (CustomerNotFoundException e) {
			// TODO Auto-generated catch block
			return "You must login to remove book";
		}
	}
}
