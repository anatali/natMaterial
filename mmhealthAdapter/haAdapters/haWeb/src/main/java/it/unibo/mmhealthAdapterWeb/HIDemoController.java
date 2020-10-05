package it.unibo.mmhealthAdapterWeb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import it.unibo.mmhealthAdapterAppl.HealthAdapterApplLogic;

/*
 * The Controller returns a HTML page
 */
@Controller 
public class HIDemoController {

	@RequestMapping("/")
	public String index(Model model) {
		return  "reacHtmlOnlyt0";
 	}
	@RequestMapping("/gui")
	public String gui(Model model) {
 		return  "gui";
	}

}