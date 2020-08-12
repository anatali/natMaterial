package it.unibo.HealthAdapterFacade;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.util.HtmlUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

 
@Controller 
public class HealthAdapterHIController { 
 

private final HealthService healthService;

	public HealthAdapterHIController( HealthService healthService ) {
		this.healthService = healthService;
	}

  @GetMapping("/")
  public Publisher<String> entry(Model model) {
	   //System.out.println("------------------- HealthAdapterHIController homePage " + model  );
 	   model.addAttribute("outField", "started" );
       return Mono.just("indexHealthAdapterFacade"  );
  } 

//  @GetMapping("/createPatient")
//  public Publisher<String> create(Model model) {
//	    System.out.println("------------------- HealthAdapterHIController createPatient " + model  );	    
//	    Long id = healthService.create_patient();	
//	    String answer = healthService.read_a_resource(id) ;  
//	    model.addAttribute("outField", answer);
//	    //healthService.delete_patient(""+id );
//        return Mono.just("indexHealthAdapterFacade"  );
//  } 

  @GetMapping("/search")	//NO MORE , produces=MediaType.TEXT_EVENT_STREAM_VALUE
  public String search( Model model ) {
	    System.out.println("------------------- HealthAdapterHIController search "    );
 	    //healthService.read_a_resource("987654321");
	    String res = healthService.search_for_patients_named("AliceBologna");
	    String s   = healthService.prettyFormat(res,2); 
	    //System.out.println( s );
	    model.addAttribute("outField", s );
	    return  "indexHealthAdapterFacade" ;
  } 
  

//  @GetMapping("/searchhhhh")
//  public Publisher<String> read(Model model) {
//	    System.out.println("------------------- HealthAdapterHIController read " + model  );
// 	    //healthService.read_a_resource("987654321");
//	    String res = healthService.search_for_patients_named("AliceBologna");
//	    String s = healthService.prettyFormat(res,2); 
//	    System.out.println( s );
//	    model.addAttribute("outField", s );
//        return Mono.just("indexHealthAdapterFacade"  );
//  } 


}

