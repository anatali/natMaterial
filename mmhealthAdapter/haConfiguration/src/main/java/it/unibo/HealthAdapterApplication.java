package it.unibo;
/*
 * In this version, we work in a package that is the root of alla application subpackaes.
 * Thus, we can exploit the auto-configuration feature of @SpringBootApplication annotation.
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

@SpringBootApplication
/*
@Configuration
@ComponentScan	             //scans all packages under the class annotated with this annotation
@EnableAutoConfiguration	//the current package is often used as a 'default', e.g. when scanning for @Entity classes
//It is generally recommended that you place @EnableAutoConfiguration 
//in a root package so that all sub-packages and classes can be searched
*/

public class HealthAdapterApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthAdapterApplication.class, args);
	}

}