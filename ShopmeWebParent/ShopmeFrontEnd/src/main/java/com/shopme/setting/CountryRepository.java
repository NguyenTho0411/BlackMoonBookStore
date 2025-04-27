package com.shopme.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.shopme.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
	public List<Country> findAllByOrderByNameAsc();
	
	@Query("SELECT c FROM Country c WHERE c.code = ?1")
	public Country findByCode(String code);
}