package com.itel.healthadapter.sandbox;

import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
 
import com.itel.healthadapter.api.Constants;
import com.itel.healthadapter.api.EnrollmentPayload;
import com.itel.healthadapter.api.HealthAdapterAPI;
import com.itel.healthadapter.api.StatusReference;
import com.itel.healthadapter.sandbox.cda.parser.CDAParseResult;
import com.itel.healthadapter.sandbox.cda.parser.CDAParser;
import com.itel.healthadapter.sandbox.cda.parser.CDAParserException;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

 


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

//
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import ca.uhn.fhir.rest.api.MethodOutcome;

/*
 * The Controller returns data
 */
@RestController
//@RequiredArgsConstructor
 
public class MIController {
	private  HAServiceInterface serviceHA ;
	private  IGenericClient fhirClient;
	private  CDAParser parser;
	private  String  serverAddr;

	/*
	 * We adopt DIP (Dependency Inversion Principle)
	 * 
	 * This constructor-based Dependency Injection boilerplate can be avoided by using lombok
	 */

	public MIController(HAServiceInterface service) {
		System.out.println("			 %%% MIController CREATED service=" + service);
		this.serviceHA = service;
		//AN: create just once
		serverAddr 		= "http://localhost:9442/fhir-server/api/v4/";
		//String  serverAddr 	= "https://hapi.fhir.org/baseR4";
		//String  serverAddr 	= "http://34.78.71.250:80/fhir-server/api/v4/" ;
		fhirClient     		= createFhirClient(serverAddr);
		parser 				= new CDAParser();		
		//fhirClient     = createFhirClient("https://localhost:9442");		//ITel-FHIR
	}
  
	@GetMapping( "/" )	 
	public String welcome() {
 		return "WELCOME TO healthAdapter BY MIController in health-adapter-disi";
	}
 
	@GetMapping( "/setImportPolicy" )	//the policy IMPLICA STATO?
	public String setImportPolicy() {
 		return serviceHA.setImportPolicy("Single target policy");
	}
	
	//curl -X PUT -H "Content-Type:application/json" -d "{\"pid\": \"12345\"}" http://localhost:8080/importPatient
	@PutMapping( HAServiceInterface.importPatientUri ) 
	public StatusReference importPatient( @RequestBody String patientIdJsonStr  ) {	// {"pid": "12345"}
		System.out.println("			 %%% MIController | importPatient patientIdJsonStr=" + patientIdJsonStr);
  		String patientId    = getFromJson(patientIdJsonStr, "pid");
  		String answer		= "";
  		if( patientId.length() > 0 ) answer= serviceHA.importPatient( patientId );
  		else answer = "Sorry, error on " + patientIdJsonStr;
	 	return new StatusReference("imported patient with id="+ answer);
 	}

 	/* from  JC */

	@PostMapping("enrollment")
	public StatusReference enrollment(@RequestBody EnrollmentPayload payload) {
        System.out.println("		%%% MIController | Enrollment with payload.getBody().length():  " + payload.getBody().length());
		ByteArrayInputStream is = new ByteArrayInputStream(payload.getBody().getBytes(StandardCharsets.UTF_8));
		//parser = new CDAParser(); //AN: create just once
		try {
			CDAParseResult result = parser.parseCDA(is);
			String patientTaxCode = result.patientTaxCode();
			/* Modified by AM to check response */
	        System.out.println("		%%% MIController | Enrollment patientTaxCode=" + patientTaxCode);
			if(savePatientOnFhir(patientTaxCode)) {
				return new StatusReference(HttpStatus.CREATED.toString() + " patientIdentifier="+patientTaxCode); //AN
			}else { //AN
				return new StatusReference("		%%% MIController | Enrollment PATIENT CREATE ERROR "   ); 
			}
		}
		catch (CDAParserException e) {/* AN: to avoid exceptions: better an explicit answer */
			 return new StatusReference("		%%% MIController | EnrollmentINTERNAL_SERVER_ERROR " + e.getMessage() );
		}
	}

	//To check: http://34.78.71.250:80/fhir-server/api/v4/Patient?identifier=BNCPLA43S41D643Q
	private boolean savePatientOnFhir(String patientTaxCode) {
		Patient patient = new Patient().addIdentifier(patientTaxCodeIdentifier(patientTaxCode));
        System.out.println("		%%% MIController | savePatientOnFhir patient="    + patient);
        System.out.println("		%%% MIController | savePatientOnFhir serverAddr=" + serverAddr);
        try { //AN after using http://localhost:9442
	        if( fhirClient != null ) {
	        	MethodOutcome outcome = fhirClient  
		        		.create()
		        		.resource(patient)
						.execute();
	        	//Long idVal = outcome.getId().getIdPartAsLong();
	        	String idVal = outcome.getId().getIdPart();
	        	//https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/org/hl7/fhir/instance/model/api/IIdType.html
	            System.out.println("		%%% MIController | savePatientOnFhir idVal=  " + idVal );
	        	return outcome.getCreated();
	        }
	        else return false;
         
        }catch( Exception e) {
            System.out.println("		%%% MIController | savePatientOnFhir ERROR:  " + e.getMessage() );
        	return false;
        }
	}
    
	private Identifier patientTaxCodeIdentifier(String patientTaxCode) {
		return new Identifier().setUse(Identifier.IdentifierUse.OFFICIAL)
				.setType(new CodeableConcept().addCoding(new Coding().setSystem(Constants.FHIR_IDENTIFIER_TYPE).setCode(Constants.FHIR_IDENTIFIED_TYPE_TAX)))
				.setSystem(Constants.FHIR_IDENTIFIER_TYPE)
				.setValue(patientTaxCode);
	}
	

	/* end JC */

	private IGenericClient createFhirClient(String addr) {
		FhirContext ctx = FhirContext.forR4();
//		String publicFhirServer = addr;
//		return ctx.newRestfulGenericClient(publicFhirServer);
        String serverBase = addr; //configurationProperties.getFhirServerBaseUrl();
        IGenericClient fhirClient = ctx.newRestfulGenericClient(serverBase);
        addBasicAuthInterceptor(fhirClient);
        return fhirClient;

	}

	private void addBasicAuthInterceptor(IRestfulClient client) {
	  BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor("fhiruser", "change-password");
	  client.registerInterceptor(authInterceptor);
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

/*

Indirizzo email Slack: help@slack-corp.com
https://www.javadevjournal.com/spring/feign/
OUTPUT di savePatientOnFhir

http://34.78.71.250/fhir-server/api/v4/Patient/17525deab7f-fbd2d111-4679-46d3-a32f-6c5da4085ca6/_history/1
http:/34.78.71.250/fhir-server/api/v4/Patient?identifier=BNCPLA43S41D643Q
http:/34.78.71.250/fhir-server/api/v4/Patient?id=17525deab7f-fbd2d111-4679-46d3-a32f-6c5da4085ca6
*/