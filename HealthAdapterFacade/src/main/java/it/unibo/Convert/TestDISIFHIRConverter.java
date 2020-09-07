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
package it.unibo.Convert;


import java.io.FileInputStream;
import java.util.Base64;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import it.unibo.HealthAdapterFacade.FhirServiceClient;
import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TestDISIFHIRConverter {
	private static String hostaddr = "localhost:3000";
	private static WebClient webClient = WebClient
	    	  .builder()
	    	  .build();

	public static String getFromFile(String fileName) {
		try {			
			FileInputStream fis = new FileInputStream(fileName);
		    String str          = IOUtils.toString(fis, "UTF-8");
//		    System.out.println( "getFromFile " + str);
 		    return str;
		} catch (Exception e) {
			System.out.println("getFromFile ERROR"+ e.getMessage() );
			return null;
		}
	}

	public static String prettyJson(String jsonStr) {
 		try {
 			JSONObject json = new JSONObject(jsonStr);
 			return json.toString(2);
		} catch (JSONException e) {
			return "prettyJson ERROR for:"+jsonStr;
		}	 		
 	}
	
    
 	private static void subscribeAndHandleCompletion( String msg, Flux<String> answer ) {
 		System.out.println(msg + " IS BUILDING THE ANSWER ... " + msg);
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> { /* System.out.println("%%% "+ msg + " "+ item ); */ strbuild.append(item); },
 				error -> System.out.println(msg + " error= " + error ),
 				()    -> {
 					System.out.println(msg +    prettyJson( strbuild.toString() ) );
 				}
 		);			
 	}
 	
  
  	public static void doGet( String path ) {
  		Flux<String> flux   = readResource( path );
		subscribeAndHandleCompletion("get_" , flux); 		
 	}
	public static Flux<String> readResource( String path ) { 
		System.out.println( "readResource " + path);
		String addr = hostaddr+ path; 	 
     	Flux<String> answer = webClient.get()
				.uri( addr )   
                .retrieve()
                .bodyToFlux(String.class);
    	return answer;
	}
 	
  	public static void doPost( String path, String data ) {
  		String addr = hostaddr+ path; 
  		System.out.println("doPost addr=" +  addr ) ;
    	Flux<String> flux = webClient.post()
				.uri( addr )   
				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just(data), String.class )
                .retrieve()
                .bodyToFlux(String.class);
    	subscribeAndHandleCompletion("post_" , flux ); 	//.filter( v -> ... )
 	}
  	
  	public static void doPut( String path, String args ) {
  		String addr = hostaddr+ path; 
    	Flux<String> flux = webClient.put()
				.uri( addr )   
				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just(args), String.class )
                .retrieve()
                .bodyToFlux(String.class);
		subscribeAndHandleCompletion("post_" , flux); 		
 	}
  	
  	
 	public  static Flux<String> cvthl7tofhir( String templateFileName, String hl7data ) {
   		String addr 		   = "localhost:3000/hl7tofhir";  
  		String templateStr     = HttpFhirSupport.readFromFileJson(templateFileName);  
  		//System.out.println("FhirServiceClient |  templateStr = " + templateStr  ) ; 
  		System.out.println("FhirServiceClient |  data = " + hl7data  ) ; 
		String encodedTemplate = Base64.getEncoder().encodeToString(templateStr.getBytes());
		String encodedHl7msg   = Base64.getEncoder().encodeToString(hl7data.getBytes());
  		String args  = "{ \"templateb64\" : \"" + encodedTemplate + "\" , \"hl7b64\" : \"" + encodedHl7msg + "\"   }";
  		System.out.println("FhirServiceClient |  cvthl7tofhir addr = " + addr  ) ; //+ " data=" +  data
    	Flux<String> flux = webClient.post()
				.uri( addr )   
				.contentType(MediaType.APPLICATION_JSON)
				.body( Mono.just(args), String.class )
                .retrieve()
                .bodyToFlux(String.class);
		return flux;		//Restituisce la conversione in Json dei data
  	}
  	
  	
    public static void main(String[] args)   {
    	 String templateFileName = "src/main/java/it/unibo/HL7/datafiles/ADT_A01.hbs";
         String hl7_data 		 = getFromFile("src/main/java/it/unibo/HL7/datafiles/ADT01-23.hl7");
         Flux<String> fluxresult = cvthl7tofhir(templateFileName, hl7_data);
         subscribeAndHandleCompletion("" , fluxresult); 	   	
     	HealthService.delay(3000);
    	
    	
/*
 * WARNING:     "resourceType": "Bundle", "type": "transaction" SONO IN FONDO
 	
 */
//    	String xxxtsr 		 = getFromFile("src/main/java/it/unibo/HL7/datafiles/xxx.txt");//   ADT01-23cvtByMicrosoft.txt");
//    	String outS          = prettyJson( xxxtsr );
//    	System.out.println( "BYE" + outS);		
    }
}

