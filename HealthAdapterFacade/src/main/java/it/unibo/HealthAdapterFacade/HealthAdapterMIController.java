package it.unibo.HealthAdapterFacade;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@RestController 	//annotates all the methods with @ResponseBody that embeds the return value in the body of HTTP answer
public class HealthAdapterMIController { 
     
    public HealthAdapterMIController() { }


    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HealthAdapterMIController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
 
}

