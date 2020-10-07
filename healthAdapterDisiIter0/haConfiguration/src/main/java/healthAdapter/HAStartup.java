package healthAdapter;
/*
 * We work in a package that is the root of alla application subpackaes.
 * Thus, we can exploit the auto-configuration feature of @SpringBootApplication annotation.
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HAStartup {

	public static void main(String[] args) {
		SpringApplication.run(HAStartup.class, args);
	}

}