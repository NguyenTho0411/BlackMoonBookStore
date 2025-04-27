package com.shopme.cart;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Book;
import com.shopme.common.entity.CartItem;
import com.shopme.common.entity.Customer;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
	public List<CartItem> findByCustomer(Customer customer);
	
	public CartItem findByCustomerAndBook(Customer customer, Book book);
	
	@Modifying
	@Query("UPDATE CartItem c set c.quantity = ?1 where c.customer.id = ?2 AND c.book.id =?3")
	public void updateQuantity(Integer quantity, Integer customerId, Integer bookId);
	
	
	@Modifying
	@Query("DELETE FROM CartItem c where c.customer.id =?1 AND c.book.id =?2")
	public void deleteByCustomerAndBook(Integer customerId, Integer bookId);
}
