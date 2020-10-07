package it.unibo.HealthAdapter.Clients;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.Mono;

public class EVENSimulator {
	private static String hostaddr = "localhost:8080";
	private static WebClient webClient = WebClient
	    	  .builder()
	    	  .build();

	public static void do_importPatient() {
		String args   = "{\"pid\": \"12345\"}";
		Mono<String> answer = webClient.put()
		.uri( hostaddr+"/importPatient" )   
		.contentType(MediaType.APPLICATION_JSON)
		.body( Mono.just(args), String.class )
        .retrieve()
        .bodyToMono(String.class);
		
		System.out.println( answer.block() );
	}
	
	
    public static void main(String[] args)   {
    	do_importPatient( );
//    	HealthService.delay(10000);
    	System.out.println( "BYE");		
    }


}
