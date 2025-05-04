package com.shopme.admin.user.controller;

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
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
	@Autowired
	private UserService service;
	
	@GetMapping("/users")
	public String listFirstPage(Model model) {
		return listByPage(1,model,"firstName","asc",null);
	}
	@GetMapping("/users/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum, Model model,
			@Param("sortField") String sortField
			,@Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> listUsers = page.getContent();
		String reverseSortDir  = sortDir.equals("asc") ? "desc" : "asc";
		
		long startCount = (pageNum -1)* UserService.USER_PER_PAGE+1;
		long endCount = startCount +UserService.USER_PER_PAGE -1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		model.addAttribute("keyword", keyword);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalPage", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listUsers", listUsers);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("sortField", sortField);
		return "users/users";
	}
	
	@GetMapping("/users/new")
	public String newUser(Model model) {
		List<Role> listRole = service.listRole();
		model.addAttribute("listRole", listRole);
		model.addAttribute("pageTitle", "Create new user");
		User user = new User();
		user.setEnabled(true);
		model.addAttribute("user", user);
		return "users/user_form";
	}
	
	
	@PostMapping("/users/save")
	public String saveUser(User user, RedirectAttributes redirectAttributes, @RequestParam("image") MultipartFile multipartFile) throws IOException {
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir ="user-photos/" +savedUser.getId();
			AmazonS3Util.removeFolder(uploadDir);
			AmazonS3Util.uploadFile(uploadDir, fileName, multipartFile.getInputStream());		
		}else {
			if(user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}

		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
		return getRedirectURLAffectedUser(user);

	}
	private String getRedirectURLAffectedUser(User user) {
		String firstPartOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword="+firstPartOfEmail;
	}
	
	@GetMapping("/users/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws UserNotFoundException {
		try {
			User user = service.getUser(id);
			List<Role> listRole = service.listRole();
			model.addAttribute("listRole", listRole);
			model.addAttribute("user",user);
			model.addAttribute("pageTitle", "Edit user (ID: " + id + ")");
			return "users/user_form";
		} catch(UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
			return"redirect:/users";
		}

	}
	@GetMapping("/users/delete/{id}")
	public String deleteUser(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes redirectAttributes) throws UserNotFoundException {
		try {
			service.delete(id);
			String userPhotosDir = "user-photos/" + id;
			AmazonS3Util.removeFolder(userPhotosDir);
			redirectAttributes.addFlashAttribute("message", "The user with ID "+id+" has been deleted successfully!");
		} catch(UserNotFoundException ex) {
			redirectAttributes.addFlashAttribute("message", ex.getMessage());
		}
		return"redirect:/users";
	}
	
	@GetMapping("/users/{id}/enabled/{status}")
	public String updateUserEnabledStatus(@PathVariable(name = "id") Integer id,
			@PathVariable(name = "status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		
		service.updateUserEnabledStatus(id, enabled);
		String status = enabled ? "enabled":"disabled";
		String message = "The user with ID: "+id+" has been "+status;
		redirectAttributes.addFlashAttribute("message",message);
		return "redirect:/users";
		
	}
	
	@GetMapping("/users/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(listUsers,response);
	}
	@GetMapping("/users/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserExcelExporter ex = new UserExcelExporter();
		ex.export(listUsers,response);
	}
	@GetMapping("/users/export/pdf")
	public void exportToPDF(HttpServletResponse response) throws IOException {
		List<User> listUsers = service.listAll();
		UserPdfExporter ex = new UserPdfExporter();
		ex.export(listUsers,response);
	}
}
