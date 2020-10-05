package healthAdapter.healthAdapterWeb;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import healthAdapter.healthAdapterAppl.HealthAdapterApplLogic;

/*
 * The Controller returns data
 */
@RestController
public class MIDemoController {

	@RequestMapping("/msg")
	public String showmsg() {
		/*
		 * We make reference to application logic software defined in module haApplication
		 */
		return HealthAdapterApplLogic.getHelloMessage( ) + " ... by MIDemoController in haWeb module";
	}
 
}