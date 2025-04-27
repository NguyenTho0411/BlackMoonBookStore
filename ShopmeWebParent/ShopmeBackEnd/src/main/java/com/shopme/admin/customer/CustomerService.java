package com.shopme.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.shopme.admin.paging.PagingAndSortingHelper;
import com.shopme.admin.setting.CountryRepository;
import com.shopme.common.entity.AuthenticationType;
import com.shopme.common.entity.Country;
import com.shopme.common.entity.Customer;
import com.shopme.common.exception.CustomerNotFoundException;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 10;

	@Autowired
	private CustomerRepository customerRepo;
	@Autowired
	private CountryRepository countryRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;

	public void listByPage(int pageNum, PagingAndSortingHelper helper) {
		helper.listEntities(pageNum, CUSTOMERS_PER_PAGE, customerRepo);
	}

	public void updateCustomerEnabledStatus(Integer id, boolean enabled) {
		customerRepo.updateEnabledStatus(id, enabled);
	}

	public Customer get(Integer id) throws CustomerNotFoundException {
		try {
			return customerRepo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}
	}

	public List<Country> listAllCountries() {
		return countryRepo.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = customerRepo.findByEmail(email);

		if (existCustomer != null && existCustomer.getId() != id) {
			// found another customer having the same email
			return false;
		}

		return true;
	}

	public void save(Customer customerInForm) {
	    // Retrieve the customer from the database
	    Customer customerInDB = customerRepo.findById(customerInForm.getId()).get();

	    // Handle the password logic only if the authentication type is DATABASE
	    if (customerInDB.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
	        if (!customerInForm.getPassword().isEmpty()) {
	            // If a new password is provided, encode it
	            String encodedPassword = passwordEncoder.encode(customerInForm.getPassword());
	            customerInForm.setPassword(encodedPassword);
	        } else {
	            // Ensure that the password is set to the existing one in the database
	            customerInForm.setPassword(customerInDB.getPassword());
	        }
	    } else {
	        // For other authentication types, retain the existing password from the database
	        customerInForm.setPassword(customerInDB.getPassword());
	    }

	    // Retain other fields from the database
	    customerInForm.setEnabled(customerInDB.isEnabled());
	    customerInForm.setCreatedTime(customerInDB.getCreatedTime());
	    customerInForm.setVerificationCode(customerInDB.getVerificationCode());
	    customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());

	    // Save the updated customer
	    customerRepo.save(customerInForm);
	}


	public void delete(Integer id) throws CustomerNotFoundException {
		Long count = customerRepo.countById(id);
		if (count == null || count == 0) {
			throw new CustomerNotFoundException("Could not find any customers with ID " + id);
		}

		customerRepo.deleteById(id);
	}

}