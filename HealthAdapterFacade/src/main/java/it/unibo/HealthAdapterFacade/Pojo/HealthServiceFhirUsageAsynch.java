/*
 * ------------------------------------------------------------------------
 * Utilizza  HealthServiceFhir per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade.Pojo;
 
import java.io.IOException;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
import reactor.core.publisher.Flux;
 

public class HealthServiceFhirUsageAsynch {
 	
  	private static HealthServiceInterface healthService;
  	
	public static final String resourceFileName   = 
			"src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejson.txt";
	public static final String updateResourceFileName = 
			"src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejsonUpdate.txt";
	public static final String queryStr  = 
			"{ \"resourceType\": \"Patient\", \"address\": { \"city\": \"Cesena\", \"country\": \"Italy\" } }"; 
  	
  	private static String serverBase="https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";   
  	private static String currentResourceType = "Patient";
  	private static Long   currentResourceId   = null;
  	private static boolean currentJobDone     = false;
  			
 	public HealthServiceFhirUsageAsynch() {
 		healthService = new HealthServiceFhir( serverBase ) ;	
  	}
 	
  	
 	private void extractValues( String jsonRep ) {
		try {
			JSONObject jsonobj = new JSONObject( jsonRep );
			String idstr        = jsonobj.getString("id");
			currentResourceType = jsonobj.getString("resourceType");
	  		currentResourceId   = Long.parseLong( idstr );
	  		System.out.println("extractValues id=:" + currentResourceId + " currentResourceType="  + currentResourceType);
		} catch (JSONException e) {
			System.out.println("extractValues ERROR" + e.getMessage() );
		}	 			 				
 	}
 	

	public void createResourceFromFile( String fname, Consumer<Long> callback) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		Flux<String> answer = healthService.createResourceAsynch( jsonRep );
 		final StringBuilder strbuild = new StringBuilder();  
 		System.out.println("createResourceFromFile IS BUILDING THE ANSWER ... ");
		answer.subscribe(			
 				item  -> {   strbuild.append(item); },
 				error -> System.out.println("HealthServiceFhirUsage | createResourceFromFile error= " + error ),
 				()    -> {   
 					extractValues( strbuild.toString() );  
 					callback.accept( currentResourceId  );	//calls createResourceDone
 				}
 		);	
   	}
	
	public static void createResourceDone( Long id )   {
		System.out.println("HealthServiceFhirUsage | createResourceDone id= " + id );
		readResource( id,  HealthServiceFhirUsageAsynch::readResourceDone );
  	}

	public static void readResource( Long id, Consumer<String> callback )   {
 		Flux<String> answer  = healthService.readResourceAsynch(currentResourceType,id);
 		final StringBuilder strbuild = new StringBuilder();  
		answer.subscribe(			
 				item  -> {   strbuild.append(item); },
 				error -> System.out.println("HealthServiceFhirUsage | readResource error= " + error ),
 				()    -> {   
  					callback.accept( HttpFhirSupport.prettyJson( strbuild.toString() )  ); //calls readResourceDone
 				}
 	    );
 	}

	public static void readResourceDone( String resourceRep )   {
		System.out.println("HealthServiceFhirUsage | readResourceDone " + resourceRep );
		doAfterRead( );
 	}
	
/*
 * ===========================================================================	
 * Avoiding callback is not simpler ...
 * ===========================================================================	
 */
	
	private static void forceSequentialBehavior() {
		while( ! currentJobDone  ) {
	 		System.out.println("forceSequentialBehavior ... ");
			HealthService.delay(500);
		}
		currentJobDone = false;
 	}
	
 	private static void subscribeAndHandleCompletion( Flux<String> answer ) {
 		System.out.println("HealthServiceFhirUsage | endOfJob  IS BUILDING THE ANSWER ... ");
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> {   strbuild.append(item); },
 				error -> System.out.println("HealthServiceFhirUsage | subscribeAndHandleCompletion error= " + error ),
 				()    -> {  
 					currentJobDone = true; 
 					System.out.println("HealthServiceFhirUsage | subscribeAndHandleCompletion " + HealthService.prettyJson( strbuild.toString() ) );  
 				}
 		);			
 	}
 	
	
	public static void doAfterRead() {
		System.out.println("HealthServiceFhirUsage | doAfterRead " + currentResourceType + " " + currentResourceId  );
		System.out.println(" %%% SEARCH  ------------------------------ ");
		searchResource( queryStr );
//		forceSequentialBehavior();		
		
		System.out.println(" %%% UPDATE  ------------------------------ ");
		updateResourceFromFile(updateResourceFileName, currentResourceId);
		forceSequentialBehavior();

		System.out.println(" %%% DELETE  ------------------------------ ");
		currentResourceType = "Patient";
		deleteResource( currentResourceType, currentResourceId );	//
	}
	
	
 	public static void searchResource(String queryJson) {
 		Flux<String> answer   = healthService.searchResourceAsynch(queryJson);
 		subscribeAndHandleCompletion( answer );
 	}

 	public static void updateResourceFromFile( String fname, Long id ) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		Flux<String> answer = healthService.updateResourceAsynch(   jsonRep  );
   		subscribeAndHandleCompletion( answer );
 	}

 	public static void deleteResource(  String resourceType, Long id ) {
 		Flux<String> answer   = healthService.deleteResourceAsynch(  resourceType, id.toString() );
  		subscribeAndHandleCompletion( answer );
 	}

	
	
	/*
	 * CRUD - the callback hell
	 */	
	public static void main( String[] args) throws IOException {
		HealthServiceFhirUsageAsynch appl = new HealthServiceFhirUsageAsynch();
  
		System.out.println(" %%% CREATE  ------------------------------ ");
		appl.createResourceFromFile(resourceFileName, HealthServiceFhirUsageAsynch::createResourceDone   );
		
//		//wait until the appl.currentResourceId is updated
//		while( appl.currentResourceId == null ) { 
//	 		System.out.println("waiting for THE ANSWER ... ");
//			HealthService.delay(500);
//		}
		
		/*		
		System.out.println(" %%% READ    ------------------------------ ");
		appl.readResource( appl.currentResourceId );
		appl.waitEndOfJob();
 		
		System.out.println(" %%% SEARCH  ------------------------------ ");
		appl.searchResource( queryStr );
		appl.waitEndOfJob();
 		
		System.out.println(" %%% UPDATE  ------------------------------ ");
		appl.updateResourceFromFile(updateResourceFileName, 1439336L);
		appl.waitEndOfJob();

		System.out.println(" %%% DELETE  ------------------------------ ");
		appl.currentResourceType = "Patient";
		appl.deleteResource( appl.currentResourceType, appl.currentResourceId );	//
*/ 		
		System.out.println(" %%% END  ------------------------------ ");
		HealthService.delay(10000);  //To avoid premature termination
		
//		System.out.println(" %%% END  ------------------------------ ");
//		appl.currentJobDone = true;
		}
}
