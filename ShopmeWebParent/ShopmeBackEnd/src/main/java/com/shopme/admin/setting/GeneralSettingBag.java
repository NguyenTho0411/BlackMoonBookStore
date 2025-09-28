package com.shopme.admin.setting;

import java.util.List;

import com.shopme.common.entity.Setting;
import com.shopme.common.entity.SettingBag;

public class GeneralSettingBag extends SettingBag {

	private static volatile GeneralSettingBag instance;

	private GeneralSettingBag(List<Setting> listSettings) {
		super(listSettings);
	}

	public static GeneralSettingBag getInstance(List<Setting> listSettings) {
		GeneralSettingBag result = instance;
		if (result == null) {
			synchronized (GeneralSettingBag.class) {
				result = instance;
				if (result == null) {
					instance = result = new GeneralSettingBag(listSettings);
				}
			}
		}
		return result;
	}

	public static GeneralSettingBag getInstance() {
		if (instance == null) {
			throw new IllegalStateException("GeneralSettingBag chưa được khởi tạo.");
		}
		return instance;
	}

	public void updateCurrencySymbol(String value) {
		super.update("CURRENCY_SYMBOL", value);
	}

	public void updateSiteLogo(String value) {
		super.update("SITE_LOGO", value);
	}
}
