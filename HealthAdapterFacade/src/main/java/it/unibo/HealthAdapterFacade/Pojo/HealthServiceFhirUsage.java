/*
 * ------------------------------------------------------------------------
 * Utilizza  HealthServiceFhir per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade.Pojo;
 
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
import reactor.core.publisher.Flux;
 

public class HealthServiceFhirUsage {
 	
  	private HealthServiceInterface healthService;
  	private String serverBase="https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";   
  	private String currentResourceType = "Patient";
  	private Long   currentResourceId   = null;
  	private boolean currentJobDone     = false;
  			
 	public HealthServiceFhirUsage() {
 		healthService = new HealthServiceFhir( serverBase ) ;	
  	}
 	/*
 	DEFINED BY HealthServiceInterface
	public Flux<String> createResourceAsynch(String jsonStr);   		 
	public Flux<String> readResourceAsynch(String resourceType,Long id);   		   		 
	public Flux<String> searchResourceAsynch(String jsonTemplate);
	public Flux<String> deleteResourceAsynch( String resourceType, String id );
 	*/
 	
 	public void waitEndOfJob() {
		while( ! currentJobDone  ) {
	 		System.out.println("waiting for END OF THE JOB ... ");
			HealthService.delay(500);
		}
		currentJobDone = false;
 	}
  	
 	private void extractValues( String jsonRep ) {
		try {
			JSONObject jsonobj = new JSONObject( jsonRep );
			String idstr        = jsonobj.getString("id");
			currentResourceType = jsonobj.getString("resourceType");
	  		currentResourceId   = Long.parseLong( idstr );
	  		System.out.println("readPatient id=:" + currentResourceId + " currentResourceType="  + currentResourceType);
		} catch (JSONException e) {
			System.out.println("extractValues ERROR" + e.getMessage() );
		}	 			 				
 	}
 	
 	private void endOfJob( Flux<String> answer ) {
 		System.out.println("HealthServiceFhirUsage | endOfJob  IS BUILDING THE ANSWER ... ");
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> {   strbuild.append(item); },
 				error -> System.out.println("HealthServiceFhirUsage | endOfJob error= " + error ),
 				()    -> {  
 					currentJobDone = true; 
 					System.out.println("HealthServiceFhirUsage | endOfJob " + HealthService.prettyJson( strbuild.toString() ) );  
 				}
 		);			
 	}
 	
	public void createResourceFromFileSynch( String fname ) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		Long id             = healthService.createResourceSynch( jsonRep );
  		currentResourceId = id;
	}

	public void createResourceFromFile( String fname ) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		Flux<String> answer = healthService.createResourceAsynch( jsonRep );
 		final StringBuilder strbuild = new StringBuilder();  
 		System.out.println("createResourceFromFile IS BUILDING THE ANSWER ... ");
		answer.subscribe(			
 				item  -> {   strbuild.append(item); },
 				error -> System.out.println("HealthServiceFhirUsage | readPatient error= " + error ),
 				()    -> {   extractValues( strbuild.toString() );  }
 		);	
   	}
	
	public void readResource( Long id )   {
 		Flux<String> answer  = healthService.readResourceAsynch(currentResourceType,id);
  		endOfJob( answer );
 	}
	
 	public void searchResource(String queryJson) {
 		Flux<String> answer   = healthService.searchResourceAsynch(queryJson);
 		endOfJob( answer );
 	}

 	public void updateResourceFromFile( String fname, Long id ) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		Flux<String> answer = healthService.updateResourceAsynch(   jsonRep  );
   		endOfJob( answer );
 	}

 	public void deleteResource(  String resourceType, Long id ) {
 		Flux<String> answer   = healthService.deleteResourceAsynch(  resourceType, id.toString() );
 		endOfJob( answer );
 	}

	
	
	/*
	 * CRUD - the callback hell
	 */	
	public static void main( String[] args) throws IOException {
		HealthServiceFhirUsage appl = new HealthServiceFhirUsage();
		String resourceFileName       = "src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejson.txt";
		String updateResourceFileName = "src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejsonUpdate.txt";
		String queryStr         = "{ \"resourceType\": \"Patient\", \"address\": { \"city\": \"Cesena\", \"country\": \"Italy\" } }"; 
  
		System.out.println(" %%% CREATE  ------------------------------ ");
//		appl.createResourceFromFile(resourceFileName);
//		//wait until the appl.currentResourceId is updated
//		while( appl.currentResourceId == null ) { 
//	 		System.out.println("waiting for THE ANSWER ... ");
//			HealthService.delay(500);
//		}
		
		appl.createResourceFromFileSynch(resourceFileName);		//updates appl.currentResourceId and stores the id
		
		System.out.println(" %%% READ    ------------------------------ ");
		appl.readResource( appl.currentResourceId );
		appl.waitEndOfJob();
/* 		
		System.out.println(" %%% SEARCH  ------------------------------ ");
		appl.searchResource( queryStr );
		appl.waitEndOfJob();
 		
		System.out.println(" %%% UPDATE  ------------------------------ ");
		appl.updateResourceFromFile(updateResourceFileName, 1439336L);
		appl.waitEndOfJob();
*/ 
		System.out.println(" %%% DELETE  ------------------------------ ");
		appl.currentResourceType = "Patient";
		appl.deleteResource( appl.currentResourceType, appl.currentResourceId );	//
		
		HealthService.delay(2000);  //To avoid premature termination
		
//		System.out.println(" %%% END  ------------------------------ ");
//		appl.currentJobDone = true;
		}
}
