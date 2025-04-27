package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.State;

public interface StateRepository extends JpaRepository<State, Integer> {
	
	public List<State> findByCountryOrderByNameAsc(Country country);
}