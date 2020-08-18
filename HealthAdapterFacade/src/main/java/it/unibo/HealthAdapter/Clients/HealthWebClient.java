package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */
import java.io.IOException;
import java.time.Duration;
import org.springframework.web.reactive.function.client.WebClient;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HealthWebClient {
	private String hostaddr = "https://hapi.fhir.org/baseR4/";
	private WebClient webClient = WebClient
	    	  .builder()
//	    	  .filters(exchangeFilterFunctions -> {
//	    	      exchangeFilterFunctions.add(logRequest());
//	    	      exchangeFilterFunctions.add(logResponse());
//	    	  })
	    	  .build();
	
	public void searchPatient(String id) {
		String addr = "https://hapi.fhir.org/baseR4/Patient/"+id;	//+"/_history/1"
		WebClient webClient = WebClient.create( addr );
        Mono<String> result = webClient.get()
                .retrieve()
                .bodyToMono(String.class);
		String response = result.block();
 		System.out.println(response);		
	}
	
	public void searchPatientFlux(String id) {
		String addr = hostaddr+"/Patient/"+id; //+"/_history/1"
 		final StringBuilder strbuild = new StringBuilder();  
    	Flux<String> result1 = webClient.get()
				.uri( addr )   
                .retrieve()
                .bodyToFlux(String.class);
		System.out.println("searchPatientFlux | result1 ..." + result1 );
		result1.subscribe(  
			item  -> {strbuild.append(item); },
			error -> System.out.println("result1 error= " + error ),
			()    -> System.out.println("result1 done " )   
		);		
		String response = result1.blockLast( Duration.ofMillis(4000));
		System.out.println("WebClientExample | response="+ strbuild);
	   
		//strbuild is a JSON string
	    ObjectMapper mapper = new ObjectMapper();
	    JsonFactory factory = mapper.getFactory();
 		try {
			JsonParser parser  = factory.createParser(strbuild.toString());
		    JsonNode actualObj = mapper.readTree(parser);
		    System.out.println( actualObj.toPrettyString() );
		    //Inspecting the JSON tree
			System.out.println("WebClientExample | resourceType="+ actualObj.get("resourceType"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void callHealthAdapter() throws Exception {}
	public void callFhirServer() throws Exception {}
	
    public static void main(String[] args) throws InterruptedException {
     	HealthWebClient appl = new HealthWebClient();
//    	appl.searchPatient("1432878");
    	appl.searchPatientFlux("1436187");
    }
}