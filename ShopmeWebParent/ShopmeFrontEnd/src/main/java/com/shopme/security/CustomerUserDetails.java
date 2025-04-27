package com.shopme.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.shopme.common.entity.Customer;

public class CustomerUserDetails implements UserDetails {
	private Customer customer;
	public CustomerUserDetails(Customer customer) {
		this.customer = customer;
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		return customer.getPassword();
	}

	@Override
	public String getUsername() {
		return customer.getEmail();
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO Auto-generated method stub
		return UserDetails.super.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO Auto-generated method stub
		return UserDetails.super.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO Auto-generated method stub
		return UserDetails.super.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return UserDetails.super.isEnabled();
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}

	public String getFullName() {
		return customer.getFirstName()+" "+customer.getLastName();
	}


	public Customer getCustomer() {
		return customer;
	}


	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	
  
}
