package com.shopme.admin.category.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.AmazonS3Util;
import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.CategoryNotFoundException;
import com.shopme.admin.category.CategoryPageInfo;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.export.CategoryCsvExporter;
import com.shopme.common.entity.Category;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CategoryController {
	
	@Autowired
	private CategoryService service;
	
	@GetMapping("/categories")
	public String listAll(@Param("sortDir")String sortDir, Model model) {
			return listByPage(1,sortDir,null,model);
	}
	@GetMapping("/categories/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,@Param("sortDir")String sortDir,@Param("keyword")String keyword, Model model ) {
		if(sortDir == null || sortDir.isEmpty()) {
			sortDir = "asc";
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		CategoryPageInfo pageInfo = new CategoryPageInfo();
		List<Category> listCategories = service.listByPage(pageInfo,pageNum,sortDir,keyword);
		long startCount = (pageNum -1)* CategoryService.ROOT_CATEGORIES_PER_PAGE+1;
		long endCount = startCount +CategoryService.ROOT_CATEGORIES_PER_PAGE-1;
		if(endCount > pageInfo.getTotalElements()) {
			endCount = pageInfo.getTotalElements();
		}
		model.addAttribute("totalPages", pageInfo.getTotalPages());
		model.addAttribute("totalItems", pageInfo.getTotalElements());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", "name");
		model.addAttribute("sortField", "keyword");
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "category/categories";
	}
	@GetMapping("/categories/new")
	public String newCategory(Model model) {
		List<Category> listCategories = service.listCategoriesUsedInForm();
		model.addAttribute("category", new Category());
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("pageTitle", "Create New Category");
		return "category/category_form";
	}
	@PostMapping("/categories/save")
	public String saveCategory(Category category, @RequestParam("fileImage")
	MultipartFile multipartFile, RedirectAttributes redirectAttributes) throws IOException {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		category.setImage(fileName);
	
		Category savedCategory = service.save(category);
		String uploadDir = "../category-images/" +savedCategory.getId();
		AmazonS3Util.removeFolder(uploadDir);
		AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
		redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");
		return "redirect:/categories";
	}

	@GetMapping("/categories/edit/{id}")
	public String editCategory(@PathVariable(name="id") Integer id, Model model, RedirectAttributes ra ) throws UserNotFoundException {
		try {
			Category category = service.getCategory(id);
			List<Category> listCategories = service.listCategoriesUsedInForm();
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("category", category);
			model.addAttribute("pageTitle", "Edit Category (ID:" +id +")");
			return "category/category_form";
		}catch(CategoryNotFoundException ex) {
			ra.addFlashAttribute("message",ex.getMessage());
			return "redirect:/categories";
		}
	}
	
	@GetMapping("/categories/{id}/enabled/{status}")
	public String updateCategoryEnabledStatus(@PathVariable(name="id") Integer id, 
			@PathVariable(name="status") boolean enabled,
			Model model, RedirectAttributes ra ) throws UserNotFoundException {
			service.updateCategoryEnabledStatus(id, enabled);
			String status = enabled ? "enabled":"disabled";
			String message = "The category ID "+id+" has been "+status;
			ra.addFlashAttribute("message", message);
			return "redirect:/categories";
	}
	
	@GetMapping("/categories/delete/{id}")
	public String deleteCategory(@PathVariable(name="id") Integer id, 
			Model model, RedirectAttributes ra ) throws UserNotFoundException {
			try {
				service.delete(id);
				String categoryDir = "category-images/" + id;
				AmazonS3Util.removeFolder(categoryDir);
				
				ra.addFlashAttribute("message","The category with ID "+ id+ " has been deleted successfully");
			}catch(CategoryNotFoundException ex) {
				ra.addFlashAttribute("message",ex.getMessage());
			}
			return "redirect:/categories";
	}
	
	@GetMapping("/categories/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<Category> listCategories = service.listAll();
		CategoryCsvExporter exporter = new CategoryCsvExporter();
		exporter.export(listCategories,response);
	}
}
