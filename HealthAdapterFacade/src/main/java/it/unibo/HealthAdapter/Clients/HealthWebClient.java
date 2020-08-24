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


import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HealthWebClient {
	private static String hostaddr = "localhost:8081";
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
 				item  -> {System.out.println("%%% "+ msg + " "+ item ); strbuild.append(item); },
 				error -> System.out.println(msg + " error= " + error ),
 				()    -> System.out.println(msg + prettyJson( strbuild.toString() )) 
 		);			
 	}
 	
 	public static Flux<String> startDataflux(  String args  )  { //method=POST 
 		System.out.println("startDataflux " + args);
		String addr = hostaddr+HealthService.startDatafluxUri; 				 
    	Flux<String> answer = webClient.post()
				.uri( addr )   
				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just(args), String.class )
                .retrieve()
                .bodyToFlux(String.class);
    	return answer;
    }
 
 	public static Flux<String> subscribeTheDataflux(   )  { //method=POST 
 		System.out.println("subscribeTheDataflux "  );
		String addr = hostaddr+HealthService.subscribehotfluxUri; 				 
    	Flux<String> answer = webClient.post()
				.uri( addr )   
				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just("nothing at the moment"), String.class )
                .retrieve()
                .bodyToFlux(String.class);
    	return answer;
    }
 	
 	public static void doRead() {
		String resourcetype ="Patient";
 		String id           ="1439336"; 
 		Flux<String> flux   = readResource( resourcetype,id);
		subscribeAndHandleCompletion("read_"+id, flux); 		
 	}
	public static Flux<String> readResource(String resourcetype, String resourceid) { 
		String addr = hostaddr+HealthService.readResourceUri+"/"+resourceid+"&"+resourcetype; 	//+"/_history/1"
     	Flux<String> answer = webClient.get()
				.uri( addr )   
                .retrieve()
                .bodyToFlux(String.class);
    	return answer;
	}
 	
 	/*
 	 * subscribeTheDataflux restituisce un hot flux running nel server FHIR
 	 * quando qualcuno fa la subscribe al flusso riceve i dati da quel momento in poi
 	 */
 	public static void workWithHotFlux() {
 		Flux<String> answer  = startDataflux(  "hot"   );
 		subscribeAndHandleCompletion("startDataflux ", answer  ); 
 		HealthService.delay(2000);		//give time to start ...
   		System.out.println( "Subscribe to hot flux ----------------------------");	
   		Flux<String> dataFluxHotA = subscribeTheDataflux();
 		subscribeAndHandleCompletion("thedataFluxHot_a " , dataFluxHotA);
 		HealthService.delay(2000);
   		Flux<String> dataFluxHotB = subscribeTheDataflux();
 		subscribeAndHandleCompletion("thedataFluxHot_b " , dataFluxHotB);		
 	}
 	
 	public static void activateFlux() {
// 		Flux<String> dataFluxHot1  = startDataflux(  "hot"   );		
// 		Flux<String> dataFluxHot2  = startDataflux(  "hot"   );		
  		Flux<String> dataFluxCold1 = startDataflux(  "cold"  );		
  		System.out.println( "Delay for a while ... ");	
 		HealthService.delay(2000);
//		subscribeAndHandleCompletion("dataFluxHot1 " , dataFluxHot1 ); 		
// 		HealthService.delay(1000);
//		subscribeAndHandleCompletion("dataFluxHot2 " , dataFluxHot2 ); 		
  		System.out.println( "Subscribe to cold flux ");	
 		subscribeAndHandleCompletion("dataFluxCold1 ", dataFluxCold1); 		
 		HealthService.delay(1000);
 		
//  		subscribeAndHandleCompletion("dataFluxHot1a " , dataFluxHot1 ); 	
//  		HealthService.delay(2000);
//  		System.out.println( "Subscribe to hot flux AGAIN ----------------------");	
//  		subscribeAndHandleCompletion("dataFluxHot1b " , dataFluxHot1 ); 		// 
 	}
 	
 	public static void callHealthProduct()  {
  		doRead();
// 		activateFlux();
// 		workWithHotFlux();
// 		HealthService.delay(1500);
	}
 	
    public static void main(String[] args)   {
    	callHealthProduct( );
    	HealthService.delay(10000);
    	System.out.println( "BYE");		
    }
}