package com.itel.healthadapter.sandbox;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
 

/*
 * The Controller returns a HTML page
 */
@Controller 
public class HIController {

	@RequestMapping("/gui")
	public String gui(Model model) {
 		return  "gui";
	}

}