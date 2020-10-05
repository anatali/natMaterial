package it.unibo.mmhealthAdapterConfig;

/*
 * In this version, we work in a package that has NO subpackags.
 * Thus, in order to import tha application modules (the web modules in particular) into the application context,
 * we use @Import, since we are working in package named mmhealthAdapterConfig and not mmhealthAdapter
 * 
 * 
 */
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import java.util.Arrays;

//import it.unibo.mmhealthAdapterWeb;

//@SpringBootApplication	//EXCLUDED AT THE MOMENT
/*
@Configuration
@ComponentScan	             //scans all packages under the class annotated with this annotation
@EnableAutoConfiguration	//the current package is often used as a 'default', e.g. when scanning for @Entity classes
//It is generally recommended that you place @EnableAutoConfiguration 
//in a root package so that all sub-packages and classes can be searched
*/

@Import( {it.unibo.mmhealthAdapterWeb.HIDemoController.class, it.unibo.mmhealthAdapterWeb.MIDemoController.class} )

public class HealthAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthAdapterApplication.class, args);
	}
	/* 	
	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}
*/ 
}