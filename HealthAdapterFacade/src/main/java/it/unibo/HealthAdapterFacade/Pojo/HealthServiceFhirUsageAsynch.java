/*
 * ------------------------------------------------------------------------
 * Utilizza  HealthServiceFhir per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade.Pojo;
 
import java.io.IOException;
import java.util.Scanner;
import java.util.function.Consumer;
import org.hl7.fhir.r4.model.DomainResource;
import org.json.JSONException;
import org.json.JSONObject;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
import it.unibo.HealthResource.ResourceUtility;
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
 				item  -> {   strbuild.append(item); }, //System.out.println(item); 
 				error -> System.out.println("HealthServiceFhirUsage | readResource error= " + error ),
 				()    -> {   
  					callback.accept( HttpFhirSupport.prettyJson( strbuild.toString() )  ); //calls readResourceDone
 				}
 	    );
 	}

	public static void readResourceDone( String resourceRep )   {
		System.out.println("HealthServiceFhirUsage | readResourceDone " + resourceRep );
		//doAfterRead( );  //WARNING: WRONG if we invoke here, we will run it in the callback !!!
 	}
	
/*
 * ===========================================================================	
 * Also avoiding callbacks could be not simple ...
 * ===========================================================================	
 */
	
	private static Scanner scanner = new Scanner(System.in);
	
	private static void forceSequentialBehavior(String move) {
		System.out.println("forceSequentialBehavior after "+move+" > ");
		try {			
			scanner.nextLine();
		} catch (Exception e) {
 			e.printStackTrace();
		}
 	}
	
 	private static void subscribeAndHandleCompletion( Flux<String> answer ) {
 		System.out.println("HealthServiceFhirUsage | subscribeAndHandleCompletion  IS BUILDING THE ANSWER ... ");
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> {  System.out.println("&&&&&&&& " + item); strbuild.append(item); },  //SI VEDE!!!
 				error -> System.out.println("HealthServiceFhirUsage | subscribeAndHandleCompletion error= " + error ),
 				()    -> {  
 					System.out.println("HealthServiceFhirUsage | subscribeAndHandleCompletion " + HealthService.prettyJson( strbuild.toString() ));  //  
 				}
 		);			
 	}
 	
	
	public static void doAfterRead() {
		System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%"  );
		System.out.println("HealthServiceFhirUsage | doAfterRead " + currentResourceType + "/" + currentResourceId  );
		
		
		System.out.println(" %%% SEARCH  ------------------------------ " );
		searchResource( queryStr );
  		forceSequentialBehavior("SEARCH");		
  		/*
		System.out.println(" %%% UPDATE  ------------------------------ " + currentResourceId);
		updateResourceFromFile(updateResourceFileName, currentResourceId);
		forceSequentialBehavior("UPDATE");

 		System.out.println(" %%% DELETE  ------------------------------ " + currentResourceType +"/"+currentResourceId);
  		deleteResource( currentResourceType, currentResourceId );	//
		forceSequentialBehavior("DELETE");
*/
	}
	
	
 	public static void searchResource(String queryJson) {
 		Flux<String> answer   = healthService.searchResourceAsynch(queryJson);
 		subscribeAndHandleCompletion( answer );
 	}

 	public static void updateResourceFromFile( String fname, Long id ) {
 		DomainResource newresource = ResourceUtility.createResourceFromFileJson( fname );
 		//Inject the id
 		ResourceUtility.injectId(newresource, id.toString() );
 		String newresourceJsonStr =  ResourceUtility.getJsonRep(newresource);
		Flux<String> answer = healthService.updateResourceAsynch(   newresourceJsonStr  );
   		subscribeAndHandleCompletion( answer );
 	}

 	public static void deleteResource(  String resourceType, Long id ) {
 		Flux<String> answer   = healthService.deleteResourceAsynch(  resourceType, id.toString() );
  		subscribeAndHandleCompletion( answer );
 	}

	
 	public static void  subscribeToDataFlux( Flux<String> dataFlux, String mode  ) {
  		dataFlux.subscribe(			
 				item  -> System.out.println( "dataFlux "+mode+" | " + item ) ,
 				error -> System.out.println( "dataFlux "+mode+" | error" + error ),
 				()    -> System.out.println( "dataFlux "+mode+" | completed"    )				 
 	    );
 	}
	
/*
 * CRUD
*/	
 	public void doCrudOperations() {
		Flux<String> dataFluxCold = ResourceUtility.startDataflux(  "cold"  );		
		Flux<String> dataFluxHot  = ResourceUtility.startDataflux(  "hot"  );
 		subscribeToDataFlux(dataFluxHot,"hot1");
		
//CREATE & READ will be done using callbacks  
		System.out.println(" %%% CREATE & READ ------------------------------ ");
		createResourceFromFile(resourceFileName, HealthServiceFhirUsageAsynch::createResourceDone   );
   		forceSequentialBehavior("CREATE");		
  		
		subscribeToDataFlux(dataFluxCold,"cold");
 		subscribeToDataFlux(dataFluxHot,"hot2");
 		
//SEARCH, UPDATE & DELETE will forced to work in sequence  		
  		doAfterRead( );
		
 	}
 	
 	public void doSearchOnly() {
		System.out.println(" %%% SEARCH  ------------------------------ " );
		searchResource( queryStr );
  		forceSequentialBehavior("SEARCH");		 		
 	}
 	
 	public void doReadOnly() {
		System.out.println(" %%% READ  ------------------------------ " );
 		Flux<String> answer  = healthService.readResourceAsynch("Patient",1439336L);
 		final StringBuilder strbuild = new StringBuilder();  
		answer.subscribe(			
 				item  -> {   System.out.println("%%%%% "+item); strbuild.append(item); }, // 
 				error -> System.out.println("HealthServiceFhirUsage | readResource error= " + error ),
 				()    -> {   System.out.println( strbuild.toString() );	}
 	    );
		forceSequentialBehavior("READ");	
 	}
	
 	
	public static void main( String[] args) throws IOException {
		HealthServiceFhirUsageAsynch appl = new HealthServiceFhirUsageAsynch();		
//		appl.doCrudOperations();
		appl.doReadOnly();
//		appl.doSearchOnly();
		System.out.println(" %%% END  ------------------------------ ");
	}
}
