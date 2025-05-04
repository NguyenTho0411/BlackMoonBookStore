package com.shopme.order;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.shopme.Utility;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.customer.CustomerService;
import com.shopme.review.ReviewService;

import jakarta.servlet.http.HttpServletRequest;



@Controller
public class OrderController {
	@Autowired private OrderService orderService;
	@Autowired private CustomerService customerService;
	@Autowired private ReviewService reviewService;
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
	@GetMapping("/orders")
	public String listFirstPage(Model model, HttpServletRequest request) {
		return listOrdersByPage(model, request, 1, "orderTime", "desc", null);
	}
	
	@GetMapping("/orders/page/{pageNum}")
	public String listOrdersByPage(Model model, HttpServletRequest request,
						@PathVariable(name = "pageNum") int pageNum,
						String sortField, String sortDir, String keyword
			) {
		Customer customer = getAuthenticatedCustomer(request);
		
		Page<Order> page = orderService.listForCustomerByPage(customer, pageNum, sortField, sortDir, keyword);
		List<Order> listOrders = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("listOrders", listOrders);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("moduleURL", "/orders");
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("categoryId", null); // để đảm bảo Thymeleaf không bị thiếu

		long startCount = (pageNum - 1) * OrderService.ORDERS_PER_PAGE + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + OrderService.ORDERS_PER_PAGE - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "orders/orders_customer";		
	}

	@GetMapping("/orders/detail/{id}")
	public String viewOrderDetails(Model model,
			@PathVariable(name = "id") Integer id, HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Order order = orderService.getOrder(id, customer);
		
		model.addAttribute("PROCESSING", OrderStatus.PROCESSING);
		model.addAttribute("PICKED", OrderStatus.PICKED);
		model.addAttribute("SHIPPING", OrderStatus.SHIPPING);
		model.addAttribute("DELIVERED", OrderStatus.DELIVERED);
		model.addAttribute("PACKAGED", OrderStatus.PACKAGED);
		model.addAttribute("RETURNED", OrderStatus.RETURNED);
		model.addAttribute("REFUNDED", OrderStatus.REFUNDED);
		model.addAttribute("order", order);
		setProductReviewableStatus(customer, order);
		
		model.addAttribute("order", order);
		return "orders/order_details_modal";
	}	
	private void setProductReviewableStatus(Customer customer, Order order) {
		Iterator<OrderDetail> iterator = order.getOrderDetails().iterator();
		
		while(iterator.hasNext()) {
			OrderDetail orderDetail = iterator.next();
			Book book = orderDetail.getBook();
			Integer productId = book.getId();
			
			boolean didCustomerReviewProduct = reviewService.didCustomerReviewProduct(customer, productId);
			book.setReviewedByCustomer(didCustomerReviewProduct);
			
			if (!didCustomerReviewProduct) {
				boolean canCustomerReviewProduct = reviewService.canCustomerReviewProduct(customer, productId);
				book.setCustomerCanReview(canCustomerReviewProduct);
			}
			
		}
	}
	

}