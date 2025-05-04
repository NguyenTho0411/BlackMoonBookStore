package com.shopme.admin.order;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shopme.common.entity.order.OrderDetail;

public interface OrderDetailRepository extends CrudRepository<OrderDetail, Integer> {
	
	@Query("SELECT NEW com.shopme.common.entity.order.OrderDetail(d.book.category.name, d.quantity,"
			+ " d.bookCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithCategoryAndTimeBetween(Date startTime, Date endTime);
	
	@Query("SELECT NEW com.shopme.common.entity.order.OrderDetail(d.quantity, d.book.name,"
			+ " d.bookCost, d.shippingCost, d.subtotal)"
			+ " FROM OrderDetail d WHERE d.order.orderTime BETWEEN ?1 AND ?2")
	public List<OrderDetail> findWithProductAndTimeBetween(Date startTime, Date endTime);	
}