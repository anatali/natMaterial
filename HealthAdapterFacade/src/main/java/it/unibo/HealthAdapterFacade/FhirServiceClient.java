package it.unibo.HealthAdapterFacade;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.function.Consumer;

import org.hl7.fhir.instance.model.api.IBaseOperationOutcome;
/*
 * ------------------------------------------------------------------------
 * Utility to be used internally (within the HealthProduct or appl)
 * ------------------------------------------------------------------------
 */
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodyUriSpec;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import it.unibo.HealthResource.PatientResource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;

public class FhirServiceClient {
	private String serverBase=""; //"https://hapi.fhir.org/baseR4";  http://example.com/fhirBaseUrl
	private FhirContext fhirctx ;  
    // Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
 	IGenericClient client ; //= ctx.newRestfulGenericClient(serverBase);
	private WebClient webClient = WebClient
	    	  .builder()
//	    	  .filters(exchangeFilterFunctions -> {
//	    	      exchangeFilterFunctions.add(logRequest());
//	    	      exchangeFilterFunctions.add(logResponse());
//	    	  })
	    	  .build();
  	
 	public FhirServiceClient( String serverBase ) {
 		this.serverBase = serverBase;
  		fhirctx         = HealthService.fhirctx;  //to avoid redundant creation FhirContext.forR4();
 		client          = fhirctx.newRestfulGenericClient(serverBase);
		System.out.println("FhirServiceClient created for " + serverBase  );
  	}

 	public FhirContext  getFhirContext() {
 		return fhirctx; 		
 	}
 	
 	public MethodOutcome create( IBaseResource theResource ) {
 		try { 
			MethodOutcome outcome = client
					.create()
					.resource(theResource)
					.execute(); 
	 		return outcome;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient create  ERROR " + e.getMessage());
			return null;
		}
 	}
 	
 	public Long createAndGetId( IBaseResource theResource ) {
 		try { 
			MethodOutcome outcome = client
					.create()
					.resource(theResource)
					.execute(); 
			// Log the ID that the server assigned
			IIdType id = outcome.getId();
			Long idVal = id.getIdPartAsLong();
			System.out.println("createAndGetId patient ID: " + id + " value=" + idVal );
			return idVal;		
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient createAndGetId  ERROR " + e.getMessage());
			return 0L;
		}
 	}
	
 	public DomainResource read( Class<DomainResource> resourceClass, Long id ) {
 		try { 
	 		DomainResource resource   = client.read()
				.resource( resourceClass )
				.withId(id)
				.execute(); //Construct a read for the given resource type 
	 		return resource;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient read " + id + " ERROR " + e.getMessage());
			return null;
		}
 	}
 	
 	public String readInJson( Class<DomainResource> resourceClass, Long id ) {
 		DomainResource r = read(resourceClass, id);
 		return HealthService.cvtJson( r );
 	}
 	public String readInXml( Class<DomainResource> resourceClass, Long id ) {
 		DomainResource r = read(resourceClass, id);
 		return HealthService.cvtXml( r );
 	}
 	
 	public Patient readPatient( Class<Patient> resourceClass, Long id ) {
 		try { 
	 		Patient resource   = client.read()
				.resource( resourceClass )
				.withId(id)
				.execute(); //Construct a read for the given resource type 
	 		return resource;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient readPatient " + id + " ERROR " + e.getMessage());
			return null;
		}
 	}
 	
 	public Bundle search( Class<DomainResource> resourceClass, String name ) { 
 		try { 
	 		Bundle results = client
	 				.search()
	 				.forResource( resourceClass  )
	 				.where(Patient.NAME.matches().value(name))
	 				.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
	 				.execute();  
	 		return results;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient search " + name + " ERROR " + e.getMessage());
			return null;
		}
	}
 	/*	TO UNDERSTAND	
	// Load the next page (???)
		try {
		org.hl7.fhir.r4.model.Bundle nextPage = client
			.loadPage()
			.next(results)
			.execute();
		if( nextPage != null ) {
			System.out.println("Next page: ");
			String res1 = ctx.newXmlParser().encodeResourceToString(nextPage);
			return res1;
		}else  return res;

		}catch( Exception e) {
			System.out.println("WARNING: " + e.getMessage() );
			return res;
		}
*/ 		

 	
	public Bundle searchPatient( Class<Patient> resourceClass, String name) {
 	try { 
 		Bundle results = client
 				.search()
 				.forResource( resourceClass  )
 				.where(Patient.NAME.matches().value(name))
 				.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
 				.execute(); 
 		return results;
	} catch ( Exception e) {	//ResourceNotFoundException
		System.out.println("FhirServiceClient searchPatient  ERROR " + e.getMessage());
		return null;
	}
	}
	
	public String delete(String className, String id) {
// 	try { 
		MethodOutcome response = client
		   .delete()
		   .resourceById(new IdType(className, id))
		   .execute();
//		OperationOutcome outcome = (OperationOutcome) response.getOperationOutcome();	
		//See https://www.hl7.org/fhir/operationoutcome.html
		return "FhirServiceClient delete terminated " + response;
		//return response.getOperationOutcome();
//	} catch ( Exception e) {	//ResourceNotFoundException
//		return "FhirServiceClient delete  ERROR " + e.getMessage() ;		
//	}
	}
	
/*
 * ASYNCH	
 */
	public Flux<String> readPatientAsycnh(String id) {
		String addr = serverBase+"/Patient/"+id;  
    	System.out.println("FhirServiceClient | addr=" + addr);
    	Flux<String> result = webClient.get()
				.uri( addr )   
                .retrieve() 
                .bodyToFlux(String.class);
    	//https://www.javatips.net/api/reactor-core-master/src/main/java/reactor/core/publisher/MonoFlatMapMany.java
		//MonoFlatMapMany
  		return result; 
 	}
	
// 	public void createAsynch( IBaseResource theResource ) {
//		String addr = serverBase+" " ;  
//    	System.out.println("FhirServiceClient | addr=" + addr);
//    	RequestBodySpec result =  webClient.post()  
//				.uri( addr ) ;
//    	//https://www.javatips.net/api/reactor-core-master/src/main/java/reactor/core/publisher/MonoFlatMapMany.java
//		//MonoFlatMapMany
//  		//return result;  	
//    	System.out.println("FhirServiceClient createAsynch  result= " + result);
// 	}
	public  Flux<String> createAsynch( String resJson  )  { 
		String uri         = serverBase+"/Patient";
		String contentType = "application/json";  //utf-8" "plain/text; utf-8"
//		System.out.println( "post " + uri +" body=" + resJson + " contentType=" + contentType);
		System.out.println( "createAsynch " + uri + " contentType=" + contentType);
		try {
			URL url = new URL(uri);
			final HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", contentType);
 			con.setRequestProperty("Accept", contentType);
			con.setDoOutput(true);
			System.out.println( "createAsynch " + url +" con=" + con);
//create a flux for the body		 
		 Flux<String> myflux = Flux.push( (Consumer<? super FluxSink<String>>) sink -> {
		   new Thread() {
			public void run() {
				try {
					//write the body
		 			OutputStream os = con.getOutputStream();
					byte[] input    = resJson.getBytes("utf-8");
					os.write(input, 0, input.length);

					int status = con.getResponseCode();
					System.out.println( "createAsynch " + url +" status=" + status);
//READ THE ANSWER
					BufferedReader in = new BufferedReader(new InputStreamReader( con.getInputStream(), "utf-8"));
					String inputLine; 
					while ((inputLine = in.readLine()) != null) {
						//System.out.println( "createAsynch inputLine " + inputLine );
						sink.next( inputLine );
					}//while
					//in.close();	
		 	  	    sink.complete();
				}catch(Exception e) {
					System.out.println( "createAsynch thread ERROR:" +e.getMessage() );			
				}		
			}//run
		   }.start();	
		 });
		 return myflux;
		}catch(Exception e) {
			System.out.println( "createAsynch ERROR" +e.getMessage() );
 			return null;
		}		
	}
	
 	
/*
 * CONVERSIONS	
 */
// 	public String cvt( IBaseResource theResource,  boolean useJson ) {
// 		if( useJson ) return cvtJson(theResource);
// 		else return cvtXml(theResource);
// 	}
// 	public String cvt( Bundle bundle,  boolean useJson ) {
// 		if( useJson ) return fhirctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
// 		else return fhirctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
// 	}
//
// 	public String cvtJson( IBaseResource theResource ) {
// 		return fhirctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(theResource);
// 	}
//
// 	public String cvtXml( IBaseResource theResource ) {
// 		return fhirctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(theResource);
// 	}

}

//public String prettyFormat(String input, int indent) {
//try {
//    Source xmlInput = new StreamSource(new StringReader(input));
//    StringWriter stringWriter = new StringWriter();
//    StreamResult xmlOutput = new StreamResult(stringWriter);
//    TransformerFactory transformerFactory = TransformerFactory.newInstance();
//    transformerFactory.setAttribute("indent-number", indent);
//    Transformer transformer = transformerFactory.newTransformer(); 
//    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    transformer.transform(xmlInput, xmlOutput);
//    return xmlOutput.getWriter().toString();
//} catch (Exception e) {
//	System.out.println("prettyFormat ERROR " + e.getMessage());
//	return "";
// }
//}
