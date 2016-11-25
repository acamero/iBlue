package com.iblue.bo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AppController {

	@RequestMapping("/")
	public String indexPage(Model model) {
		// String message = "Welcome to Spring MVC";
		// model.addAttribute("welcomeMessage", message);
		return "availability";
	}
	
	@RequestMapping("/availability")
	public String availabilityPage(Model model) {
		// String message = "Welcome to Spring MVC";
		// model.addAttribute("welcomeMessage", message);
		return "availability";
	}
	
	@RequestMapping("/availability-priv")
	public String availabilityPrivPage(Model model) {
		// String message = "Welcome to Spring MVC";
		// model.addAttribute("welcomeMessage", message);
		return "availability-priv";
	}

	@RequestMapping("/spots")
	public String spotsPage(Model model) {

		return "spots";
	}

	@RequestMapping("/us")
	public String usPage(Model model) {

		return "us";
	}
	
	@RequestMapping("/home")
	public String homePage(Model model) {

		return "home";
	}
	
	@RequestMapping("/map")
	public String mapPage(Model model) {

		return "map";
	}


	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginPage(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Credenciales incorrectas.");
		}

		
		//if (logout != null) {
		//	model.addObject("message", "Logged out from JournalDEV successfully.");
		//}

		model.setViewName("login");
		return model;
	}

}
