package com.shopme.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Customer;
import com.shopme.customer.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Lazy
	@Autowired
	private CustomerService customerService;
	
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		CustomerOath2User oauth2User = (CustomerOath2User) authentication.getPrincipal();
		String name = oauth2User.getName();
		String email = oauth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clientName  = oauth2User.getClientName();
	    if (email == null || email.isEmpty()) {
	        String id = oauth2User.getName(); // hoặc oauth2User.getAttribute("id");
	        email = id + "@facebook.com";
	    }

		Customer customer =customerService.getCustomerByEmail(email);
		AuthenticationType authenticationType =getAuthenticationType(clientName);
		if(customer == null) {
			customerService.addNewCustomerUponAuthLogin(name,email,countryCode,authenticationType);
		}else {
			customerService.updateAuthenticationType(customer, authenticationType);
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
	
	private AuthenticationType getAuthenticationType(String clientName) {
		if(clientName.equals("Google")) {
			return AuthenticationType.GOOGLE;
		} else 		if(clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		} else {
			return AuthenticationType.DATABASE;
		}
	}

}
