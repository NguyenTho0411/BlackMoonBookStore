package com.shopme.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Book;

public interface BookRepository extends JpaRepository<Book, Integer>{

	@Query("SELECT p FROM Book p WHERE p.enabled = true "
			+ "AND (p.category.id = ?1 OR p.category.allParentIDs LIKE %?2%)"
			+ " ORDER BY p.name ASC")
	public Page<Book> listByCategory(Integer categoryId, String categoryIDMatch, Pageable pageable);

	
	
	public Book findByAlias(String alias);
	
	
	@Query(value = "SELECT * FROM Books WHERE enabled = true AND "
			+ "MATCH(name, short_description, full_description) AGAINST (?1)", 
			nativeQuery = true)
	public Page<Book> search(String keyword, Pageable pageable);
	@Query("Update Book p SET p.averageRating = COALESCE((SELECT AVG(r.rating) FROM Review r WHERE r.book.id = ?1), 0),"
			+ " p.reviewCount = (SELECT COUNT(r.id) FROM Review r WHERE r.book.id =?1) "
			+ "WHERE p.id = ?1")
	@Modifying
	public void updateReviewCountAndAverageRating(Integer bookId);	
}
