package it.unibo.HealthAdapterFacade.Pojo;
/*
 * ------------------------------------------------------------------------
 * Utilizza  HealthServiceFhir per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
 
import org.hl7.fhir.r4.model.Patient;

import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
import reactor.core.publisher.Flux;
/*
  	public Long create_patient( Patient newPatient );
	public Long createPatientFromFile(String fileName );
	public Long create_patient(String familyName,String name);
	public String search_for_patients_named(String name, boolean usejson);
	public void delete_patient(String id);
	public String readPatient(Long id);
 
 */

public class HealthServiceFhirUsageOld {
 	
  	private HealthServiceInterface healthService;
  	private String serverBase="https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";   
  	
 	public HealthServiceFhirUsageOld() {
 		healthService = new HealthServiceFhir( serverBase ) ;	
  	}
	public Long createPatientFromFile( String fname ) {
		Long id = healthService.createPatientFromFile( fname );
		System.out.println("createPatientFromFile id= " + id);
		return id;
 	}
	
	public void readPatient(Long id)   {
 		Flux<String> answer          = healthService.readPatientAsynch(id);
 		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> { /* System.out.println("-> " + item); */ strbuild.append(item); },
 				error -> System.out.println("HealthServiceFhirUsage | readPatient error= " + error ),
 				()    -> { /* System.out.println("HealthServiceFhirUsage | readPatient response="+ strbuild); */   }
 		);	
 		System.out.println("readPatient IS BUILDING THE ANSWER ... ");
 		answer.blockLast(); //TO AVOID PREMATURE TERMINATION
  		System.out.println("readPatient ANSWER:" + HealthService.prettyJson( strbuild.toString() ) );
 	}
	
	
	public void search(Long id) {
		
	}
	
 	public void searchPatient(String patientName) {
		String answerjson = healthService.search_for_patients_named(patientName, true); //true=> usejson
		System.out.println("searchPatient " + patientName);
		System.out.println( answerjson );		
	}
 	
 	public void delete_patient(String id) {
 		String res = healthService.delete_patient(id);
		System.out.println("----------------- deletePatient result:" + res );
 	}
	/*
	 * Create, Read, Search, Delete	
	 */	
	public static void main( String[] args) {
		HealthServiceFhirUsageOld appl = new HealthServiceFhirUsageOld();
		
		System.out.println(" %%% READ  ------------------------------ ");
//		appl.readPatient(id);
 		appl.readPatient(1436187L);
		/* 		
		System.out.println(" %%% CREATE ------------------------------");
		String resourceFileName = "src/main/java/it/unibo/HealthResource/PatientExample0json.txt";
 		Long id = appl.createPatientFromFile( resourceFileName ) ;
		

		System.out.println(" %%% SEARCH ----------------------------- ");
		appl.searchPatient("PeterUniboBologna");
		
 		System.out.println(" %%% DELETE ----------------------------- ");	
 		appl.delete_patient(  id.toString() );
 		
 		System.out.println(" %%% SEARCH ----------------------------- ");	
		appl.searchPatient("PeterUniboBologna");
*/ 	
		}
}
