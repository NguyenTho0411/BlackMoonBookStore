package com.shopme.admin.book;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer> {
	public Long countById(Integer id);
	
	public Book findByName(String name);
	
	@Query("UPDATE Book p SET p.enabled = ?2 WHERE p.id = ?1")
	@Modifying
	public void updateEnabledStatus(Integer id, boolean enabled);
	
	
	@Query("SELECT p FROM Book p WHERE p.name LIKE %?1% " 
			+ "OR p.shortDescription LIKE %?1% "
			+ "OR p.fullDescription LIKE %?1% "
			+ "OR p.publisher.name LIKE %?1% "
			+ "OR p.category.name LIKE %?1%")
	public Page<Book> findAll(String keyword, Pageable pageable);

	@Query("SELECT p FROM Book p WHERE p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%")	
	public Page<Book> findAllInCategory(Integer categoryId, String categoryIdMatch, 
			Pageable pageable);
	
	@Query("SELECT p FROM Book p WHERE (p.category.id = ?1 "
			+ "OR p.category.allParentIDs LIKE %?2%) AND "
			+ "(p.name LIKE %?3% " 
			+ "OR p.shortDescription LIKE %?3% "
			+ "OR p.fullDescription LIKE %?3% "
			+ "OR p.publisher.name LIKE %?3% "
			+ "OR p.category.name LIKE %?3%)")			
	public Page<Book> searchInCategory(Integer categoryId, String categoryIdMatch, 
			String keyword, Pageable pageable);
	
	@Query("SELECT p FROM Book p WHERE p.name LIKE %?1%")
	public Page<Book> searchBooksByName(String keyword, Pageable pageable);
	@Query("Update Book p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.book.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.book.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer bookId);

}
