package it.unibo.HealthAdapterFacade;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
 


@RestController 	//annotates all the methods with @ResponseBody that embeds the return value in the body of HTTP answer
public class HealthAdapterMIController { 
	private final HealthService healthService;

     public HealthAdapterMIController(HealthService healthService) {this.healthService = healthService; }

     @PostMapping( HealthService.createPatientUri )
     public String create(  ) {
   	    System.out.println("------------------- HealthAdapterMIController createPatient "   );	    
   	    Long id = healthService.create_patient();	
   	    String answer = healthService.read_a_resource(id) ;  
   	    //model.addAttribute("outField", answer);
        return "createPatient Done";
     } 

     @PostMapping( HealthService.deleteResourceUri ) 
     public String delete( @RequestBody String id ) {	 
    	 System.out.println("------------------- HealthAdapterMIController deleteResource id="  + id  );
    	 healthService.delete_patient(id);
    	 return "delete Done" + id; 
     }

//     @GetMapping(path="/searchevent", produces=MediaType.TEXT_EVENT_STREAM_VALUE)	 
//     public Flux<String> searchevent(  ) { //
//   	    System.out.println("------------------- HealthAdapterHIController searchPatient "    );
//    	    //healthService.read_a_resource("987654321");
//   	    String res = healthService.search_for_patients_named("AliceBologna");
//   	    String s   = healthService.prettyFormat(res,2); 
//   	    //System.out.println( s );
//   	    //model.addAttribute("outField", s );
//           return Flux.create(sink -> {	sink.next( s  );   }  );     //sink.complete(); 
//     } 

     @GetMapping( HealthService.searchPatientUri )	 
     public String searchpatient(  ) { //
   	    System.out.println("------------------- HealthAdapterMIController searchPatient AliceBologna "    );
    	String res = healthService.search_for_patients_named("AliceBologna");
   	    String s   = healthService.prettyFormat(res,2); 
   	    //System.out.println( s );
        return s;
     } 
     @GetMapping( HealthService.readResourceUri )	 
     public String readresource(  ) { //@RequestBody Long id 
   	    System.out.println("------------------- HealthAdapterMIController readresource   "    );
    	String res = healthService.read_a_resource( 1432669L );
   	    String s   = healthService.prettyFormat(res,2); 
   	    //System.out.println( s );
        return s;
     } 
     
  
    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HealthAdapterMIController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
 
}

