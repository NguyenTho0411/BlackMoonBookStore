package com.shopme.question;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.book.BookService;
import com.shopme.common.entity.Book;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.Question;
import com.shopme.common.exception.BookNotFoundException;

import com.shopme.customer.CustomerService;
import com.shopme.question.vote.QuestionVoteService;

import jakarta.servlet.http.HttpServletRequest;
@Controller
public class QuestionController {

	@Autowired private QuestionService questionService;
	@Autowired private CustomerService customerService;

	
	@Autowired private BookService bookService;
	
	@Autowired private QuestionVoteService voteService;	
	
	@GetMapping("/ask_question/{bookAlias}")
	public String askQuestion(@PathVariable(name = "bookAlias") String bookAlias) {
		return "redirect:/b/" + bookAlias + "#qa";
	}
	
	@GetMapping("/questions/{bookAlias}") 
	public String listQuestionsOfProduct(@PathVariable(name = "bookAlias") String bookAlias,
			Model model, HttpServletRequest request) throws BookNotFoundException{
		return listQuestionsOfProductByPage(model, request, bookAlias, 1, "votes", "desc");
	}
	
	@GetMapping("/questions/{bookAlias}/page/{pageNum}") 
	public String listQuestionsOfProductByPage(
				Model model, HttpServletRequest request,
				@PathVariable(name = "bookAlias") String bookAlias,
				@PathVariable(name = "pageNum") int pageNum,
				String sortField, String sortDir) throws BookNotFoundException {
		Page<Question> page = questionService.listQuestionsOfProduct(bookAlias, pageNum, sortField, sortDir);
		List<Question> listQuestions = page.getContent();
		Book book = bookService.getBook(bookAlias);
		
		Customer customer = getAuthenticatedCustomer(request);
		if (customer != null) {
			voteService.markQuestionsVotedForProductByCustomer(listQuestions, book.getId(), customer.getId());
		}				
				
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		
		model.addAttribute("listQuestions", listQuestions);
		model.addAttribute("book", book);

		long startCount = (pageNum - 1) * QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		if (page.getTotalPages() > 1) {
			model.addAttribute("pageTitle", "Page " + pageNum + " | Questions for book: " + book.getName());
		} else {
			model.addAttribute("pageTitle", "Questions for book: " + book.getName());
		}		
		
		return "book_questions";
	}	

	@GetMapping("/customer/questions") 
	public String listQuestionsByCustomer(Model model, HttpServletRequest request) {
		return listQuestionsByCustomerByPage(model, request, 1, null, "askTime", "desc");
	}
	
	@GetMapping("/customer/questions/page/{pageNum}") 
	public String listQuestionsByCustomerByPage(
				Model model, HttpServletRequest request,
				@PathVariable(name = "pageNum") int pageNum,
				String keyword, String sortField, String sortDir) {
		Customer customer = getAuthenticatedCustomer(request);
		Page<Question> page = questionService.listQuestionsByCustomer(customer, keyword, pageNum, sortField, sortDir);		
		List<Question> listQuestions = page.getContent();
		
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("totalItems", page.getTotalElements());
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
		model.addAttribute("moduleURL", "/customer/questions");
		
		model.addAttribute("listQuestions", listQuestions);

		long startCount = (pageNum - 1) * QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING + 1;
		model.addAttribute("startCount", startCount);
		
		long endCount = startCount + QuestionService.QUESTIONS_PER_PAGE_FOR_PUBLIC_LISTING - 1;
		if (endCount > page.getTotalElements()) {
			endCount = page.getTotalElements();
		}
		
		model.addAttribute("endCount", endCount);
		
		return "question/customer_questions";
	}
	
	@GetMapping("/customer/questions/detail/{id}")
	public String viewQuestion(@PathVariable("id") Integer id, Model model, RedirectAttributes ra, 
			HttpServletRequest request) {
		Customer customer = getAuthenticatedCustomer(request);
		Question question = questionService.getByCustomerAndId(customer, id);
		
		if (question != null) {	
			model.addAttribute("question", question);
			return "question/question_detail_modal";
		} else {
			ra.addFlashAttribute("message", "Could not find any question with ID " + id);
			return "redirect:/customer/questions";			
		}
	}	
	public Customer getAuthenticatedCustomer(HttpServletRequest request) {
		String email = Utility.getEmailOfAuthenticatedCustomer(request);				
		return customerService.getCustomerByEmail(email);
	}
}