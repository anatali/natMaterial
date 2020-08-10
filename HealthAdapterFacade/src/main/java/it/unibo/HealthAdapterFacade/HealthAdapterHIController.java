package it.unibo.HealthAdapterFacade;

import org.reactivestreams.Publisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

 
@Controller 
public class HealthAdapterHIController { 
 
  @GetMapping("/")
  public Publisher<String> entry(Model model) {
	   //System.out.println("------------------- HumanInterfaceController homePage " + model  );
       //model.addAttribute("appName", appName);
       return Mono.just("Welcome"  );
  } 
 }

