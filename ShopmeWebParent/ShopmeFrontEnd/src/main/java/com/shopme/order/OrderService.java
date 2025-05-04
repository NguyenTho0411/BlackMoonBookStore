package com.shopme.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.Address;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderDetail;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;
import com.shopme.common.entity.order.PaymentMethod;
import com.shopme.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 2;
	
	@Autowired
	private OrderRepository repo;
	
	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems, PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {
		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());
		if(paymentMethod.equals(PaymentMethod.PAYPAL))
		{
			newOrder.setStatus(OrderStatus.PAID);
		}else {
			newOrder.setStatus(OrderStatus.NEW);
		}

		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getBookCost());
		newOrder.setSubtotal(checkoutInfo.getBookTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());
		
		
		if(address == null) {
			newOrder.copyAddressFromCustomer();
		}else
		{
			newOrder.copyShippingAddress(address);
		}
		Set<OrderDetail> orderDetails =newOrder.getOrderDetails();
		for(CartItem cartItem : cartItems) {
			Book book = cartItem.getBook();
			
			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setBook(book);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(book.getDiscountPrice());
			orderDetail.setBookCost(book.getCost()*cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());
			orderDetails.add(orderDetail);
		}
		OrderTrack track = new OrderTrack();
		track.setOrder(newOrder);
		track.setStatus(OrderStatus.NEW);
		track.setNotes(OrderStatus.NEW.defaultDescription());
		track.setUpdatedTime(new Date());
		
		newOrder.getOrderTracks().add(track);
		return repo.save(newOrder);
	}
	public Page<Order> listForCustomerByPage(Customer customer, int pageNum, 
			String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		sort = sortDir.equals("asc") ? sort.ascending() : sort.descending();
		
		Pageable pageable = PageRequest.of(pageNum - 1, ORDERS_PER_PAGE, sort);
		
		if (keyword != null) {
			return repo.findAll(keyword, customer.getId(), pageable);
		}
		
		return repo.findAll(customer.getId(), pageable);
	}
	
	public Order getOrder(Integer id, Customer customer) {
		return repo.findByIdAndCustomer(id, customer);
	}	
	
	
	public void setOrderReturnRequest(OrderReturnRequest request, Customer customer) throws OrderNotFoundException {
		Order order = repo.findByIdAndCustomer(request.getOrderId(), customer);
		if(order == null) {
			throw new OrderNotFoundException("Order Id " + request.getOrderId()+" not found");
		}
		
		if(order.isReturnRequested())	return;
		
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(new Date());
		track.setStatus(OrderStatus.RETURN_REQUESTED);
		String notes = "Reason "+request.getReason();
		if(!"".equals(request.getNote())) {
			notes+=". "+request.getNote();
		}
		track.setNotes(notes);
		order.getOrderTracks().add(track);
		order.setStatus(OrderStatus.RETURN_REQUESTED);
		repo.save(order);
	}
}
