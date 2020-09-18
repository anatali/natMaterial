/*
 * ------------------------------------------------------------------------
 * Utility to be used internally (within the HealthProduct or appl)
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;	
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
 

public class FhirServiceClient {
	private String serverBase    =  ""; //"https://hapi.fhir.org/baseR4";  http://example.com/fhirBaseUrl
	public static final String templateFileName   = 
			"src/main/java/it/unibo/HealthResource/datafiles/ADT_A01.hbs";
	String templateJsonStr 		= HttpFhirSupport.readFromFileJson(templateFileName);

	private String  cvtHostAddr  = "localhost:3000/hl7tofhir";//"localhost:2019/api/convert/hl7v2";
     // Create a client. 
	// See  https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
	//      https://hapifhir.io/hapi-fhir/docs/client/examples.html
 	IGenericClient client; // = HealthService.fhirctx.newRestfulGenericClient(serverBase);
   	
    /*
    -------------------------- 
    CLIENTS
    -------------------------- 
     REFERENCES:	
 	 For WebClient, see https://howtodoinjava.com/spring-webflux/webclient-get-post-example/ 	
	 In each method, the type of result  is MonoFlatMapMany, described in
	 https://www.javatips.net/api/reactor-core-master/src/main/java/reactor/core/publisher/MonoFlatMapMany.java	 
    */ 

 	private WebClient webClient = WebClient
	    	  .builder()
//	    	  .filters(exchangeFilterFunctions -> {
//	    	      exchangeFilterFunctions.add(logRequest());
//	    	      exchangeFilterFunctions.add(logResponse());
//	    	  })
	    	  .build();
  	
 	public FhirServiceClient( String serverBase ) {
 		this.serverBase = serverBase;
 		client 			= HealthService.fhirctx.newRestfulGenericClient(serverBase);
 		System.out.println("FhirServiceClient created for " + serverBase  );
  	}
 	
/*
 * =========================================================================
 * SYNCH   
 * =========================================================================
*/	 	
//  CREATE
 	public Long createSynch( IBaseResource theResource ) {
		MethodOutcome outcome = client
					.create()
					.resource(theResource)
					.execute(); 
		IIdType id = outcome.getId();
		Long idVal = id.getIdPartAsLong();
		System.out.println("FhirServiceClient | createSynch patient ID: " + id + " value=" + idVal );
		return idVal;		
 	}	
 	
//READ 	
 	public DomainResource readResourceSynch( Class<? extends DomainResource> clazz , String id ){
 	  DomainResource resource   = client
 		.read()
		.resource( clazz )
		.withId(id)
		.execute(); 
		return resource  ;
 	}

//SEARCH
 	//https://hapi.fhir.org/baseR4/Patient/?address=Cesena&address=Italy
 	public Bundle searchResourceSynch( String resourceType, String queryStr) { 	
 		String searchUrl = serverBase+"/"+resourceType+"/?"+queryStr;	
 		System.out.println("FhirServiceClient | searchResourceSynch searchUrl=" + searchUrl);
 		Bundle response = client  //we are NOT using the fluent method calls to build the URL
				   .search()			
				   .byUrl(searchUrl)
				   .returnBundle(Bundle.class)
 				   .execute();	
 		return response;
 	}
 	
 	public Bundle searchResourceSynchPost( String resourceType, String queryStr) { 	
// 		queryStr like address=Cesena&address=Italy	
 		System.out.println("FhirServiceClient | searchResourceSynchPost " + queryStr );
		// Costruisce la query come una Map (chiave-valore)
		Map<String, List<String>> query = new HashMap<>();
		String[] qsplit = queryStr.split("&");
		for( int i=0; i<qsplit.length; i++ ) {
			String pair[] = qsplit[i].split("=");
			query.put(pair[0], 	Arrays.asList(pair[1]));
		}
 		Bundle response = client  //we are NOT using the fluent method calls to build the URL
				   .search()			
				   .forResource(resourceType)
				   .whereMap(query)
				   .usingStyle(SearchStyleEnum.POST) // Forza il metodo HTTP a POST
				   .returnBundle(Bundle.class)
 				   .execute();	
 		return response;
 	}
 	
 	
//UPDATE 
 	public String updateResourceSynch(   DomainResource newresource   ) {
 		MethodOutcome response = client
				   .update()			
				   .resource(newresource)	//id injected?
 				   .execute();	
 		return "uodate done synch (from FhirServiceClient) "  + response.getId()  ;
 		
   	}
 	
//DELETE
 	public String deleteResourceSynch( String resourceType, String id ){
		MethodOutcome response = client
				   .delete()
				   .resourceById( new IdType(resourceType, id) )
				   .execute();		 
		//TODO: useful response attributes??
		return "delete " + id + " done synch (from FhirServiceClient) "  + response.getId()  ;
 	}
 	
/*
* =========================================================================
* CRUD - ASYNCH   
* =========================================================================
*/	
    /*
    -------------------------- 
    CREATE
    -------------------------- 
    */ 
	public  Flux<String> createAsynch(  String resJson  )  {			
		String resourceType = HealthService.getResourceType(resJson);  
		System.out.println( "FhirServiceClient createAsynch resourceType=" + resourceType );
		String addr = serverBase+"/"+resourceType;  
    	System.out.println("FhirServiceClient | createAsynch addr=" + addr);
     	
		Flux<String> result = webClient.post()
				.uri( addr )
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body( Mono.just(resJson), String.class )	//Since we send a string
 				.retrieve()
				.bodyToFlux(String.class);	//Since we receive a string
		return	result;		
		//return selfMadeAsynch( resourceType, "POST",   resJson );  //An old version, before to use webClient
 	}
	
	
    /*
    -------------------------- 
    READ
    -------------------------- 
    */ 
	public Flux<String> readResourceAsynch( String resourceType, String id ) {
		String addr = serverBase+"/"+resourceType+"/"+id;  
    	System.out.println("FhirServiceClient | readPatientAsynch addr=" + addr);
    	Flux<String> result = webClient.get()
				.uri( addr )   
                .retrieve() 
                .bodyToFlux(String.class);
  		return result; 
 	}
	
    /*
    -------------------------- 
    SEARCH
    -------------------------- 
    */ 
 	public Flux<String> searchResourceAsynch(String resourceType, String queryStr) {
		//GET [base]/[type]?name=value&...{&_format=[mime-type]}}
		String addr = serverBase+"/"+resourceType+"/?"+queryStr;  
	   	System.out.println("FhirServiceClient | searchResourceAsynch addr=" + addr);
	    Flux<String> result = webClient.get()
				.uri( addr )   
                .retrieve() 
                .bodyToFlux(String.class);
	   	System.out.println("FhirServiceClient | searchResourceAsynch result=" + result); //result -> MonoFlatMapMany
 		return result; 
	}
 	
 	
     /*
    -------------------------- 
    UPDATE
    -------------------------- 
    */ 
 	// a Resource with an id element that has an identical value to the [id] in the URL.
 	public Flux<String> updateResourceAsynch( String resourceId, String newresourceJsonStr ) {
		String resourceType = HealthService.getResourceType(newresourceJsonStr);  
		System.out.println( "FhirServiceClient updateResourceAsynch resourceType=" + resourceType );
		String addr = serverBase+"/"+resourceType+"/"+resourceId;  
    	System.out.println("FhirServiceClient | updateResourceAsynch addr=" + addr);
     	
		Flux<String> result = webClient.put()
				.uri( addr )
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.body( Mono.just(newresourceJsonStr), String.class )	//Since we send a string
 				.retrieve()
				.bodyToFlux(String.class);	//Since we receive a string
		return	result;		
 		
 	}
 	
    /*
    -------------------------- 
    DELETE
    -------------------------- 
    */ 
	public Flux<String> deleteResourceAsynch( String resourceType, String id ){
//		return doAsynch( resourceType, "DELETE",   id );
		String addr = serverBase+"/"+resourceType+"/"+id;  
    	System.out.println("FhirServiceClient | deleteResourceAsynch addr=" + addr);
    	Flux<String> result = webClient.delete()
				.uri( addr )   
                .retrieve() 
                .bodyToFlux(String.class);
  		return result; 
		
	}
	
/*
 * =========================================================================
 * CONVERT 
 * =========================================================================
*/	
//  	public  Flux<String> cvthl7tofhir( String template, String data ) {
//  		//addr=localhost:2019/api/convert/hl7v2/ADT_A01.hbs
//  		String addr = cvtHostAddr + "/"  + template; 
//  		System.out.println("FhirServiceClient |  cvthl7tofhir addr = " + addr + " data=" +  data ) ;
//    	Flux<String> flux = webClient.post()
//				.uri( addr )   
//				.contentType(MediaType.TEXT_PLAIN)
//				.body( Mono.just(data), String.class )
//                .retrieve()
//                .bodyToFlux(String.class);
//		return flux;		//Restituisce la conversione in Json dei data
//		 
//		//return Flux.just( strbuild.toString() );
// 	}
  	public  Flux<String> cvthl7tofhir( String templateFileName, String hl7data ) {
   		String addr 		   = cvtHostAddr ; 
  		//String f = "C:/Progetti/natmaterial/HealthAdapterFacade/src/main/resources/templatesHbs/" +templateFileName;
  		String f = "./src/main/resources/templatesHbs/" +templateFileName;
 
   		String templateStr     = HttpFhirSupport.readFromFileJson(f);  
  		
  		
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
		 
		//return Flux.just( strbuild.toString() );
 	}

	
	
	
	
	
/*
 * ----------------------------------------------------------------------------------------------
 * 	A user-defined asynch interaction, by AN
 * ----------------------------------------------------------------------------------------------
 */
	public  Flux<String> selfMadeAsynch( String resourceType, String method, String body  )  { //method=POST/DELETE
		String uri         = serverBase+"/"+resourceType;
		String contentType = "application/json";  //utf-8" "plain/text; utf-8"
//		System.out.println( "post " + uri +" body=" + resJson + " contentType=" + contentType);
		System.out.println( "FhirServiceClient doAsynch " + uri + " contentType=" + contentType);
		try {
			URL url = new URL(uri);
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod( method );
			con.setRequestProperty("Content-Type", contentType);
 			con.setRequestProperty("Accept", contentType);
			con.setDoOutput(true);
			System.out.println( "FhirServiceClient postAsynch " + url +" con=" + con);
//create a flux for the body that reads the answer and propagates it	 
		 Flux<String> myflux = Flux.push( (Consumer<? super FluxSink<String>>) sink -> {
		   new Thread() {
			public void run() {
				try {
					//write the body
		 			OutputStream os = con.getOutputStream();
					byte[] input    = body.getBytes("utf-8");
					os.write(input, 0, input.length);

					int status = con.getResponseCode();
					System.out.println( "FhirServiceClient postAsynch " + url +" status=" + status);
//READ THE ANSWER
					BufferedReader in = new BufferedReader(new InputStreamReader( con.getInputStream(), "utf-8"));
					String inputLine; 
					while ((inputLine = in.readLine()) != null) {
						//System.out.println( "createAsynch inputLine " + inputLine );
//PROPAGATE
						sink.next( inputLine );
					}//while
					//in.close();	
		 	  	    sink.complete();
				}catch(Exception e) {
					System.out.println( "FhirServiceClient postAsynch thread ERROR:" +e.getMessage() );			
				}		
			}//run
		   }.start();	
		 });
		 return myflux;
		}catch(Exception e) {
			System.out.println( "FhirServiceClient postAsynch ERROR" +e.getMessage() );
 			return null;
		}		
	}

}

