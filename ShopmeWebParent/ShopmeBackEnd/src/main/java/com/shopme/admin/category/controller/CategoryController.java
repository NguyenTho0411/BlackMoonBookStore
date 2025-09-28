package com.shopme.admin.category.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.category.*;
import com.shopme.admin.user.export.CategoryCsvExporter;
import com.shopme.common.entity.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class CategoryController {

    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listAll(@Param("sortDir") String sortDir, Model model) {
        return listByPage(1, sortDir, null, model);
    }

    @GetMapping("/categories/page/{pageNum}")
    public String listByPage(@PathVariable(name = "pageNum") int pageNum,
                              @Param("sortDir") String sortDir,
                              @Param("keyword") String keyword,
                              Model model) {
        if (sortDir == null || sortDir.isEmpty()) sortDir = "asc";
        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
        CategoryPageInfo pageInfo = new CategoryPageInfo();
        List<Category> listCategories = service.listByPage(pageInfo, pageNum, sortDir, keyword);
        long startCount = (pageNum - 1) * CategoryService.ROOT_CATEGORIES_PER_PAGE + 1;
        long endCount = startCount + CategoryService.ROOT_CATEGORIES_PER_PAGE - 1;
        if (endCount > pageInfo.getTotalElements()) endCount = pageInfo.getTotalElements();

        model.addAttribute("totalPages", pageInfo.getTotalPages());
        model.addAttribute("totalItems", pageInfo.getTotalElements());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("currentPage", pageNum);
        model.addAttribute("sortField", "name");
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
    public String saveCategory(Category categoryForm,
                               @RequestParam("fileImage") MultipartFile multipartFile,
                               RedirectAttributes redirectAttributes) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());

        Category categoryToSave = (categoryForm.getId() == null) ?
                new DefaultCategoryFactory(categoryForm.getName()).createCategory() : categoryForm;

        categoryToSave.setImage(fileName);
        categoryToSave.setParent(categoryForm.getParent());

        Category savedCategory = service.save(categoryToSave);
        String uploadDir = "../category-images/" + savedCategory.getId();
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        redirectAttributes.addFlashAttribute("message", "The category has been saved successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model, RedirectAttributes ra) {
        try {
            Category category = service.getCategory(id);
            List<Category> listCategories = service.listCategoriesUsedInForm();
            model.addAttribute("listCategories", listCategories);
            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit Category (ID:" + id + ")");
            return "category/category_form";
        } catch (CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                              @PathVariable("status") boolean enabled,
                                              RedirectAttributes ra) {
        service.updateCategoryEnabledStatus(id, enabled);
        ra.addFlashAttribute("message", "The category ID " + id + " has been " + (enabled ? "enabled" : "disabled"));
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Integer id, RedirectAttributes ra) {
        try {
            service.delete(id);
            FileUploadUtil.removeDir("category-images/" + id);
            ra.addFlashAttribute("message", "The category with ID " + id + " has been deleted successfully");
        } catch (CategoryNotFoundException ex) {
            ra.addFlashAttribute("message", ex.getMessage());
        }
        return "redirect:/categories";
    }

    @GetMapping("/categories/export/csv")
    public void exportToCSV(HttpServletResponse response) throws IOException {
        List<Category> listCategories = service.listAll();
        new CategoryCsvExporter().export(listCategories, response);
    }
}