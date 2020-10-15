package com.itel.smartevenmock;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.web.bind.annotation.*;

import com.itel.healthadapter.api.EnrollmentPayload;
import com.itel.healthadapter.api.HealthAdapterAPI;
import com.itel.healthadapter.api.StatusReference;

import feign.Feign;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;

//import ca.uhn.fhir.context.FhirContext;
// import org.hl7.fhir.r4.model.*;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.GetMapping;

 
@RestController
 
public class MISmartEventController {
   
	@GetMapping( "/" )	 
	public String welcome() {
	    System.out.println("	%%% MISmartEventController welcome" );
 		return "WELCOME TO MISmartEventController in smarteven-mock-disi";
	} 
	
	@GetMapping( "/test" )	 
	public String test() {
		 try {
	        Feign.Builder builder = Feign.builder();
	        builder.contract(new SpringMvcContract());
	        //To overcome ERROR=class com.itel.healthadapter.api.EnrollmentPayload is not a type supported by this encoder
  			builder.encoder(new JacksonEncoder());	  
  			builder.decoder(new JacksonDecoder());
	    	HealthAdapterAPI client = builder.target(HealthAdapterAPI.class, "http://localhost:8085");
	    	System.out.println("	%%% MISmartEventController healthAdapterClient client to 8085=" + client );
	    	String CDAFilePath = System.getProperty("user.dir") + File.separator + "userDocs" +
	    			File.separator + "enrollment-payload.json";  
	    	String payload      =  readFileAsString(CDAFilePath);
	    	System.out.println("	%%% MISmartEventController healthAdapterClient payload=" + payload );
			EnrollmentPayload ep = new EnrollmentPayload( );
			ep.setBody(payload);
	    	StatusReference answer = client.enrollment( ep );
		    System.out.println("	%%% MISmartEventController test ERROR=" +  answer.getLocation() );
	    	return "MISmartEventController test answer=" + answer.getLocation();
		 }catch( Exception e ) {
		    System.out.println("	%%% MISmartEventController test ERROR=" +  e.getMessage() );
			 return "MISmartEventController test ERROR:" + e.getMessage();
		 }
  		
	} 
	
    /* Added by AM */
	private static String readFileAsString(String fileName) {
		try {
			return new String(Files.readAllBytes(Paths.get(fileName)));
		} catch(Exception e) {
			return "";
		}
	}	
}