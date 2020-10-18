package it.unibo.HealthAdapterFacade;

import java.time.Duration;
import java.time.LocalTime;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import it.unibo.HealthResource.ResourceUtility;
import reactor.core.publisher.Flux;

@RestController 	//annotates all the methods with @ResponseBody that embeds the return value in the body of HTTP answer
/*
 * Le risposte di questi metodi vanno ai programmi (come curl o HttpClientHealthAdapter )
 * che effettuano una m2m interaction.
 * Il codice Javascript di indexHealthAdapterFacade.html riceve una risposta alla sua invocazione POST
 * e genera un messaggio di stato per l'utente umano.
 */
public class HealthAdapterMIController { 
	 
 	private HealthServiceInterface healthService;
	private final boolean usejson       = true; 
 	public static Flux<String> hotflux  = null;
 	
	 @Autowired
     public HealthAdapterMIController( HealthService healthServiceBuilder ) {
  		healthService             = healthServiceBuilder.getdHealthService();
  		System.out.println("----- HealthAdapterMIController CREATED "   );
     }

	 /*
	  * =========================================================================
	  * ITEL20 SYCNH PART  
	  * =========================================================================
	  */		 
	 
/*
 * =========================================================================
 * SYNCH PART  
 * =========================================================================
 */	 
//CREATE SYNCH	 
	 @PostMapping( HealthService.createResourceUriSynch )
	 public String createResourceSynch( @RequestBody String jsonStr ) {
		 String id = healthService.createResourceSynch(  jsonStr );
		 return id;
	 }

//READ SYNCH	
	 @GetMapping( HealthService.readResourceUriSynch+"/{id}&{resourceType}" )	 
	 public  String readResourceSynch(  
	    		@PathVariable( value = "id" ) String resourceId ,
	    		@PathVariable( value = "resourceType" ) String resourceType ) {      	    
	  	  System.out.println("----- HealthAdapterMIController readResourceSynch  id=" + resourceId  + " usejson=" + usejson );
	  	  String answer = healthService.readResourceSynch(  resourceType, resourceId  );  
	  	  System.out.println("----- HealthAdapterMIController answer=" + answer );
	  	  return answer;
	 }

//SEARCH SYNCH	 
	 @GetMapping( HealthService.searchResourceUriSynch+"/{queryjson}" )	 
	 public  String searchResourceSynch( @PathVariable(value = "queryjson") String queryjson ) {
		String answer = healthService.searchResourceSynch(queryjson);
		return answer;
	 }
	 
//UPDATE SYNCH
	@PutMapping( HealthService.updateResourceUriSynch ) 
	public String updateResourceSynch( @RequestBody String newResource ) {	 
		return  healthService.updateResourceSynch( newResource );
	}
	 
//DELETE SYNCH	
	@DeleteMapping( HealthService.deleteResourceUriSynch ) 
	public String deleteResourceSynch( @RequestBody String body ) {	
		String[] args = body.split("&");
		String   resourceType   = args[0];
		String   id   			= args[1];
		System.out.println("----- HealthAdapterMIController deleteResourceSynch resourceType=" + resourceType + " id=" + id  );
		return healthService.deleteResourceSynch( resourceType, id );	 
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
		 //System.out.println("----- HealthAdapterMIController createResourceAsynch " + jsonStr  );	    
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
    		@PathVariable( value = "id" ) String resourceId ,
    		@PathVariable( value = "resourceType" ) String resourceType ) {     
    	
  	    System.out.println("----- HealthAdapterMIController readResourceAsynch  id= " + resourceId  + " usejson=" + usejson );
  	    Flux<String>  result = healthService.readResourceAsynch( resourceType, resourceId  );	 
  	    //System.out.println("----- HealthAdapterMIController readResourceAsynch result= " + result );
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
    public Flux<String> deleteResourceAsynch( @RequestBody String body ) {	
    	String[] args = body.split("&");
    	String   resourceType   = args[0];
    	String   id   			= args[1];
   	 	System.out.println("----- HealthAdapterMIController deleteResourceAsynch resourceType=" + resourceType + " id=" + id  );
   	 	return healthService.deleteResourceAsynch( resourceType, id );	 
    }
    
/*
 * =========================================================================
 * SELECT HEALTH CENTER
 * =========================================================================
*/	  
     

    @PostMapping( HealthService.selectHealthCenterUri  ) 
    public String select( @RequestBody String choichejson ) {	 //, @RequestParam("hct")String hct
   	 System.out.println("----- HealthAdapterMIController select healthService="  + healthService  );
   	 try {
			 JSONObject jo = new JSONObject(choichejson);
			 String choice = (String) jo.get("argchoice"); 
			 String addr   = (String) jo.get("argserveraddr");
	    	 System.out.println("----- HealthAdapterMIController select choiche="  + choice + " addr=" + addr );
	    	 healthService.setHealthService(choice,addr);
	    	 return "select Done" + choice; 
		} catch (JSONException e) {
			String error = "select Done ERROR " + choichejson;
			System.out.println("----- HealthAdapterMIController "  + error  );
			return error; 
		}
    }
    
    
/*
 * =========================================================================
 * DATAFLUX     (experiments)  
 * =========================================================================
*/	  
    @PostMapping( HealthService.subscribehotfluxUri ) 
    public Flux<String> subscribeDataflux(   ) {	 
   	 	System.out.println("----- HealthAdapterMIController subscribeDataflux " + " hotflux=" + hotflux  );
   	 	if( hotflux == null ) return Flux.just("Please start DataStream hot");   	 		
   	 	else return hotflux;  
    }
    
    @PostMapping( HealthService.startDatafluxUri ) 
    public Flux<String> startDataflux( @RequestBody String args ) {	 
   	 	System.out.println("----- HealthAdapterMIController startDataflux args=" + args + " hotflux=" + hotflux);
   	 	if(  args.equals("hot") ) {
   	 		if(   hotflux == null ) hotflux = ResourceUtility.startHotDataflux( );  	 		
   	 		return Flux.just("Hot dataFlux started");
   	 	}
   	 	else return ResourceUtility.startColdDataflux( );
    }
    @PostMapping( HealthService.stopDatafluxUri ) 
    public Flux<String> stopDataflux( @RequestBody String args ) {	 
    	return ResourceUtility.stopDataflux(args);
    }
    
    
/*
 * =========================================================================
 * CONVERSIONS
 * =========================================================================
*/   
    
    @PostMapping( HealthService.cvtHL7ToFHIRUri )  
    public Flux<String> cvtHL7ToFHIRUri( @RequestBody String args  ) {	 
    	//return Flux.just("cvtHL7ToFHIRUri " + template + " " + datahl7 + " wait a minute ...");
    	//System.out.println("----- HealthAdapterMIController cvtHL7ToFHIRUri args=" + args  );
    	String[] split  = args.split( "///" ); 
    	System.out.println("----- HealthAdapterMIController cvtHL7ToFHIRUri template=" + split[1]  );
    	return	healthService.cvthl7tofhir(split[1],split[0]);	
    	//return	healthService.docvthl7tofhir(split[1],split[0]);	//EXPERIMENT with DISI converter
    	
    }
    
    
 
    
/*
 * =========================================================================
 * SERVER SENT EVENTS (experiment)
 * an HTTP standard that allows a web application to handle a unidirectional event stream 
 * and receive updates  whenever server emits data.
 * =========================================================================
*/   
    
    @GetMapping("/sse")
    public Flux<ServerSentEvent<String>> sse() {
        return Flux.interval(Duration.ofSeconds(1))
          .map(sequence -> ServerSentEvent.<String> builder()
            .id(String.valueOf(sequence))
              .event("periodic-event")
              .data("SSE - " + LocalTime.now().toString())
              .build());
    }   
    
    
    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HealthAdapterMIController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
    
}

