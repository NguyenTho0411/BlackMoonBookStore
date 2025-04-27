package com.shopme.admin.publisher.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.publisher.CategoryDTO;
import com.shopme.admin.publisher.PublishNotFoundRestException;
import com.shopme.admin.publisher.PublisherNotFoundException;
import com.shopme.admin.publisher.PublisherService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Publisher;

@RestController
public class PublisherRestController {

	@Autowired
	private PublisherService service;
	
	@PostMapping("/publishers/check_unique")
	public String eheckUnique(Integer id, String name) {
		return service.checkUnique(id, name);
		
	}
	
	@GetMapping("/publishers/{id}/categories")
	public List<CategoryDTO> listCategoriesByPublisher(@PathVariable(name = "id") Integer publisherId) throws PublishNotFoundRestException{
		List<CategoryDTO> listCategories = new ArrayList<>();
		try {
			Publisher publisher = service.get(publisherId);
			Set<Category> categories = publisher.getCategories();
			for(Category category : categories) {
				CategoryDTO dto = new CategoryDTO(category.getId(), category.getName());
				listCategories.add(dto);
			}
			return listCategories;
		}catch(PublisherNotFoundException ex ) {
			throw new PublishNotFoundRestException();
		}
	}
}
