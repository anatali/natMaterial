package it.unibo.HealthAdapterFacade;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

 
@Controller 
public class HumanInterfaceController { 
     @Value("${human.logo}")
     String appName;
    
    Set<String> robotMoves = new HashSet<String>(); 
    
    public HumanInterfaceController() {
        robotMoves.addAll( Arrays.asList(new String[] {"w","s","h","r","l","z","x"}) );        	
    }

  String applicationModelRep="waiting";
 
  
  //@GetMapping("/") 		  //DONE BY THE MachineInterfaceController
  public String entry(Model model) {
	   //System.out.println("------------------- HumanInterfaceController homePage " + model  );
       model.addAttribute("appName", appName);
      return "welcome";
  } 
/*   
  @PostMapping( path = "/home") 		 
  public String homePage(Model model) {
       return entry(model);
   } 
  
  @GetMapping("/model")
  @ResponseBody
  public String halt(Model model) {
	  model.addAttribute("arg", appName);
      return String.format("HumanInterfaceController text normal state= " + applicationModelRep );      
  }     
	
	
    @RequestMapping(value = "/w", 
    		method = RequestMethod.POST,
    		consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public @ResponseBody String doMove( String move ) {
		applicationModelRep = "w";
        return  "doMove APPLICATION_FORM_URLENCODED_VALUE move="+move+ " Current Robot State:"+applicationModelRep; 
    }    

	@PostMapping( path = "/move" ) 
	public String doMove( 
		@RequestParam(name="move", required=false, defaultValue="h") 
		//binds the value of the query string parameter name into the moveName parameter of the  method
		String moveName, Model viewmodel) {
		if( robotMoves.contains(moveName) ) {
			applicationModelRep = moveName;
			viewmodel.addAttribute("arg", "Current Robot State:"+applicationModelRep);
		}else {
			viewmodel.addAttribute("arg", "Sorry: move unknown - Current Robot State:"+applicationModelRep );
		}		
		return "guiMoves";
	}	
    
	@PostMapping( path = "/movegui" ) 
	public String doHalt( Model viewmodel ) {
 		viewmodel.addAttribute("arg", "Current Robot State:" + applicationModelRep);
		return "guiMoves";		
	}
	

    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"HumanInterfaceController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
*/
}

