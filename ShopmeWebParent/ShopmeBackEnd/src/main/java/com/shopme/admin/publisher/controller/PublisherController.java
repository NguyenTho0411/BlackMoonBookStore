package com.shopme.admin.publisher.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.publisher.PublisherNotFoundException;
import com.shopme.admin.publisher.PublisherService;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Publisher;

@Controller
public class PublisherController {
	private String defaultRedirectURL = "redirect:/publishers/page/1?sortField=name&sortDir=asc";
	@Autowired private PublisherService publisherService;	
	@Autowired private CategoryService categoryService;
	
	@GetMapping("/publishers")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/publishers/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,Model model, @Param("sortField")String sortField,
			@Param("sortDir")String sortDir,@Param("keyword")String keyword ) {
		Page<Publisher> page = publisherService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Publisher> listPublishers= page.getContent();
		long startCount = (pageNum -1)* PublisherService.BRANDS_PER_PAGE+1;
		long endCount = startCount +PublisherService.BRANDS_PER_PAGE-1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listPublishers", listPublishers);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "publisher/publishers";
	}
	@GetMapping("/publishers/new")
	public String newBrand(Model model) {
		List<Category> listCategories = categoryService.listCategoriesUsedInForm();
		
		model.addAttribute("listCategories", listCategories);
		model.addAttribute("publisher", new Publisher());
		model.addAttribute("pageTitle", "Create new Publihser");
		return "publisher/publisher_form";
		
	}
	
	@PostMapping("/publishers/save")
	public String savePublisher(Publisher publisher, @RequestParam("fileImage") MultipartFile multipartFile, RedirectAttributes r) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			publisher.setLogo(fileName);
			
			Publisher savedPublisher = publisherService.save(publisher);
			String uploadDir = "publisher-logos"+ savedPublisher.getId();
			
			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			
		}else{
			publisherService.save(publisher);
		}
		r.addFlashAttribute("message", "The publisher has been saved successfully");
		return "redirect:/publishers";
	}
	@GetMapping("/publishers/edit/{id}")
	public String editPublisher(@PathVariable(name = "id") Integer id, Model model,	RedirectAttributes r) throws IOException {
		try {
			Publisher publisher = publisherService.get(id);
			List<Category> listCategories = categoryService.listCategoriesUsedInForm();
			model.addAttribute("listCategories", listCategories);
			model.addAttribute("publisher", publisher);
			model.addAttribute("pageTitle", "Edit Brand (ID: " + id + ")");
			return "publisher/publisher_form";
		}
		catch(PublisherNotFoundException ex)
		{
			r.addFlashAttribute("message", ex.getMessage());
			return "redirect:/publishers";
		}
	}
	
	@GetMapping("/publishers/delete/{id}")
	public String deletePublisher(@PathVariable(name = "id") Integer id, Model model,RedirectAttributes r)  throws IOException{
		try {
			publisherService.delete(id);
				
			String categoryDir = "../publisher-logos/"+id;
			FileUploadUtil.removeDir(categoryDir);
			r.addFlashAttribute("message","The publisher with ID "+ id+ " has been deleted successfully");
		}catch(PublisherNotFoundException ex) {
			r.addFlashAttribute("message",ex.getMessage());
		}
		return "redirect:/publishers";
	}
	
}
