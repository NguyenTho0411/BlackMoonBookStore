package com.shopme.admin.promotion;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.Promotion;


public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
	
	@Query("SELECT c FROM Promotion c WHERE c.name LIKE %?1%")
	public Page<Category> search(String keyword, Pageable pageable);
	
//	@Query("SELECT NEW Promotion(b.id,b.name) FROM Promotion b ORDER BY b.name ASC")
//	public List<Promotion> findAll();
//	
	
	@Query("SELECT b FROM Promotion b WHERE b.name LIKE %?1%")
	public Page<Promotion> findAll(String keyword, Pageable pageable);
}
