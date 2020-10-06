package healthAdapter.healthAdapterWeb;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
//import healthAdapter.healthAdapterAppl.HealthAdapterService;

import healthAdapter.port.in.HAServiceInterface;
//import lombok.RequiredArgsConstructor;

/*
 * The Controller returns data
 */
@RestController
//@RequiredArgsConstructor
 
public class MIController {
	private  HAServiceInterface serviceHA ;
	/*
	 * We adopt DIP (Dependency Inversion Principle)
	 * 
	 * This constructor-based Dependency Injection boilerplate can be avoided by using lombok
	 */
	
	public MIController(HAServiceInterface service) {
		System.out.println("			 %%% MIController CREATED service=" + service);
		this.serviceHA = service;
	}
  
 
	@GetMapping( "/setImportPolicy" )
	public String setImportPolicy() {
 		return serviceHA.setImportPolicy("Single target policy");
	}
	
	@GetMapping( "/import" )	//NON REST, just to test ...
	public String importPatient() {
		String v = serviceHA.importPatient("todo");
 		return "imported patient with id="+v;
	}
 
}