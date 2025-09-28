package com.shopme.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository repo;

	public List<Setting> listAllSetting() {
		return repo.findAll();
	}

	public GeneralSettingBag getGeneralSettings() {
		List<Setting> settings = new ArrayList<>();
		List<Setting> generalSettings = repo.findByCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = repo.findByCategory(SettingCategory.CURRENCY);
		List<Setting> mailServerSettings = repo.findByCategory(SettingCategory.MAIL_SERVER);
		List<Setting> mailTemplate = repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
		List<Setting> settingPayemnt = repo.findByCategory(SettingCategory.PAYMENT);
		settings.addAll(generalSettings);
		settings.addAll(currencySettings);
		settings.addAll(mailServerSettings);
		settings.addAll(mailTemplate);
		settings.addAll(settingPayemnt);
		// Singleton initialization
		return GeneralSettingBag.getInstance(settings);
	}

	public void saveAll(Iterable<Setting> settings) {
		repo.saveAll(settings);
	}

	public List<Setting> getMailServerSettings() {
		return repo.findByCategory(SettingCategory.MAIL_SERVER);
	}

	public List<Setting> getMailTemplateSettings() {
		return repo.findByCategory(SettingCategory.MAIL_TEMPLATES);
	}

	public List<Setting> getCurrencySettings() {
		return repo.findByCategory(SettingCategory.CURRENCY);
	}

	public List<Setting> getPaymentSettings() {
		return repo.findByCategory(SettingCategory.PAYMENT);
	}
}
