/*
 * ------------------------------------------------------------------------
 * Provides a set of operations defined by the HealthServiceInterface  
 * by using an instance of FhirServiceClient
 * It is used by HealthService when the user selects FHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade;

import it.unibo.HealthResource.SearchResourceUtility;
import reactor.core.publisher.Flux;


public class HealthServiceFhir implements HealthServiceInterface {
  	
	private FhirServiceClient fhirclient;	 
	
	public HealthServiceFhir(String serverBase) {
		fhirclient = new FhirServiceClient( serverBase );	 
 		System.out.println("HealthServiceFhir created for " + serverBase  );
	}
	
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
	public Flux<String> createResourceAsynch(String jsonStr) {
		Flux<String> creationflux  = fhirclient.createAsynch( jsonStr );
		return creationflux;
	}
	
	/*
	-------------------------- 
	READ
	-------------------------- 
	*/ 	
 	@Override
	public Flux<String> readResourceAsynch(String resourceType, Long id) { 		
 		Flux<String> result = fhirclient.readResourceAsynch( resourceType,id.toString());		 
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
 	
 	

}
