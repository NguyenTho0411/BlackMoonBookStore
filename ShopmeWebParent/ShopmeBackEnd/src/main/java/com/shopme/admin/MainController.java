package com.shopme.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.shopme.admin.setting.SettingService;
import com.shopme.common.Constants;
import com.shopme.common.entity.Setting;

@Controller
public class MainController {

	@Autowired
	private SettingService service;
	@GetMapping("")
	public String viewHomePage() {
		return "index";
	}
	@GetMapping("/login")
	public String loginPage(Model model) {
		List<Setting> listSettings = service.listAllSetting();
		for(Setting setting : listSettings) {
			model.addAttribute(setting.getKey(), setting.getValue());
		}
		model.addAttribute("S3_BASE_URI", Constants.S3_BASE_URI);
		return "login";
	}
}
