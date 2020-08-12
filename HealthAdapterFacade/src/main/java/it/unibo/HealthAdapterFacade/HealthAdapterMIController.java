package it.unibo.HealthAdapterFacade;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
     public String create( @RequestBody String name ) {
   	    System.out.println("------------------- HealthAdapterMIController createPatient " + name  );	    
   	    Long id = healthService.create_patient( name );	
   	    if( id == 0 ) return "createError";	
//   	    String answer = healthService.read_a_resource(id) ;  
//   	    System.out.println("------------------- HealthAdapterMIController createPatient answer= " + answer  );	
        return ""+id; //answer;	
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

     @GetMapping( HealthService.searchPatientUri+"/{name}" )	 
     public String searchpatient( @PathVariable(value = "name") String patientName ) { //
   	    System.out.println("------------------- HealthAdapterMIController searchPatient  " +  patientName  );
    	String res = healthService.search_for_patients_named( patientName );
   	    String s   = healthService.prettyFormat(res,2); 
   	    //System.out.println( s );
        return s;
     } 
     
     @GetMapping( HealthService.readResourceUri+"/{id}" )	 
     public String readresource(  @PathVariable(value = "id") Long resourceId ) {  
   	    System.out.println("------------------- HealthAdapterMIController readresource  id= " + resourceId   );
    	String res = healthService.read_a_resource( resourceId );
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

