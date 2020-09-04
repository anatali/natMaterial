/*
 * ------------------------------------------------------------------------
 * Interagisce con the HealthProduct server
 * usando org.springframework.web.reactive.function.client.WebClient.
 * 
 * Evidenzia che, contraramente a JavaScipt fetch, si possono ottenere 
 * da HealthProduct le informazioni 'on the fly' senza
 * attendere il completamento del Flux.
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapter.Clients;


import java.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class DisicvtClient {
	
	public static final String templateFileName   = 
			"src/main/java/it/unibo/HealthResource/datafiles/ADT_A01.hbs";
	public static final String hl7FileName   = 
			"src/main/java/it/unibo/HealthResource/datafiles/ADT01-23.hl7";
	
	private static String hostaddr = "localhost:3000";
	private static WebClient webClient = WebClient
	    	  .builder()
	    	  .build();

	public static String prettyJson(String jsonStr) {
 		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			return json.toString(2);
		} catch (JSONException e) {
			return "prettyJson ERROR for:"+jsonStr;
		}	 		
 	}
	
    
 	private static void subscribeAndHandleCompletion( String msg, Flux<String> answer ) {
 		System.out.println(msg + " IS BUILDING THE ANSWER ... ");
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> { /* System.out.println("%%% "+ msg + " "+ item ); */ strbuild.append(item); },
 				error -> System.out.println(msg + " error= " + error ),
 				()    -> System.out.println(msg + prettyJson( strbuild.toString() )) 
 		);			
 	}
 	
  
  	
 	public static void doRead() {
		String addr = hostaddr+""; 	 
		System.out.println( "doRead addr=" + addr);
     	Flux<String> answer = webClient.get()
				.uri( addr )   
                .retrieve()
                .bodyToFlux(String.class);
 		subscribeAndHandleCompletion("read_" , answer); 		
 	}
  	
 	public static void doCvt() {
		String addr = hostaddr+"/hl7tofhir"; 	
		String templateJsonStr 	= HttpFhirSupport.readFromFileJson(templateFileName);
		String hl7MsgStr 		= HttpFhirSupport.readFromFileJson(hl7FileName);
		
		String encodedTemplate = Base64.getEncoder().encodeToString(templateJsonStr.getBytes());
		String encodedHl7msg   = Base64.getEncoder().encodeToString(hl7MsgStr.getBytes());

//		System.out.println( "hl7MsgStr="       + hl7MsgStr);
		
 		String args  = "{ \"a\" : \"" + encodedTemplate + "\" , \"b\" : \"" + encodedHl7msg + "\"   }";
     	Flux<String> answer = webClient.post()
				.uri( addr )   
 				.contentType(MediaType.APPLICATION_JSON)
//  				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just( args ), String.class )
                .retrieve()
                .bodyToFlux(String.class);
 		subscribeAndHandleCompletion("cvt_" , answer); 		
 	}
  	
  	
    public static void main(String[] args)   {
    	doCvt();
      	HealthService.delay(3000);
    	System.out.println( "BYE");		
    }
}