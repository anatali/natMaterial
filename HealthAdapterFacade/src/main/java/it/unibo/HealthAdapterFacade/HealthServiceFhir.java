/*
 * ------------------------------------------------------------------------
 * Provides a set of operations defined by the HealthServiceInterface  
 * by using an instance of FhirServiceClient
 * It is used by HealthService when the user selects FHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade;

import java.util.Hashtable;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;

import it.unibo.HealthResource.ResourceUtility;
import reactor.core.publisher.Flux;


public class HealthServiceFhir implements HealthServiceInterface {
  	
	private FhirServiceClient fhirclient;	
	/*
	 * ----------------------------------------------------------------------------
	 * WARNING: the sever becomes WITH STATE. 
	 * It should be avoid here.
	 * The state should be maintained in the ITelCore
	 * ----------------------------------------------------------------------------
	 */
	private Hashtable<String,String> resources = new Hashtable<String,String>();  //id, type
	
	public HealthServiceFhir(String serverBase) {
		fhirclient = new FhirServiceClient( serverBase );	 
 		System.out.println("HealthServiceFhir created for " + serverBase  );
	}
	

/*
 * =============================================================================
 * SYNCH	PART
 * =============================================================================
 */	
//	CREATE
	//we store the resourceType for the created resource
	@Override
	public Long createResourceSynch(String jsonStr) {
 		IBaseResource resource = ResourceUtility.buildResource(  jsonStr );
		Long id = fhirclient.createSynch( resource );
		resources.put(id.toString(), resource.fhirType());
		return id;
	}
	
//	READ	
	@Override 
	public String readResourceSynch( String resourceType, String id ){
		Class<? extends DomainResource> resource = ResourceUtility.getTheClass(  resourceType );
		DomainResource answer  = fhirclient.readResourceSynch( resource, id );
		return HealthService.fhirctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(answer);	
	}
	
//	UPDATE	
	@Override 
	public String updateResourceSynch( String newresourceJsonStr ){
		String[] info  = ResourceUtility.getBasicInfo( newresourceJsonStr );
		DomainResource newresource = ResourceUtility.buildResource(newresourceJsonStr, info[1]); //info[1]-> id to Inject 
		String answer  = fhirclient.updateResourceSynch( newresource );
		return answer;
		
	}
	
//	DELETE	
	@Override
	public String deleteResourceSynch( String resourceType, String id ){
		String answer  = fhirclient.deleteResourceSynch( resourceType, id );
		return answer;		
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
		String[] answer  = ResourceUtility.inspect( jsonTemplate );
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
	@Override 
	public Flux<String> updateResourceAsynch( String newresourceJsonStr ){
		String[] answer  = ResourceUtility.getBasicInfo( newresourceJsonStr ); 
 		Flux<String> result = fhirclient.updateResourceAsynch( answer[1], newresourceJsonStr );
 		System.out.println("HealthServiceFhir | updateResourceAsynch result= " + result  );
		return result;
	}
	

	/*
	-------------------------- 
	DELETE
	-------------------------- 
	*/ 	
	@Override
	public Flux<String> deleteResourceAsynch( String resourceType, String id ){
		//Search the resourceType  
//		String resourceType = resources.get( id );
//		System.out.println("HealthServiceFhir | deleteResourceAsynch HAS FOUND resourceType = " + resourceType  );
//		if( resourceType == null ) return Flux.just("Sorry, resource with id=" + id + "not found");
 		Flux<String> result = fhirclient.deleteResourceAsynch(resourceType, id );
 		System.out.println("HealthServiceFhir | deleteResourceAsynch result= " + result  );
		return result;
	}
 	
 	

}
