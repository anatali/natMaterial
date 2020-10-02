package com.example.accessingdatarest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AccessingDataRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccessingDataRestApplication.class, args);
	}
}

/*
curl http://localhost:8080/people	//DB VUOTO
curl -i -H "Content-Type:application/json" -d "{\"firstName\": \"Frodo\", \"lastName\": \"Baggins\"}" http://localhost:8080/people
curl http://localhost:8080/people
curl http://localhost:8080/people/1
curl http://localhost:8080/people/search		//CUSTOM QUERY
curl http://localhost:8080/people/search/findByLastName?name=Baggins

curl -X PUT -H "Content-Type:application/json" -d "{\"firstName\": \"Bilbo\", \"lastName\": \"Baggins\"}" http://localhost:8080/people/1
curl -X PATCH -H "Content-Type:application/json" -d "{\"firstName\": \"Bilbo Jr.\"}" http://localhost:8080/people/1
curl -X DELETE http://localhost:8080/people/1
curl http://localhost:8080/people

curl http://localhost:8080/accounts/send/41/42/500

 * 
 */

