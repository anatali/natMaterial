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
import java.lang.String;
import java.nio.charset.StandardCharsets;

@RestController    
public class HealthAdapterController implements HealthAdapterAPI {

    private Logger logger = LoggerFactory.getLogger(HealthAdapterController.class);

    private final IGenericClient fhirClient;

    public HealthAdapterController(IGenericClient fhirClient) {
        this.fhirClient = fhirClient;
    }
    
    //Added to show something in a browser
	 @GetMapping( "/" )	 
	 public  String entry( ) {
		 return "HealthAdapterController | Welcome from com.itel.healthadapter.sandbox" ;
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
			CDAParser parser 	  = new CDAParser();  
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
        System.out.println("		%%% HealthAdapterController | savePatientOnFhir patient: " + patient);
        System.out.println("		%%% HealthAdapterController | savePatientOnFhir fhirClient: " + fhirClient);
       return fhirClient
        		.create()
        		.resource(patient)
        		.execute()
        		.getCreated();  /* Added by AM to check the creation's result */
    }

    private Identifier patientTaxCodeIdentifier(String patientTaxCode) {
        return new Identifier().setUse(Identifier.IdentifierUse.OFFICIAL)
                .setType(new CodeableConcept().addCoding(new Coding().setSystem(Constants.FHIR_IDENTIFIER_TYPE).setCode(Constants.FHIR_IDENTIFIED_TYPE_TAX)))
                .setSystem(Constants.FHIR_IDENTIFIER_TYPE)
                .setValue(patientTaxCode);
    }

    @Override
    public StatusReference _import(String identifier) {

        System.out.println("Import performed with identifier:  " + identifier);
        logger.info("Import performed with identifier:  " + identifier);

        // TODO Perform import

        return new StatusReference("To be implemented"); // TODO
    }
}
