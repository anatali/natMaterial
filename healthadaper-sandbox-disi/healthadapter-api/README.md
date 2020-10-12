## Health Adapter API

The Health Adapter API is described by a Java iterface using Spring Annotations to model its semantics.
This allows such interface to be used as a base for a Spring `@RestController` as well as a 
[Feign](https://cloud.spring.io/spring-cloud-netflix/multi/multi_spring-cloud-feign.html) client interface.

### Known issue
At the moment request (query) parameter names need to be explicitly specified like so:

`String _import(@RequestParam("identifier") String identifier);`

See [here](https://stackoverflow.com/questions/44313482/feign-client-with-spring-boot-requestparam-value-was-empty-on-parameter-0) 
for an explanation of the reason and [here](https://discuss.gradle.org/t/how-to-set-compiler-options/10188)
for ways to specifies Java compiler options to Java build.