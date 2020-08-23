/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
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
	private String hostaddr = "localhost:8081";
	private WebClient webClient = WebClient
	    	  .builder()
	    	  .build();

	public String prettyJson(String jsonStr) {
 		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			return json.toString(2);
		} catch (JSONException e) {
			return "prettyJson ERROR for"+jsonStr;
			}	 		
 	}
	
	public Flux<String> readResource(String resourcetype, String resourceid) {
		String addr = hostaddr+HealthService.readResourceUri+"/"+resourceid+"&"+resourcetype; 				//+"/_history/1"
 		final StringBuilder strbuild = new StringBuilder();  
    	Flux<String> answer = webClient.get()
				.uri( addr )   
                .retrieve()
                .bodyToFlux(String.class);
    	return answer;
	}
    
 	private void subscribeAndHandleCompletion( String msg, Flux<String> answer ) {
 		System.out.println(msg + " IS BUILDING THE ANSWER ... ");
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> {System.out.println(item ); strbuild.append(item); },
 				error -> System.out.println(msg + " error= " + error ),
 				()    -> System.out.println(msg + prettyJson( strbuild.toString() )) 
 		);			
 	}
 	
 	public Flux<String> startDataflux(  String args  )  { //method=POST 
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
 	
 	public static void  subscribeToDataFlux( Flux<String> dataFlux, String mode  ) {
 		System.out.println("subscribeToDataFlux " + mode);
  		dataFlux.subscribe(			
 				item  -> System.out.println( "dataFlux "+mode+" | " + item ) ,
 				error -> System.out.println( "dataFlux "+mode+" | error" + error ),
 				()    -> System.out.println( "dataFlux "+mode+" | completed"    )				 
 	    );
 	}
 	
 	public void callHealthAdapter()  {
  		Flux<String> dataFluxCold1 = startDataflux(  "cold"  );		
//		Flux<String> dataFluxHot  = startDataflux(  "hot"   );
		String resourcetype ="Patient";
 		String id           ="1439336"; 
 		Flux<String> flux = readResource( resourcetype,id);
		subscribeAndHandleCompletion("read_"+id, flux);
		
 		subscribeAndHandleCompletion("dataFluxCold1 ", dataFluxCold1);
		
	}
 	
    public static void main(String[] args)   {
     	HealthWebClient appl = new HealthWebClient();
    	appl.callHealthAdapter( );
    	HealthService.delay(10000);
    	System.out.println( "BYE");
		
    }
}