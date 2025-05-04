package com.shopme.setting;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Currency;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {

}