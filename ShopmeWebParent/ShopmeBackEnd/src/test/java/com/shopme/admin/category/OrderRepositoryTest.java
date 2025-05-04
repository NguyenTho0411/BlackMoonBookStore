package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import com.shopme.admin.order.OrderRepository;
import com.shopme.common.entity.order.Order;
import com.shopme.common.entity.order.OrderStatus;
import com.shopme.common.entity.order.OrderTrack;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class OrderRepositoryTest {

	@Autowired private OrderRepository repo;
	@Autowired private TestEntityManager entityManager;
	
	
	
	
	@Test
	public void testUpdateOrderTracks() {
		Integer orderId = 1;
		Order order = repo.findById(orderId).get();
		
		OrderTrack newTrack = new OrderTrack();
		newTrack.setOrder(order);
		newTrack.setUpdatedTime(new Date());
		newTrack.setStatus(OrderStatus.NEW);
		newTrack.setNotes(OrderStatus.NEW.defaultDescription());

		OrderTrack processingTrack = new OrderTrack();
		processingTrack.setOrder(order);
		processingTrack.setUpdatedTime(new Date());
		processingTrack.setStatus(OrderStatus.PROCESSING);
		processingTrack.setNotes(OrderStatus.PROCESSING.defaultDescription());
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		orderTracks.add(newTrack);
		orderTracks.add(processingTrack);
		
		Order updatedOrder = repo.save(order);
		
		assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}
	
	@Test
	public void testAddTrackWithStatusNewToOrder() {
		Integer orderId = 2;
		Order order = repo.findById(orderId).get();
		
		OrderTrack newTrack = new OrderTrack();
		newTrack.setOrder(order);
		newTrack.setUpdatedTime(new Date());
		newTrack.setStatus(OrderStatus.NEW);
		newTrack.setNotes(OrderStatus.NEW.defaultDescription());
		
		List<OrderTrack> orderTracks = order.getOrderTracks();
		orderTracks.add(newTrack);		

		Order updatedOrder = repo.save(order);
		
		assertThat(updatedOrder.getOrderTracks()).hasSizeGreaterThan(1);
	}
	
	@Test
	public void testFindByOrderTimeBetween() throws ParseException {
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Date startTime = dateFormatter.parse("2021-08-01");
		Date endTime = dateFormatter.parse("2021-08-31");
		
		List<Order> listOrders = repo.findByOrderTimeBetween(startTime, endTime);
		
		assertThat(listOrders.size()).isGreaterThan(0);
		
		for (Order order : listOrders) {
			System.out.printf("%s | %s | %.2f | %.2f | %.2f \n", 
					order.getId(), order.getOrderTime(), order.getProductCost(), 
					order.getSubtotal(), order.getTotal());
		}
	}
}