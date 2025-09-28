package com.shopme.admin.promotion;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Promotion;


@Service
public class PromotionService {


	public static final int PROMOTION_PER_PAGE = 4;
	@Autowired
	private PromotionRepository repo;

	
	public List<Promotion> listAll(){
		return repo.findAll();
	}
	public Page<Promotion> listByPage(int pageNum,String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		if(sortDir == null || sortDir.isEmpty()) {
			sort = sort.ascending();
		} else if(sortDir.equals("asc")) {
			sort =sort.ascending();
		}else if(sortDir.equals("desc")){
			sort =sort.descending();
		}
		Pageable pageable = PageRequest.of(pageNum-1, PROMOTION_PER_PAGE, sort);

		if(keyword != null ) {
			 return repo.findAll(keyword,pageable);
		}
		return repo.findAll(pageable);

	}
	public void save(Promotion promotion) {
		repo.save(promotion);
	}
	public Promotion get(Integer id) throws PromotionNotFoundException {
	    return repo.findById(id).orElseThrow(() -> 
	        new PromotionNotFoundException("Could not find any promotion with ID " + id)
	    );
	}
	public void delete(Integer id) throws PromotionNotFoundException {
	    Optional<Promotion> result = repo.findById(id);
	    if (result.isEmpty()) {
	        throw new PromotionNotFoundException("Could not find any promotion with ID " + id);
	    }

	    repo.deleteById(id);
	}

	
}

