	package com.shopme.promotion;
	
	import java.util.ArrayList;
	import java.util.Date;
	import java.util.List;
	
	import org.springframework.beans.factory.annotation.Autowired;
	import org.springframework.stereotype.Service;
	
	import com.shopme.common.entity.Promotion;
	
	@Service
	public class PromotionService {
	
	    @Autowired
	    private PromotionRepository promotionRepo;
	
	    public List<Promotion> getActivePromotions() {
	        List<Promotion> promotions = promotionRepo.findByEnabledTrue();
	        List<Promotion> result = new ArrayList<>();
	        Date now = new Date();
	
	        for (Promotion promo : promotions) {
	            if (promo.isActive()) {
	                result.add(promo);
	            }
	        }
	
	        return result;
	    }
	}