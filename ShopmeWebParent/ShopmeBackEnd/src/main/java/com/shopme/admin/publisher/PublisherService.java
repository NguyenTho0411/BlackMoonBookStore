package com.shopme.admin.publisher;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Publisher;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class PublisherService {
	public static final int BRANDS_PER_PAGE = 10;
	
	@Autowired
	private PublisherRepository repo;
	
	public List<Publisher> listAll() {
		return (List<Publisher>) repo.findAll();
	}
	
	public Page<Publisher> listByPage(int pageNum,String sortField, String sortDir, String keyword) {
		Sort sort = Sort.by(sortField);
		
		if(sortDir == null || sortDir.isEmpty()) {
			sort = sort.ascending();
		} else if(sortDir.equals("asc")) {
			sort =sort.ascending();
		}else if(sortDir.equals("desc")){
			sort =sort.descending();
		}
		Pageable pageable = PageRequest.of(pageNum-1, BRANDS_PER_PAGE, sort);

		if(keyword != null ) {
			 return repo.findAll(keyword,pageable);
		}
		return repo.findAll(pageable);

	}
	public Publisher save(Publisher publisher) {
		return repo.save(publisher);
	}
	
	public Publisher get(Integer id) throws PublisherNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new PublisherNotFoundException("Could not find any brand with ID " + id);
		}
	}
	
	public void delete(Integer id) throws PublisherNotFoundException {
		Long countById = repo.countById(id);
		
		if (countById == null || countById == 0) {
			throw new PublisherNotFoundException("Could not find any brand with ID " + id);			
		}
		
		repo.deleteById(id);
	}
	
	public String checkUnique(Integer id, String name) {
		boolean isCreatingNew = (id == null || id == 0);
		Publisher brandByName = repo.findByName(name);
		
		if (isCreatingNew) {
			if (brandByName != null) return "Duplicate";
		} else {
			if (brandByName != null && brandByName.getId() != id) {
				return "Duplicate";
			}
		}
		
		return "OK";
	}
}