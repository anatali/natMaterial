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
package it.unibo.HealthAdapterFacade;

import reactor.core.publisher.Flux;

public class HealthServiceHL7 implements HealthServiceInterface{
	/*
	* =============================================================================
	* ASYNCH	PART
	* =============================================================================
	*/ 
	public HealthServiceHL7(String serverBase) {
		//fhirclient = new FhirServiceClient(serverBase,true);	//true => UseJson
	}

	@Override
	public Flux<String> readResourceAsynch(String resourceType,String id) {
 		return null;
	}

	@Override
	public Flux<String> createResourceAsynch(String jsonStr) {
 		return null;
	}

	@Override
	public Flux<String> searchResourceAsynch(String jsonTemplate) {
 		return null;
	}

	@Override
	public Flux<String> deleteResourceAsynch( String resourceType,  String id ) {
 		return null;
	}

	@Override
	public Flux<String> updateResourceAsynch( String newresourceJsonStr ) {
 		return null;
	}

	/*
	* =============================================================================
	* SYNCH	PART
	* =============================================================================
	*/ 
	@Override
	public String createResourceSynch(String jsonStr) {
 		return null;
	}


	@Override
	public String readResourceSynch(String resourceType, String id) {
 		return null;
	}

	@Override
	public String searchResourceSynch(String jsonTemplate) {
 		return null;
	}
 
	@Override
	public String deleteResourceSynch(String resourceType, String id) {
		return null;
	}

	@Override
	public String updateResourceSynch(String newresourceJsonStr) {
		return null;
	}

	/*
	* =============================================================================
	* CONVERT
	* =============================================================================
	*/ 
	@Override
	public Flux<String> cvthl7tofhir(String template, String data) {
 		return null;
	}

	public  Flux<String> docvthl7tofhir( String path, String data ) {
		return null;
	}

	@Override
	public void setHealthService(String choice, String serverAddr) {
		// TODO Auto-generated method stub
	}

 
}
