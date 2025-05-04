package com.shopme.shipping;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Country;
import com.shopme.common.entity.ShippingRate;

public interface ShippingRateRepository extends JpaRepository<ShippingRate, Integer> {
	public ShippingRate findByCountryAndState(Country country, String state);
}
