package com.shopme.order;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import com.shopme.checkout.CheckoutInfo;
import com.shopme.common.entity.*;
import com.shopme.common.entity.order.*;
import com.shopme.common.exception.OrderNotFoundException;

@Service
public class OrderService {
	public static final int ORDERS_PER_PAGE = 2;

	@Autowired
	private OrderRepository repo;

	public Order createOrder(Customer customer, Address address, List<CartItem> cartItems,
							 PaymentMethod paymentMethod, CheckoutInfo checkoutInfo) {

		Order newOrder = new Order();
		newOrder.setOrderTime(new Date());

		newOrder.setCustomer(customer);
		newOrder.setProductCost(checkoutInfo.getBookCost());
		newOrder.setSubtotal(checkoutInfo.getBookTotal());
		newOrder.setShippingCost(checkoutInfo.getShippingCostTotal());
		newOrder.setTax(0.0f);
		newOrder.setTotal(checkoutInfo.getPaymentTotal());
		newOrder.setPaymentMethod(paymentMethod);
		newOrder.setDeliverDate(checkoutInfo.getDeliverDate());
		newOrder.setDeliverDays(checkoutInfo.getDeliverDays());

		if (address == null) {
			newOrder.copyAddressFromCustomer();
		} else {
			newOrder.copyShippingAddress(address);
		}

		// Thêm chi tiết đơn hàng
		Set<OrderDetail> orderDetails = newOrder.getOrderDetails();
		for (CartItem cartItem : cartItems) {
			Book book = cartItem.getBook();

			OrderDetail orderDetail = new OrderDetail();
			orderDetail.setOrder(newOrder);
			orderDetail.setBook(book);
			orderDetail.setQuantity(cartItem.getQuantity());
			orderDetail.setUnitPrice(book.getDiscountPrice());
			orderDetail.setBookCost(book.getCost() * cartItem.getQuantity());
			orderDetail.setSubtotal(cartItem.getSubtotal());
			orderDetail.setShippingCost(cartItem.getShippingCost());

			orderDetails.add(orderDetail);
		}

		// Áp dụng trạng thái ban đầu dựa vào PaymentMethod
		if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
		    newOrder.setStatus(OrderStatus.PAID);

		    // Giao trách nhiệm cho State xử lý
		    OrderState paidState = new PaidState();
		    paidState.next(newOrder); // Chuyển sang PROCESSING và thêm track

		    // Track PAID
		    OrderTrack trackPaid = new OrderTrack();
		    trackPaid.setOrder(newOrder);
		    trackPaid.setStatus(OrderStatus.PAID);
		    trackPaid.setNotes("Đã thanh toán PayPal.");
		    trackPaid.setUpdatedTime(new Date());
		    newOrder.getOrderTracks().add(trackPaid);
		}
else {
		    newOrder.setStatus(OrderStatus.NEW);
		    // Track trạng thái NEW như bình thường
		    OrderTrack track = new OrderTrack();
		    track.setOrder(newOrder);
		    track.setStatus(OrderStatus.NEW);
		    track.setNotes(OrderStatus.NEW.defaultDescription());
		    track.setUpdatedTime(new Date());
		    newOrder.getOrderTracks().add(track);
		}


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
		if (order == null) {
			throw new OrderNotFoundException("Order Id " + request.getOrderId() + " not found");
		}

		if (order.isReturnRequested()) return;

		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setUpdatedTime(new Date());
		track.setStatus(OrderStatus.RETURN_REQUESTED);
		String notes = "Reason " + request.getReason();
		if (!"".equals(request.getNote())) {
			notes += ". " + request.getNote();
		}
		track.setNotes(notes);
		order.getOrderTracks().add(track);
		order.setStatus(OrderStatus.RETURN_REQUESTED);

		repo.save(order);
	}

	public void moveToNextState(Integer orderId, Customer customer) throws OrderNotFoundException {
		Order order = getOrder(orderId, customer);
		if (order == null) {
			throw new OrderNotFoundException("Order ID " + orderId + " not found.");
		}
		order.nextState();

		// Lưu track mới
		OrderTrack track = new OrderTrack();
		track.setOrder(order);
		track.setStatus(order.getStatus());
		track.setNotes("Status changed to: " + order.getStatus());
		track.setUpdatedTime(new Date());
		order.getOrderTracks().add(track);

		repo.save(order);
	}
}
