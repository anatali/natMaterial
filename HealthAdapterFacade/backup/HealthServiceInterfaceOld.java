/*
 * ------------------------------------------------------------------------
 * Provides a set of operations  related to the business logic
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade;

import org.hl7.fhir.r4.model.Patient;
import reactor.core.publisher.Flux;


public interface HealthServiceInterfaceOld {
/*
* ASYNCH: String are JsonString
*/
		public Flux<String> readPatientAsynch(Long id);   		   		 
		public Flux<String> createPatientAsynch(String jsonStr);   		 
		public Flux<String> searchResourceAsynch(String jsonTemplate);
		public Flux<String> deleteResourceAsynch( String resourceType, String id );
/*
* SYNCH: String are JsonString
*/ 
	public Long create_patient( Patient newPatient );
	public Long createPatientFromFile(String fileName );
	public Long create_patient(String familyName,String name);
	public String search_for_patients_named(String name, boolean usejson);
	public String delete_patient(String id);
//	public String read_a_resource(Long id);

}
