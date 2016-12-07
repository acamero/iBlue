package com.iblue.bo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
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
		return "login";
	}

	@RequestMapping(value = "/bo/{name}", method = RequestMethod.GET)
	public String boPage(@PathVariable("name") String name, Model model) {
		// String message = "Welcome to Spring MVC";
		// model.addAttribute("welcomeMessage", message);
		return name;
	}

	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public ModelAndView loginPage(@RequestParam(value = "error", required = false) String error,
			@RequestParam(value = "logout", required = false) String logout) {

		ModelAndView model = new ModelAndView();
		if (error != null) {
			model.addObject("error", "Credenciales incorrectas.");
		}

		model.setViewName("login");
		return model;
	}

	@RequestMapping(value = "/bulk/streets")
	public String bulkStreets(@RequestParam(value="bulk")String bulk) {
		System.out.println(bulk);
		return "map";
	}

}
