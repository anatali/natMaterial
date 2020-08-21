package it.unibo.HealthAdapterFacade;

 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
 
 


@RestController 	//annotates all the methods with @ResponseBody that embeds the return value in the body of HTTP answer
/*
 * Le risposte di questi metodi vanno ai programmi (come curl o HttpClientHealthAdapter )
 * che effettuano una m2m interaction.
 * Il codice Javascript di indexHealthAdapterFacade.html riceve una risposta alla sua invocazione POST
 * e genera un messaggio di stato per l'utente umano.
 */
public class HealthAdapterMIController { 
	 
	private final boolean usejson = true; 
 	private HealthServiceInterface healthService;
 	
	 @Autowired
     public HealthAdapterMIController( HealthService healthServiceBuilder ) {
  		healthService             = healthServiceBuilder.getdHealthService();
  		System.out.println("----- HealthAdapterMIController CREATED "   );
     }

/*
* =========================================================================
* CRUD - ASYNCH PART  
* =========================================================================
*/	  
		/*
		-------------------------- 
		CREATE
		-------------------------- 
		*/ 			 
	    @PostMapping( HealthService.createResourceUri )
	     public Flux<String> createResourceAsynch( @RequestBody String jsonStr ) {
	  	    //System.out.println("----- HealthAdapterMIController createPatientAsynch " + jsonStr  );	    
	    	//final StringBuilder longstrbuilder = new StringBuilder();
	    	Flux<String> creationflux = healthService.createResourceAsynch(  jsonStr );
	    	return creationflux;
	    } 

	/*
	-------------------------- 
	READ
	-------------------------- 
	*/ 		 
	 //https://projectreactor.io/2.x/reference/
	 //https://projectreactor.io/docs/core/snapshot/reference/
    @GetMapping( HealthService.readResourceUri+"/{id}&{resourceType}" )	 
    public Flux<String> readResourceAsynch(   
    		@PathVariable( value = "id" ) Long resourceId ,
    		@PathVariable( value = "resourceType" ) String resourceType ) {      	    
  	    System.out.println("----- HealthAdapterMIController readPatientAsynch  id= " + resourceId  + " usejson=" + usejson );
  	    Flux<String>  result = healthService.readResourceAsynch( resourceType, resourceId  );	 
  	    //System.out.println("----- HealthAdapterMIController readPatientAsynch result= " + result );
 	    return result;
    } 
	 
	/*
	-------------------------- 
	SEARCH
	-------------------------- 
	*/ 		 
    @GetMapping( HealthService.searchResourceUri+"/{queryjson}" )	 
    public Flux<String> searchResourceAsynch( @PathVariable(value = "queryjson") String queryjson ) { //
  	    System.out.println("----- HealthAdapterMIController searchResourceAsynh  " +  queryjson  );
   	    Flux<String>  b = healthService.searchResourceAsynch(queryjson);
   	    if( b == null )  return Flux.just("sorry, error in searchResourceAsynch");
   	    System.out.println("----- HealthAdapterMIController searchResourceAsynch b:" +  b  );
//   	    b.subscribe(  
//  				item  -> System.out.println("----- HealthAdapterMIController b" + item ) , 
//  				error -> System.out.println("----- HealthAdapterMIController b ERROR= " + error ),
//  				()    -> System.out.println("----- HealthAdapterMIController b done " )   
//  		);	   	     
  	    return b; //Flux.just("please wait ... "); 
    } 

    /*
    -------------------------- 
    UPDATE
    -------------------------- 
    */ 
    @PutMapping( HealthService.updateResourceUri ) 
    public Flux<String> updateResourceAsynch( @RequestBody String newResource ) {	 
   	 	System.out.println("----- HealthAdapterMIController updateResourceAsynch newResource="  + newResource  );  	 	
   	 	return healthService.updateResourceAsynch( newResource );	 
   	 	//return Flux.just("wait a moment ...");
    }
    
    /*
    -------------------------- 
    DELETE
    -------------------------- 
    */ 
    @DeleteMapping( HealthService.deleteResourceUri ) 
    public Flux<String> deleteResourceAsynch( @RequestBody String id ) {	 
   	 	System.out.println("----- HealthAdapterMIController deleteResourceAsynch id="  + id  );
   	 	return healthService.deleteResourceAsynch("Patient", id);	//TODO
    }
    
    
      
  
    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HealthAdapterMIController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
 
}

