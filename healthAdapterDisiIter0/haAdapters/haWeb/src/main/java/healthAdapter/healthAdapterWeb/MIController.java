package healthAdapter.healthAdapterWeb;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import healthAdapter.healthAdapterAppl.HealthAdapterService;

import healthAdapter.port.in.NaiveUseCase;
//import lombok.RequiredArgsConstructor;

/*
 * The Controller returns data
 */
@RestController
//@RequiredArgsConstructor
 
public class MIController {
	private  NaiveUseCase naiveUseCase ;
	/*
	 * This constructor-based Dependency Injection boilerplate 
	 * can be avoided by using lombok
	 */
	public MIController(NaiveUseCase service) {
		System.out.println("			 %%% MIController CREATED service=" + service);
		this.naiveUseCase = service;
	}


	
	@RequestMapping("/msg")
	public String showmsg() {
		/*
		 * We make reference to application logic software defined in module haApplication
		 */
		return HealthAdapterService.getHelloMessage( ) + " ... by MIDemoController in haWeb module";
	}
	
	/*
	 * We adopt DIP (Dependency Inversion Principle)
	 */
	@GetMapping( "/hexmsg" )
	public String hexmsg() {
		//return "wait a minute ... " + naiveUseCase;
		return naiveUseCase.doSomething("hello");
	}
 
}