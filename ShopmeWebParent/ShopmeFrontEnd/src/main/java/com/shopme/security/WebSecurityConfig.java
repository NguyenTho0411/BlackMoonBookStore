	package com.shopme.security;
	
	import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.shopme.security.oauth.CustomerOath2UserService;
import com.shopme.security.oauth.OAuth2LoginSuccessHandler;
	
	@Configuration
	@EnableWebSecurity
	public class WebSecurityConfig {
	@Autowired
	private CustomerOath2UserService oauth2UserService;
	
	@Autowired
	private OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
	
	@Autowired
	private DatabaseLoginSuccessHandler databaseLoginHandler;
		@Bean
		public PasswordEncoder passwordEncoder() {
			return new BCryptPasswordEncoder();
		}
		
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		    http.authorizeHttpRequests(authorize -> authorize
		            .requestMatchers("/account_details", "/update_account_details", "/orders/**",
		                             "/cart", "/address_book/**", "/checkout", "/place_order", 
		                             "/reviews/**", "/process_paypal_order", "/write_review/**", 
		                             "/post_review").authenticated()
		            .anyRequest().permitAll()
		    )
		    .formLogin(form -> form
		            .loginPage("/login")
		            .usernameParameter("email")
		            .permitAll().successHandler(databaseLoginHandler)
		    )
		    .oauth2Login(oauth2 -> oauth2
		            .loginPage("/login")
		            .userInfoEndpoint(userInfo -> userInfo
		                .userService(oauth2UserService)
		            )
		            .successHandler(oauth2LoginSuccessHandler)  // 👈 Thêm handler tại đây
		    )
		    .logout(logout -> logout
		            .logoutUrl("/logout")  // Đường dẫn để đăng xuất
		            .logoutSuccessUrl("/login?logout")  // Chuyển hướng sau khi đăng xuất
		            .permitAll()  // Cho phép mọi người truy cập vào trang đăng xuất
		    		)
		    .rememberMe(remember -> remember
		            .key("1234567890_aBcDeFgHiJkLmNoPqRsTuVwXyZ")
		            .tokenValiditySeconds(7 * 24 * 60 * 60)
		    );

		    return http.build();
		}

	
	
	    @Bean
	    WebSecurityCustomizer configureWebSecurity() {
	        return (web) -> web.ignoring()
	                .requestMatchers("/user-photos/**", "/images/**", "/js/**", "/webjars/**", "/fontawesome/**", "/webfonts/**", "/richtext/**", "/css/**");
	    }
	    
	    
		@Bean
		public UserDetailsService userDetailsService() {
			return new CustomerUserDetailsService();
		}
		
		@Bean
		public DaoAuthenticationProvider authenticationProvider() {
			DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	
			authProvider.setUserDetailsService(userDetailsService());
			authProvider.setPasswordEncoder(passwordEncoder());
	
			return authProvider;
		}	
	
	
	
	}
