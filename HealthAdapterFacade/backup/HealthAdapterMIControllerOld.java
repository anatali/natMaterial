package it.unibo.HealthAdapterFacade;

import org.hl7.fhir.r4.model.Bundle;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
 


@RestController 	//annotates all the methods with @ResponseBody that embeds the return value in the body of HTTP answer
/*
 * Le risposte di questi metodi vanno ai programmi (come curl o HttpClientHealthAdapter )
 * che effettuano una m2m interaction.
 * Il codice Javascript di indexHealthAdapterFacade.html riceve una risposta alla sua invocazione POST
 * e genera un messaggio di stato per l'utente umano.
 */
public class HealthAdapterMIControllerOld { 
	 
	private final boolean usejson = true;
	private HealthService healthServiceBuilder;
 	private HealthServiceInterface healthService;
//	private PatientResource patientresource = new PatientResource();
	
	 @Autowired
     public HealthAdapterMIControllerOld( HealthService healthServiceBuilder ) {
		this.healthServiceBuilder = healthServiceBuilder;
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
	     public Flux<String> createPatientAsynch( @RequestBody String jsonStr ) {
	  	    //System.out.println("----- HealthAdapterMIController createPatientAsynch " + jsonStr  );	    
	    	//final StringBuilder longstrbuilder = new StringBuilder();
	    	Flux<String> creationflux = healthService.createPatientAsynch(  jsonStr );
	    	return creationflux;
	    } 

	/*
	-------------------------- 
	READ
	-------------------------- 
	*/ 		 
	 //https://projectreactor.io/2.x/reference/
	 //https://projectreactor.io/docs/core/snapshot/reference/
    @GetMapping( HealthService.readResourceUri+"/{id}" )	 
    public Flux<String> readPatientAsynch(   @PathVariable( value = "id" ) Long resourceId ) {      	    
  	    System.out.println("----- HealthAdapterMIController readPatientAsynch  id= " + resourceId  + " usejson=" + usejson );
  	    Flux<String>  result = healthService.readPatientAsynch( resourceId  );
  	    //System.out.println("----- HealthAdapterMIController readPatientAsynch result= " + result );
 	    return result;
    } 
	 
	/*
	-------------------------- 
	SEARCH
	-------------------------- 
	*/ 		 
    @GetMapping( HealthService.searchResourcetUri+"/{queryjson}" )	 
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
    
    /*
    -------------------------- 
    DELETE
    -------------------------- 
    */ 
    @DeleteMapping( HealthService.deleteResourceUri ) 
    public Flux<String> deleteResourceAsynch( @RequestBody String id ) {	 
   	 	System.out.println("----- HealthAdapterMIController deleteAResource id="  + id  );
   	 	return healthService.deleteResourceAsynch("Patient", id);
    }
    
    
/*
 * -------------------------------------------------------------------------
 * OLD PART  (SYNCH , TO BE REFACTORED IN ANY CASE)
 * -------------------------------------------------------------------------
 */
     @PostMapping( HealthService.createPatientSynchUri )
     //public String create( @RequestBody String name ) {
     public Mono<String> createPatientSynch( @RequestBody String name ) {
   	    System.out.println("----- HealthAdapterMIController createPatient " + name  );	    
   	    Long id = healthService.create_patient( null, name );	
   	    if( id == 0 ) return Mono.just("createError");	
//   	    String answer = healthService.read_a_resource(id) ;  
//   	    System.out.println("----- HealthAdapterMIController createPatient answer= " + answer  );	
        return Mono.just(""+id); //answer;	
     } 

     @PostMapping( HealthService.deleteResourceUri ) 
     public String delete( @RequestBody String id ) {	 
    	 System.out.println("----- HealthAdapterMIController deleteResource id="  + id  );
    	 String res = healthService.delete_patient(id);
    	 return res; 
     }

     @PostMapping( HealthService.selectHealthCenterUri  ) 
     public String select( @RequestBody String choichejson ) {	 //, @RequestParam("hct")String hct
    	 System.out.println("----- HealthAdapterMIController select choiche="  + choichejson  );
    	 try {
			 JSONObject jo = new JSONObject(choichejson);
			 String choice = (String) jo.get("argchoice");
			 String addr   = (String) jo.get("argserveraddr");
	    	 System.out.println("----- HealthAdapterMIController select choiche="  + choice + " addr=" + addr );
	    	 healthServiceBuilder.setHealthService(choice,addr);
	    	 return "select Done" + choice; 
		} catch (JSONException e) {
 			String error = "select Done ERROR " + choichejson;
 			System.out.println("----- HealthAdapterMIController "  + error  );
 			return error; 
		}
     }

     @GetMapping( HealthService.searchPatientUri+"/{name}" )	 
     public String searchpatient( @PathVariable(value = "name") String patientName ) { //
   	    System.out.println("----- HealthAdapterMIController searchPatient  " +  patientName  );
    	String res = healthService.search_for_patients_named( patientName, usejson );
//   	    String s   = healthService.prettyFormat(res,2); 
   	    //System.out.println( s );
        return res;
     } 
     
//     @GetMapping( HealthService.readResourceUri+"/{id}" )	 
//     public String readresource(   @PathVariable( value = "id"   ) Long resourceId ) {      	    
//   	    System.out.println("----- HealthAdapterMIController readresource  id= " + resourceId  + " usejson=" + usejson );
//    	String res = healthService.read_a_resource( resourceId  );
//        return res;
//     } 
     
  
    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HealthAdapterMIController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
 
}

