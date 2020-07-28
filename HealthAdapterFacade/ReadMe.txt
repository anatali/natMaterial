// download a file/folder from github
...
https://github.com/eugenp/tutorials/blob/master/spring-5-reactive-2/src/main/java/com/baeldung/webflux/logging/WebFluxLoggingExample.java


gradlew wrapper --gradle-version=6.4.1 --distribution-type=bin


//WebClient docs
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/reactive/function/client/WebClient.html

//Handling responses
If we are only interested in response body entity the using methods retrieve() and then bodyToFlux() and bodyToMono() will serve the purpose.
Else, use method exchange() which will return the ClientResponse which has all the response elements such as status, headers and response body as well.
Please note that bodyToMono() and bodyToFlux() methods always expect a response body of given class type. If response status code is 4xx (client error) or 5xx (Server error) i.e. there is no response body then these methods throw WebClientException.
When using exchange(), we must always use any of the body or toEntity methods of ClientResponse to ensure resources are released and to avoid potential issues with HTTP connection pooling. Use bodyToMono(Void.class) if no response content is expected.



//logging.level.org.springframework.web=OFF
logging.level.org.springframework.web=INFO		
logging.level.root=WARN
logging.level.org.hibernate=ERROR
TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF


To disable the logs, turn off the logging.level in both application.properties and logback-test.xml