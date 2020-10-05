package healthAdapter.healthAdapterWeb;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
 

/*
 * The Controller returns a HTML page
 */
@Controller 
public class HIController {

	@RequestMapping("/")
	public String index(Model model) {
		return  "reacHtmlOnlyt0";
 	}
	@RequestMapping("/gui")
	public String gui(Model model) {
 		return  "gui";
	}

}