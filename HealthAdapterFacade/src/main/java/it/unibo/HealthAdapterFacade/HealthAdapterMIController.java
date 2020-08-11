package it.unibo.HealthAdapterFacade;

import org.reactivestreams.Publisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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

     @PostMapping( "/delete" ) 
     public String delete( @RequestBody String id ) {	 
    	 System.out.println("------------------- HealthAdapterMIController delete id="  + id  );
    	 healthService.delete_patient(id);
    	 return "delete Done" + id; 
     }


  
    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HealthAdapterMIController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
 
}

