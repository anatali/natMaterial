package it.unibo.HealthAdapterFacade;
/*
 * ------------------------------------------------------------------------
 * Provides a set of operations defined by the HealthServiceInterface  
 * by using an instance of FhirServiceClient
 * It is used by HealthService when the user selects FHIR
 * ------------------------------------------------------------------------
 */
 
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import it.unibo.HealthResource.PatientResource;
import it.unibo.HealthResource.SearchResourceUtility;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HealthServiceFhir implements HealthServiceInterface {
  	
	private PatientResource patientresource = new PatientResource();
	private FhirServiceClient fhirclient;	 
	private boolean useJson = true;
	
	public HealthServiceFhir(String serverBase) {
		fhirclient = new FhirServiceClient( serverBase );	 
 		System.out.println("HealthServiceFhir created for " + serverBase  );
	}
	
	/*
	* =========================================================================
	* CRUD - OLD PART  
	* =========================================================================
	*/	 	 
	@Override
	public Long createPatientFromFile(String fileName) {
		Patient newPatient    = patientresource.createFhirPatientFromFileJson(fileName);  //parser.parseResource(Patient.class, data);	
		Long idVal = fhirclient.createAndGetId(newPatient);	
		System.out.println("createPatientFromFile "+fileName+" got ID: " + idVal );
		return idVal;		
 	}
	
	@Override
	public Long create_patient( Patient newPatient) {
		return fhirclient.createAndGetId(newPatient);
	}
	
	@Override
	public Long create_patient(String familyName, String name) {
		try {
			Patient newPatient = patientresource.createFhirPatient(familyName,name);
			System.out.println("create_patient: " +  newPatient ); //newPatient.getBirthDateElement()
			return create_patient(newPatient );		
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("create_patient ERROR " + e.getMessage() );
			return 0L;
		}
	}
	
	@Override
	public String search_for_patients_named(String name, boolean usejson) {
		Bundle results = fhirclient.searchPatient( Patient.class, name );		
 		String res     = HealthService.cvt(results, useJson);
		return res;
	}
	
	
	@Override
	public String delete_patient(String id) {
		System.out.println("HealthServiceFhir delete for_patient id=" + id);
		String outcome = fhirclient.delete("Patient", id);
//		if (outcome != null) {
//			return "delete_patient " + id + " done" ; //" outcome=" + ((OperationOutcome) outcome).getIssueFirstRep().getDetails() ;
//		}else { 
//			return "delete_patient " + id + "NOT done" ;
//		}
		return outcome;
 	}
	
	/*
	 * curl http://localhost:8081/readResource/1435819 -i -X GET
	 */
//	public String read_a_resource( Long id ) { 
//		Patient patient = fhirclient.readPatient(Patient.class, id);
//		if( patient == null ) {			
//			//return "<resource><text>Resource " + id + "</text><text>resource not found</text></resource>";
//			return "{\"resourceType\":  \"Patient\", \"read\": \""+id+" NOT FOUND\"}";
//		} 			
//		System.out.println( "HealthService patient name="+patient.getName().toString() ); 			
//		String res = fhirclient.cvt(patient, useJson);
//		return res;			
//	}
	
	
/*
 * =============================================================================
 * ASYNCH	PART
 * =============================================================================
 */
	
	/*
	-------------------------- 
	CREATE
	-------------------------- 
	*/ 	
	@Override
	public Flux<String> createPatientAsynch(String jsonStr) {
		Flux<String> creationflux  = fhirclient.createAsynch( jsonStr );
		return creationflux;
	}
	
	
	/*
	-------------------------- 
	READ
	-------------------------- 
	*/ 	
 	@Override
	public Flux<String> readPatientAsynch(Long id) {
 		Flux<String> result = fhirclient.readPatientAsynch(id.toString());
 		System.out.println("HealthServiceFhir | readPatientAsynch result= " + result  );
		return result;
	}
 	
	/*
	-------------------------- 
	SEARCH
	-------------------------- 
	*/ 	
	@Override
	public Flux<String> searchResourceAsynch(String jsonTemplate) {
		//Bundle results = fhirclient.searchPatient( Patient.class, name );		
 		//String res     = HealthService.cvt(results, useJson);
		System.out.println("HealthServiceFhir | searchResourceAsynch " + jsonTemplate );
		String[] answer  = SearchResourceUtility.inspect( jsonTemplate );
		if( answer[0] != null ) {
			System.out.println("HealthServiceFhir | searchResourceAsynch resourceType:" + answer[0]  +" queryStr:" + answer[1] );
			return fhirclient.searchResourceAsynch(answer[0], answer[1]);
		}else return null;
 	}
 	
	/*
	-------------------------- 
	UPDATE
	-------------------------- 
	*/ 	
 	

	/*
	-------------------------- 
	DELETE
	-------------------------- 
	*/ 	
	@Override
	public Flux<String> deleteResourceAsynch( String resourceType, String id ){
 		Flux<String> result = fhirclient.deleteResourceAsynch(resourceType, id );
 		System.out.println("HealthServiceFhir | deleteResourceAsynch result= " + result  );
		return result;
	}
 	
	//@Override
//	public Mono<Long> todo( Patient newPatient ) {
//		Flux<String> creationflux = fhirclient.createAsynch( newPatient.toString()   );
//   		final StringBuilder strbuild = new StringBuilder(); 
//   		creationflux.subscribe( 				
//   			item  -> { /*System.out.println("-> " + item);*/ strbuild.append(item); },
//			error -> System.out.println("error= " + error ),
//			()    -> {  String s = patientresource.createFhirPatientFromJson( strbuild.toString() ).getId();
//						Long.parseLong(s);	//todo
//					 }
//		);
//   		return Mono.just( 0L );
//	}
 	

}
