package com.shopme.admin.promotion;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.category.CategoryNotFoundException;
import com.shopme.admin.category.CategoryService;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.common.entity.Category;
import com.shopme.common.entity.Promotion;

@Controller
public class PromotionController {
	private String defaultRedirectURL = "redirect:/promotions/page/1?sortField=name&sortDir=asc";
	@Autowired private PromotionService promotionService;	
	@Autowired private CategoryService categoryService;
	
	@GetMapping("/promotions")
	public String listFirstPage() {
		return defaultRedirectURL;
	}
	
	@GetMapping("/promotions/page/{pageNum}")
	public String listByPage(@PathVariable(name = "pageNum") int pageNum,Model model, @Param("sortField")String sortField,
			@Param("sortDir")String sortDir,@Param("keyword")String keyword ) {
		Page<Promotion> page = promotionService.listByPage(pageNum, sortField, sortDir, keyword);
		List<Promotion> listPromotions= page.getContent();
		long startCount = (pageNum -1)* PromotionService.PROMOTION_PER_PAGE+1;
		long endCount = startCount +PromotionService.PROMOTION_PER_PAGE-1;
		if(endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("listPromotions", listPromotions);
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("reverseSortDir", reverseSortDir);
		return "promotion/promotions";
	}
	@GetMapping("/promotions/new")
	public String newPromotion(Model model) {
	    List<Category> listCategories = categoryService.listCategoriesUsedInForm();

	    model.addAttribute("listCategories", listCategories);
	    model.addAttribute("promotion", new Promotion());
	    model.addAttribute("pageTitle", "Create New Promotion");
	    
	    return "promotion/promotion_form";
	}
	@PostMapping("/promotions/save")
	public String savePromotion(
	        @ModelAttribute("promotion") Promotion promotion,
	        @RequestParam("categories") List<Integer> categoryIds,
	        RedirectAttributes redirectAttributes) throws UserNotFoundException, CategoryNotFoundException {

	    Set<Category> selectedCategories = new HashSet<>();
	    for (Integer catId : categoryIds) {
	        Category category = categoryService.getCategory(catId); // đảm bảo phương thức này đã có
	        selectedCategories.add(category);
	    }

	    promotion.setCategories(selectedCategories);
	    promotionService.save(promotion);

	    redirectAttributes.addFlashAttribute("message", "The promotion has been saved successfully!");
	    return "redirect:/promotions";
	}
	@GetMapping("/promotions/edit/{id}")
	public String editPromotion(@PathVariable("id") Integer id,
	                            Model model,
	                            RedirectAttributes ra) {
	    try {
	        Promotion promotion = promotionService.get(id);
	        List<Category> listCategories = categoryService.listCategoriesUsedInForm();
	        
	        model.addAttribute("promotion", promotion);
	        model.addAttribute("listCategories", listCategories);
	        model.addAttribute("pageTitle", "Edit Promotion (ID: " + id + ")");
	        
	        return "promotion/promotion_form";
	    } catch (PromotionNotFoundException ex) {
	        ra.addFlashAttribute("message", ex.getMessage());
	        return "redirect:/promotions";
	    }
	}
	@GetMapping("/promotions/delete/{id}")
	public String deletePromotion(@PathVariable(name = "id") Integer id,
	                              RedirectAttributes ra) {
	    try {
	        promotionService.delete(id);
	        ra.addFlashAttribute("message", "The promotion with ID " + id + " has been deleted successfully.");
	    } catch (PromotionNotFoundException ex) {
	        ra.addFlashAttribute("message", ex.getMessage());
	    }
	    return "redirect:/promotions";
	}



}
