package smartEven;

//import org.springframework.http.MediaType;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;

public class haEVENSimulator {
	private static String hostaddr = "http://localhost:8080";
/*	
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
*/	
	public static void do_importPatientHTTP() {
		String answer = HttpSupport.put( 
				hostaddr+"/importPatient", "{\"pid\": \"12345\"}", javax.ws.rs.core.MediaType.APPLICATION_JSON );
		System.out.println( answer  );
	}
	
    public static void main(String[] args)   {
    	do_importPatientHTTP( );
    	System.out.println( "BYE");		
    }


}
