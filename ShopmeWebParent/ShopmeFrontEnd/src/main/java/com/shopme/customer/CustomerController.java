package com.shopme.customer;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.Utility;
import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.entity.EmailSettingBag;
import com.shopme.security.CustomerUserDetails;
import com.shopme.security.oauth.CustomerOath2User;
import com.shopme.setting.SettingService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomerController {
	@Autowired
	private CustomerService service;
	
	@Autowired
	private SettingService settingService;
	
	
	@GetMapping("/register")
	public String showRegisterForm(Model model) {
		List<Country> listCountries = service.listAllCountry();
		
		model.addAttribute("listCountries", listCountries);
		model.addAttribute("pageTitle", "Customer Registeration");
		model.addAttribute("customer", new Customer());
		return "register/register_form";
	}
	
	@PostMapping("/create_customer")
	public String createCustomer(Customer customer, Model model, HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		service.registerCustomer(customer);
		sendVerificationEmail(request, customer);
		model.addAttribute("pageTitle", "Registraion Succeeded!");
		return "/register/register_success";
	}

	private void sendVerificationEmail(HttpServletRequest request, Customer customer) throws UnsupportedEncodingException, MessagingException {
		// TODO Auto-generated method stub
		EmailSettingBag emailSettings = settingService.getEmailSettings();
		JavaMailSenderImpl mailSender = Utility.prepareMailSender(emailSettings);
		
		
		String toAddress = customer.getEmail();
		String subject = emailSettings.getCustomerVerifySubject();
		String content = emailSettings.getCustomerVerifyContent();
		
		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);
		helper.setFrom(emailSettings.getFromAddress(), emailSettings.getSenderName());
		helper.setTo(toAddress);
		helper.setSubject(subject);
		
		content = content.replace("[[name]]", customer.getFullName());
		String verifyURL = Utility.getSiteURL(request)+"/verify?code="+customer.getVerificationCode();
		content = content.replace("[[URL]]", verifyURL);
		helper.setText(content,true);
		
		mailSender.send(message);
		System.out.println("To address "+toAddress);
		System.out.println("Verify URL"+verifyURL);
	}
	@GetMapping("/verify")
	public String verifyAccount(String code, Model model) {
		boolean verified = service.verify(code);
		
		return "register/" + (verified ? "verify_success" : "verify_fail");
	}
	
    @GetMapping("/account_details")
    public String viewAccountDetails(Model model, HttpServletRequest request) {
        String email = Utility.getEmailOfAuthenticatedCustomer(request);
        
        if (email == null) {
            // Xử lý trường hợp người dùng chưa đăng nhập
            return "redirect:/login";
        }

        Customer customer = service.getCustomerByEmail(email);
        if (customer == null) {
            // Xử lý trường hợp không tìm thấy khách hàng
            customer = new Customer();
            customer.setEmail(email);
            // Gán giá trị mặc định cho password nếu cần
            customer.setPassword("");
        }

        // Đảm bảo password không null cho người dùng OAuth2
        if (customer.getAuthenticationType() != AuthenticationType.DATABASE && customer.getPassword() == null) {
            customer.setPassword(""); // Giá trị rỗng cho biểu mẫu
        }

        List<Country> listCountries = service.listAllCountry();
        model.addAttribute("listCountries", listCountries);
        model.addAttribute("customer", customer);

        return "account_form";
    }

	@PostMapping("/update_account_details")
	public String updateAccountDetails(@ModelAttribute("customer") @Validated Customer customer, 
	                                   BindingResult bindingResult, HttpServletRequest request,
	                                   RedirectAttributes ra) {
	    if (bindingResult.hasErrors()) {
	        return "account_form"; // Trả về biểu mẫu nếu có lỗi xác thực
	    }

	    // Đảm bảo password không null trước khi gửi đến service
	    if (customer.getPassword() == null) {
	        customer.setPassword(""); // Gán chuỗi rỗng nếu null
	    }

	    service.update(customer);
	    updateNameForAuthenticatedCustomer(customer,request);
	    ra.addFlashAttribute("message", "Your account details have been updated!");
	    return "redirect:/account_details";
	}


	private void updateNameForAuthenticatedCustomer(Customer customer, HttpServletRequest request) {
		Object principal = request.getUserPrincipal();
		
		if (principal instanceof UsernamePasswordAuthenticationToken 
				|| principal instanceof RememberMeAuthenticationToken) {
			CustomerUserDetails userDetails = getCustomerUserDetailsObject(principal);
			Customer authenticatedCustomer = userDetails.getCustomer();
			authenticatedCustomer.setFirstName(customer.getFirstName());
			authenticatedCustomer.setLastName(customer.getLastName());
			
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
			CustomerOath2User oauth2User = (CustomerOath2User) oauth2Token.getPrincipal();
			String fullName = customer.getFirstName() + " " + customer.getLastName();
			oauth2User.setFullName(fullName);
		}		
	}
	
	private CustomerUserDetails getCustomerUserDetailsObject(Object principal) {
		CustomerUserDetails userDetails = null;
		if (principal instanceof UsernamePasswordAuthenticationToken) {
			UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		} else if (principal instanceof RememberMeAuthenticationToken) {
			RememberMeAuthenticationToken token = (RememberMeAuthenticationToken) principal;
			userDetails = (CustomerUserDetails) token.getPrincipal();
		}
		
		return userDetails;
	}


}
