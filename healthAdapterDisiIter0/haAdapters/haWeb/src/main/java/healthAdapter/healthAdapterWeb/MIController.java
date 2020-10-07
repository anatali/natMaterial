package healthAdapter.healthAdapterWeb;

import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.json.*;
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
  
	@GetMapping( "/" )	 
	public String welcome() {
 		return "WELCOME TO healthAdapter BY MIController";
	}
 
	@GetMapping( "/setImportPolicy" )	//the policy IMPLICA STATO?
	public String setImportPolicy() {
 		return serviceHA.setImportPolicy("Single target policy");
	}
	
//curl -X PUT -H "Content-Type:application/json" -d "{\"pid\": \"12345\"}" http://localhost:8080/importPatient
	@PutMapping( HAServiceInterface.importPatientUri ) 
	public String importPatient( @RequestBody String patientIdJsonStr  ) {	// {"pid": "12345"}
		System.out.println("			 %%% MIController | importPatient patientIdJsonStr=" + patientIdJsonStr);
  		String patientId    = getFromJson(patientIdJsonStr, "pid");
  		String answer		= "";
  		if( patientId.length() > 0 ) answer= serviceHA.importPatient( patientId );
  		else answer = "Sorry, error on " + patientIdJsonStr;
	 	return "imported patient with id="+ answer;
 	}

	
/*
 * -----------------------------------------------------
 * UTILITIES
 * -----------------------------------------------------
 */
	
	//Is it better to throw the exception?
	private String getFromJson( String jsonStr, String key ) {
 		try {
			JSONObject pidObj   = new JSONObject (jsonStr );	
			String v    		= pidObj.getString( key );
 	 		return  v;
		} catch (JSONException e) {
			System.out.println("			 %%% MIController | getFromJson ERROR " + e.getMessage());
			return "";
		}		
	}
 
}