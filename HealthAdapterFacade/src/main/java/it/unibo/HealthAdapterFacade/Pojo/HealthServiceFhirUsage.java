package it.unibo.HealthAdapterFacade.Pojo;
/*
 * ------------------------------------------------------------------------
 * Utilizza un oggetto di tipo HealthServiceFhir
 * ------------------------------------------------------------------------
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.Patient;
import org.json.JSONObject;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
 

public class HealthServiceFhirUsage {
 	
  	private HealthServiceInterface healthService;
  	private String serverBase="https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";   
  	
 	public HealthServiceFhirUsage() {
 		healthService = new HealthServiceFhir( serverBase ) ;	
  	}
	public void createPatientFromFile( String fname ) {
		Long id = healthService.createPatientFromFile( fname );
		System.out.println("createPatientFromFile id= " + id);
		healthService.read_a_resource(  id);
 	}
	public void search(Long id) {
		
	}
	public void search(String patientName) {
		String answer = healthService.search_for_patients_named(patientName, true); //true=> usejson
		System.out.println("FOUND for " + patientName);
		System.out.println( answer );		
	}
	public static void main( String[] args) {
		HealthServiceFhirUsage appl = new HealthServiceFhirUsage();
		//appl.search("ElenaBologna");
		appl.createPatientFromFile( "src/main/java/it/unibo/HealthResource/PatientExample0json.txt" ) ;
	}
}
