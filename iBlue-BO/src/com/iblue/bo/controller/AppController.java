package com.iblue.bo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppController {

	@RequestMapping("/")
	public String mapPage(Model model) {
		String message = "Welcome to Spring MVC";
		model.addAttribute("welcomeMessage", message);
		return "map";
	}

	@RequestMapping("/spots")
	public String spotsPage(Model model) {

		return "spots";
	}

	@RequestMapping("/us")
	public String usPage(Model model) {

		return "us";
	}

}
