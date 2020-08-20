package it.unibo.HealthAdapterFacade;
import org.hl7.fhir.r4.model.Bundle;
/*
 * ------------------------------------------------------------------------
 * Provides a set of operations  relted to the business logic
 * ------------------------------------------------------------------------
 */
import org.hl7.fhir.r4.model.Patient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HealthServiceInterface {
	 
	public Long create_patient( Patient newPatient );
	public Long createPatientFromFile(String fileName );
	public Long create_patient(String familyName,String name);
	public String search_for_patients_named(String name, boolean usejson);
	public String delete_patient(String id);
//	public String read_a_resource(Long id);

/*
 * ASYNCH
 */
	public Flux<String> readPatientAsynch(Long id);   		   		//output is JsonString
	public Flux<String> createPatientAsynch(String jsonStr);   		//input is JsonString
	public Flux<String> searchResourceAsynch(String jsonTemplate);
	public Flux<String> deleteResourceAsynch( String resourceType, String id );

}
