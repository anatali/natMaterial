package healthAdapter;
/*
 * We work in a package that is the root of alla application subpackaes.
 * Thus, we can exploit the auto-configuration feature of @SpringBootApplication annotation.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HAApplication {

	public static void main(String[] args) {
		SpringApplication.run(HAApplication.class, args);
	}

}