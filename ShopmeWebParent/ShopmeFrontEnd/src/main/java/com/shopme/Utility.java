package com.shopme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.shopme.security.oauth.CustomerOath2User;
import com.shopme.setting.CurrencySettingBag;
import com.shopme.setting.EmailSettingBag;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {
	public static String getSiteURL(HttpServletRequest request) {
		String siteURL = request.getRequestURL().toString();
		return siteURL.replace(request.getServletPath(), "");
	}
	
	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag setting) {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(setting.getHost());
		mailSender.setPort(setting.getPort());
		mailSender.setUsername(setting.getUsername());
		mailSender.setPassword(setting.getPassword());
		
		
		Properties mailProperties = new Properties();
		mailProperties.setProperty("mail.smtp.auth", setting.getSmtpAuth());
		mailProperties.setProperty("mail.smtp.starttls.enable", setting.getSmtpSecured());
		
		
		mailSender.setJavaMailProperties(mailProperties);
		return mailSender;
	}
	
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
	    Object principal = request.getUserPrincipal();
	    if(principal == null) {
	    	return null;
	    }
	    String customerEmail = null;
	    
	    // Kiểm tra nếu principal là đối tượng xác thực với UsernamePasswordAuthenticationToken hoặc RememberMeAuthenticationToken
	    if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
	        // Lấy email của khách hàng từ principal
	        customerEmail = request.getUserPrincipal().getName();
	    } else if (principal instanceof OAuth2AuthenticationToken ) {
	    	OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) principal;
	    	CustomerOath2User oauth2User = (CustomerOath2User) oauth2Token.getPrincipal();
	    	customerEmail = oauth2User.getEmail();
	    }
	    
	    // Trả về email của khách hàng hoặc null nếu không xác thực được
	    return customerEmail;
	}
	public static String formatCurrency(float amount, CurrencySettingBag settings) {
		String symbol = settings.getSymbol();
		String symbolPosition = settings.getSymbolPosition();
		String decimalPointType = settings.getDecimalPointType();
		String thousandPointType = settings.getThousandPointType();
		int decimalDigits = settings.getDecimalDigits();
		
		String pattern = symbolPosition.equals("Before price") ? symbol : "";
		pattern += "###,###";
		
		if (decimalDigits > 0) {
			pattern += ".";
			for (int count = 1; count <= decimalDigits; count++) pattern += "#";
		}
		
		pattern += symbolPosition.equals("After price") ? symbol : "";
		
		char thousandSeparator = thousandPointType.equals("POINT") ? '.' : ',';
		char decimalSeparator = decimalPointType.equals("POINT") ? '.' : ',';
		
		DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
		decimalFormatSymbols.setDecimalSeparator(decimalSeparator);
		decimalFormatSymbols.setGroupingSeparator(thousandSeparator);
		
		DecimalFormat formatter = new DecimalFormat(pattern, decimalFormatSymbols);
		
		return formatter.format(amount);
	}
}
