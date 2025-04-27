package com.shopme.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Integer> {
	public List<Country> findAllByOrderByNameAsc();
}