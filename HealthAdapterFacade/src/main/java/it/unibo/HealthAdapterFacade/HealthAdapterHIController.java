package it.unibo.HealthAdapterFacade;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.reactivestreams.Publisher;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;

import it.unibo.Handlebars.UniboHandlebars;
import reactor.core.publisher.Mono;
 
@Controller 
public class HealthAdapterHIController { 
 
  @GetMapping("/")
  public Publisher<String> entry(Model model) {
	   //System.out.println("------------------- HealthAdapterHIController homePage " + model  );
 	   model.addAttribute("outField", "started" );
       return Mono.just("indexHealthAdapterFacade"  );
  } 
  
  @GetMapping("/cvt")
  public String index(Model model) {
	  //String s = UniboHandlebars.noTemplateFile_compilesInline();
	  //String s = UniboHandlebars.compilesInline_parameterObject();
	  //String s = UniboHandlebars.templateFile_parameterInMap();
	  String s = UniboHandlebars.usingWith();
	  //String s = UniboHandlebars.templateReference();
	  //String s = UniboHandlebars.usingEach();
	   
      model.addAttribute("info", s);
      return "cvtIndex";
  }
  
  @GetMapping("/react")
  public Publisher<String> entryreact(Model model) {
        return Mono.just("reacHtmlOnlyt0"  );
  } 
  
  
//  @GetMapping("/select/**")	//HTTP GET "/select?healthcenter=FHIR"
//  //public Publisher<String> select(Model model, HttpClientRequest request ) { //, @PathVariable(value = "hct") String healthcenter
//  public Publisher<String> select(Model model, @PathVariable(value = "?healthcenter=FHIR") String healthcenter ) { //
//	   System.out.println("------------------- HealthAdapterHIController select "    );
//	   model.addAttribute("outField", "selected:"+healthcenter );
//      return Mono.just("indexHealthAdapterFacade"  );
// } 
  
//  @GetMapping( HealthService.readResourceUri+"/{id} " )	 
//  public Publisher<String> readresourcehi(   @PathVariable( value = "id"   ) String resourceId ) {      	    
//	    System.out.println("----- HealthAdapterHIController readresource  id= " + resourceId   );
// 	//String res = healthService.read_a_resource( resourceId  );
// 	return Mono.just("indexHealthAdapterFacade"  );
//  } 

//  @GetMapping("/createPatient")
//  public Publisher<String> create(Model model) {
//	    System.out.println("------------------- HealthAdapterHIController createPatient " + model  );	    
//	    Long id = healthService.create_patient();	
//	    String answer = healthService.read_a_resource(id) ;  
//	    model.addAttribute("outField", answer);
//	    //healthService.delete_patient(""+id );
//        return Mono.just("indexHealthAdapterFacade"  );
//  } 

  

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

