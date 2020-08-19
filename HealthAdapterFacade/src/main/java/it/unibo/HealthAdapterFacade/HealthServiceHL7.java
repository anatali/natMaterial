package it.unibo.HealthAdapterFacade;
/*
 * ------------------------------------------------------------------------
 * Provides a set of operations defined by the HealthServiceInterface  
 * by using ... TODO
 * ------------------------------------------------------------------------
 */

/*
 * There's no canned process. 
 * The core problem is that segments are not naturally identified, and resources must be
 * (that's the core of the RESTful part). 
 * And identifying segments usefully is a business problem 
 * - it must be done based on the contents of the segment, it's context in the message, 
 *  and local identification etc practices applicable to the source of the message
 */
import org.hl7.fhir.r4.model.Patient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HealthServiceHL7 implements HealthServiceInterface{
 
	public HealthServiceHL7(String serverBase) {
		//fhirclient = new FhirServiceClient(serverBase,true);	//true => UseJson
	}

	@Override
	public Long createPatientFromFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long create_patient(Patient newPatient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long create_patient(String familyName,String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String search_for_patients_named(String name, boolean usejson) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String delete_patient(String id) {
		// TODO Auto-generated method stub
		return null;
	}

//	@Override
//	public String read_a_resource(Long id) {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
/*
* =============================================================================
* ASYNCH	PART
* =============================================================================
*/
	@Override
	public Flux<String> readPatientAsynch(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Mono<String> createPatientAsynch(String jsonStr) {
 		return null;
	}

	@Override
	public Flux<String> searchResourceAsynch(String jsonTemplate) {
		// TODO Auto-generated method stub
		return null;
	}


}
