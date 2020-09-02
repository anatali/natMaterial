/*
 * ------------------------------------------------------------------------
 * Provides a set of operations defined by the HealthServiceInterface  
 * by using an instance of FhirServiceClient
 * It is used by HealthService when the user selects FHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
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
	
//	SEARCH
	@Override
	public String searchResourceSynch( String queryjsonTemplate ) {
		String[] info  = ResourceUtility.inspect( queryjsonTemplate );
		System.out.println("searchResourceSynch " + info[0]);
		//Bundle answer = fhirclient.searchResourceSynch(info[0], info[1]);
		Bundle answer = fhirclient.searchResourceSynchPost(info[0], info[1]);
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
			return 
				fhirclient.searchResourceAsynch(answer[0], answer[1]);
				//fhirclient.searchResourceAsynchPost(answer[0], answer[1]);  //TODO: ma sarebbe utile?
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
 	
/*
  * =========================================================================
  * CONVERT 
  * =========================================================================
*/  	
	@Override
	public  Flux<String> cvthl7tofhir( String path, String data ) {
 		Flux<String> result = fhirclient.cvthl7tofhir(path, data );		
// 		  Flux<String> flux = Flux.generate(
//  					(sink) -> {
// 						result.subscribe(			
// 				 				item  -> {System.out.println("%%%  "+ item ); sink.next(item); },
// 				 				error -> System.out.println( " error= " + error ),
// 				 				()    -> { sink.complete();
// 				 						   System.out.println( "FhirServiceClient | ANSWER ACQUIRED "  );
// 				 						}
// 								);
// 					});
 	 		return result;
 	}	
 
	@Override 
	public  Flux<String> docvthl7tofhir( String templateFile, String data ) {
		
 		
		doConversion(  templateFile,   data);
		return null;
	} 	
	
	private void doConversion( String templateFile, String data ) {
		System.out.println("HealthServiceFhir | doConversion tamplateFile=" + templateFile + "\n data=" + data  );
		
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine engine = manager.getEngineByName("JavaScript");
			System.out.println("HealthServiceFhir | engine=" + engine );
			// read script file
			String currentDirectory = System.getProperty("user.dir");
			System.out.println("HealthServiceFhir | currentDirectory=" + currentDirectory);
			
			//BufferedReader reader = Files.newBufferedReader(file,Charset.forName("UTF-8"));
			//throw the MalformedInputException
			BufferedReader br = new BufferedReader(new InputStreamReader(	//uses  CharsetDecoder default action
					//new FileInputStream("C:/Progetti/natmaterial/HealthAdapterFacade/DisiFhirConverter/workers/uniboworker.js"),
					new FileInputStream("C:/Progetti/natmaterial/HealthAdapterFacade/DisiFhirConverter/workers/test.js"),
					StandardCharsets.UTF_8 )); //.ISO_8859_1 .US_ASCII
			
			String s1 = "require('fs').readFile( 'C:/Progetti/natmaterial/HealthAdapterFacade/DisiFhirConverter/workers/test.js', 'utf-8', ((err, data) => {}));";
//			engine.eval( br );
			engine.eval(s1);
			
//			engine.eval(Files.newBufferedReader(Paths.get(
//					"C:/Progetti/natmaterial/HealthAdapterFacade/DisiFhirConverter/workers/uniboworker.js")));
			Invocable inv = (Invocable) engine;
			// call function from script file
			String result = "todo";//(String) inv.invokeFunction("callMeFromJava", "a1" );
			System.out.println("HealthServiceFhir | doConversion result=" + result );
		} catch (Exception  e) {
			System.out.println("HealthServiceFhir | error:" + e.getMessage() );
		}
	}

}
