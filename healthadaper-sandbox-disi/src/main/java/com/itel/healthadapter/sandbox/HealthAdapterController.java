package com.itel.healthadapter.sandbox;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;

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
import java.lang.String;
import java.nio.charset.StandardCharsets;

import org.json.*;

@RestController    
public class HealthAdapterController implements HealthAdapterAPI {

    private Logger logger = LoggerFactory.getLogger(HealthAdapterController.class);

 	private  IGenericClient fhirClient;
	private  CDAParser parser;
	private  String  serverAddr;

    public HealthAdapterController() { //IGenericClient fhirClient
		serverAddr 		= "http://localhost:9442/fhir-server/api/v4/metadata";
		//serverAddr 	= "https://hapi.fhir.org/baseR4";
		//serverAddr 	= "http://34.78.71.250:80/fhir-server/api/v4/" ;
		fhirClient     		= createFhirClient(serverAddr);
		parser 				= new CDAParser();	
	    System.out.println("		%%% HealthAdapterController | CREATED serverAddr=" + serverAddr);
    }
    
    //Added to show something in a browser
	 @GetMapping( "/" )	 
	 public  String entry( ) {
		 return "HealthAdapterController | Welcome from com.itel.healthadapter.sandbox.disi" ;
	 }
    
	 @PostMapping( "/test" )
	 public String test( @RequestBody String jsonStr ) {
	     System.out.println("		%%% HealthAdapterController | test with jsonStr=" + jsonStr);
 		 return "HealthAdapterController | test with input="+jsonStr;
	 }
    

    @Override
	@PostMapping("enrollment")
	public StatusReference enrollment(@RequestBody EnrollmentPayload payload) {
        System.out.println("		%%% HealthAdapterController | Enrollment performed with payload.getBody().length():  " + payload.getBody().length());
		ByteArrayInputStream is = new ByteArrayInputStream(payload.getBody().getBytes(StandardCharsets.UTF_8));


		try {
 	        System.out.println("		%%% HealthAdapterController | Enrollment parser: " + parser);
			CDAParseResult result = parser.parseCDA(is);
	        System.out.println("		%%% HealthAdapterController | Enrollment parser result: " + result);
			String patientTaxCode = result.patientTaxCode();
	        System.out.println("		%%% HealthAdapterController | Enrollment patientTaxCode: " + patientTaxCode);
			/* Modified by AM to check response */
			if(savePatientOnFhir(patientTaxCode)) {
				return new StatusReference(HttpStatus.CREATED.toString() + " patientIdentifier="+patientTaxCode); //AN
			}else { //AN
				return new StatusReference("		%%% HealthAdapterController | Enrollment PATIENT CREATE ERROR "   ); 
			}
		}
		catch (CDAParserException e) {/* AN: to avoid exceptions: better an explicit answer */
			 return new StatusReference("		%%% HealthAdapterController | Enrollment INTERNAL_SERVER_ERROR " + e.getMessage() );
		}
	}

    private boolean savePatientOnFhir(String patientTaxCode) {
		Patient patient = new Patient().addIdentifier(patientTaxCodeIdentifier(patientTaxCode));
        System.out.println("		%%% HealthAdapterController | savePatientOnFhir patient="    + patient);
        System.out.println("		%%% HealthAdapterController | savePatientOnFhir serverAddr=" + serverAddr);
        try { //AN after using http://localhost:9442
	        if( fhirClient != null ) {
	        	MethodOutcome outcome = fhirClient  
		        		.create()
		        		.resource(patient)
						.execute();
	        	//Long idVal = outcome.getId().getIdPartAsLong();
	        	String idVal = outcome.getId().getIdPart();
	        	//https://hapifhir.io/hapi-fhir/apidocs/hapi-fhir-base/org/hl7/fhir/instance/model/api/IIdType.html
	            System.out.println("		%%% HealthAdapterController | savePatientOnFhir idVal=  " + idVal );
	        	return outcome.getCreated();
	        }
	        else return false;
         
        }catch( Exception e) {
            System.out.println("		%%% HealthAdapterController | savePatientOnFhir ERROR:  " + e.getMessage() );
        	return false;
        }
    	
    	/*
        Patient patient = new Patient().addIdentifier(patientTaxCodeIdentifier(patientTaxCode));
        System.out.println("		%%% HealthAdapterController | savePatientOnFhir patient: " + patient);
        System.out.println("		%%% HealthAdapterController | savePatientOnFhir fhirClient: " + fhirClient);
       return fhirClient
        		.create()
        		.resource(patient)
        		.execute()
        		.getCreated();  
    	 */
    }
    
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
    

    private Identifier patientTaxCodeIdentifier(String patientTaxCode) {
        return new Identifier().setUse(Identifier.IdentifierUse.OFFICIAL)
                .setType(new CodeableConcept().addCoding(new Coding().setSystem(Constants.FHIR_IDENTIFIER_TYPE).setCode(Constants.FHIR_IDENTIFIED_TYPE_TAX)))
                .setSystem(Constants.FHIR_IDENTIFIER_TYPE)
                .setValue(patientTaxCode);
    }

    @Override
	@PostMapping("import")
    public StatusReference _import(String identifier) {
    	
        logger.info("Import performed with identifier:  " + identifier);
		System.out.println("		%%% HealthAdapterController | importPatient identifier=" + identifier);
  		String patientId    = getFromJson(identifier, "pid");
  		String answer		= "";
  		if( patientId.length() > 0 ) { //answer= serviceHA.importPatient( patientId );
  			// TODO Perform import
  			answer = "importPatient: search in HCServer a patient with id=" + identifier 
					+" and next do a PUT of (part of) the answer) to ITEL-FHIR";
			System.out.println(answer);

  		}
  		else answer = "Sorry, error on " + identifier;
	 	return new StatusReference("imported patient with id; answer="+ answer);
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

http://localhost:9442/fhir-server/api/v4/$healthcheck
*/