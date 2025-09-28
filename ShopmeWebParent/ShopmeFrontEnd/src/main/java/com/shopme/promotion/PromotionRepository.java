package com.shopme.promotion;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Category;
import com.shopme.common.entity.Promotion;



import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopme.common.entity.Promotion;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {

    List<Promotion> findByEnabledTrue(); // lấy tất cả promotion đang bật
    
    
    @Query("SELECT p FROM Promotion p JOIN p.categories c WHERE c.id = ?1 AND p.enabled = true AND CURRENT_DATE BETWEEN p.startDate AND p.endDate")
    List<Promotion> findActivePromotionsForCategory(Integer categoryId);
    
    @Query("SELECT p FROM Promotion p WHERE p.enabled = true AND CURRENT_DATE BETWEEN p.startDate AND p.endDate")
    Set<Promotion> findAllActivePromotions();
}
