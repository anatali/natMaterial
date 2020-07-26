package it.unibo.HealthAdapterFacade;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

//https://www.booleanworld.com/curl-command-tutorial-examples


@RestController 	//annotates all the methods with @ResponseBody that embeds the return value in the body of HTTP answer
public class MachineInterfaceController { 
    @Value("${machine.logo}")
    String logo;
     
    public MachineInterfaceController() { }

  String applicationModelRep="  | now I'm waiting ...";
 
  
  //http://localhost:8081 
  //curl http://localhost:8081 		//OK -> HealthItelDisi : welcome to m2m interaction   | now I'm waiting ...
  @GetMapping("/") 		 
  public String entry(Model model) {
	  System.out.println("------------------- MachineInterfaceController homePage model=" + model  ); //-> model={}
      return logo + applicationModelRep;	//just a string, no more a view ...
  } 
      
  //http://localhost:8081/model?id=abc
  //curl http://localhost:8081/model?id=abc		//OK -> MachineInterfaceController id = abc fooId=abc
  @GetMapping("/model")  
//@ResponseBody		//No need to use @ResponseBody if you are using @RestController
  public String applmodel(@RequestParam String id, @RequestParam(name = "id") String fooId) {
	  //We can also do @RequestParam(value = “id”) or just @RequestParam(“id”).
      return String.format("MachineInterfaceController id = " + id + " fooId=" + fooId);      
  }     
  
    //curl -d "moveName=left&speed=low" http://localhost:8081/robotmove 	//OK -> move=left speed=low
 	@PostMapping(value = "robotmove", consumes = {"application/x-www-form-urlencoded"})
 	public Mono<String>  doRobotMove( ServerWebExchange exchange ) {	//You can't use @RequestParam annotation
		Mono< MultiValueMap<String, String> > data = exchange.getFormData();
		return data.map( formData -> {
	        String moveArg  = formData.getFirst("moveName");
	        String speedArg = formData.getFirst("speed");
			System.out.println("doRobotMove formData="+formData + " move=" + moveArg + " speed=" + speedArg) ;
			//-> doRobotMove formData={moveName=[left], speed=[low]} move=left speed=low
	        return "move="+moveArg+" speed="+speedArg;  
	    });
	} 

 	//curl -X POST http://localhost:8081/robotmoveold							//OK -> move=null
    //curl --data "moveName=left" http://localhost:8081/robotmoveold 			//OK -> move=left
 	@PostMapping( "/robotmoveold" )
// 	@ResponseBody	//No need to use @ResponseBody if you are using @RestController
	public Mono<String>  doRobotMoveOld( ServerWebExchange exchange ) {	//You can't use @RequestParam annotation
		Mono< MultiValueMap<String, String> > data = exchange.getFormData();
		return data.map( formData -> {
	        String parameterValue = formData.getFirst("moveName");
			System.out.println("doRobotMoveOld formData="+formData + " parameterValue=" + parameterValue);
	        return "move="+parameterValue;  
	    });
	} 
  

    @ExceptionHandler 
    public ResponseEntity<String> handle(Exception ex) {
    	HttpHeaders responseHeaders = new HttpHeaders();
        return new ResponseEntity<String>(
        		"MachineInterfaceController ERROR " + ex.getMessage(), responseHeaders, HttpStatus.CREATED);
    }
 
}

