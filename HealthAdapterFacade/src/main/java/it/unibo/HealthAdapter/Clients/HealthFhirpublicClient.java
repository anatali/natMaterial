/*
 * ------------------------------------------------------------------------
 * Interagisce con il FHIR server pubblico
 * usando org.springframework.web.reactive.function.client.WebClient.
 * 
 * Goal: trovare esempi di risorse connesse
 * ------------------------------------------------------------------------
 */ 
package it.unibo.HealthAdapter.Clients;


import java.io.IOException;
import java.util.Scanner;

import org.hl7.fhir.r4.model.DomainResource;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import ca.uhn.fhir.model.dstu2.resource.*;
import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class HealthFhirpublicClient {
	private static String serverBase 	= "https://hapi.fhir.org/baseR4";
	private String patientElenaJson		= "src/main/java/it/unibo/HealthResource/datafiles/PatientElenaJson.txt";
 
	private static WebClient webClient = WebClient
	    	  .builder()
	    	  .build();

	public static String prettyJson(String jsonStr) {
 		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			return json.toString(2);
		} catch (JSONException e) {
			return "prettyJson ERROR for:"+jsonStr;
		}	 		
 	}
	
    
 	private static void subscribeAndHandleCompletion( String msg, Flux<String> answer ) {
 		System.out.println(msg + " IS BUILDING THE ANSWER ... ");
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> { /*System.out.println("%%% "+ msg + " "+ item ); */ strbuild.append(item); },
 				error -> System.out.println(msg + " error= " + error ),
 				()    -> System.out.println(msg + prettyJson( strbuild.toString() )) 
 		);			
 	}
 	

 	
	public Long createResource( String resourceType, String fname ) {
		String pjson = HttpFhirSupport.readFromFileJson( fname );
		String res   = HttpFhirSupport.post( serverBase+"/"+resourceType, pjson, "application/json; utf-8" );
 		//System.out.println( "createPatient post res=" + res  ); 		 
 		if( res.length() > 0) return HttpFhirSupport.getResourceId(resourceType, res);
 		else return 0L;
 	}
	
	public void createPatientAsynch( ) {
		String pjson = HttpFhirSupport.readFromFileJson( patientElenaJson );
//  		System.out.println( "createPatientAsynch " + prettyJson(pjson)  ); 		
		Flux<String> res   = HttpFhirSupport.postAsynch( serverBase+"/Patient", pjson, "application/json; utf-8" );
 		subscribeAndHandleCompletion("create", res);
 	}

	public void readPatient(Long id)   {
		String answerJson =  HttpFhirSupport.get( serverBase+"/Patient/"+id+"?_format=json" );		 
 		//String answerXml  =  get(serverBase+"/readResource/"+id+"?_format=xml");		  
 		System.out.println( answerJson );
 	}
	
	public void searchPatient(String patientName) {
		String answerJson =  HttpFhirSupport.get( serverBase+"/Patient/?family:exact=Unibo&_format=json" );		 
		System.out.println("searchPatient answewr= " + HttpFhirSupport.prettyJson(answerJson)  );
	}
	public void searchObservationForPatient(String patientId) {
		String url   = serverBase+"/Observation/?subject=Patient"+"/"+patientId;
		System.out.println("searchObserationForPatient url= " + url );
		//https://hapi.fhir.org/baseR4/Observation/?subject=Patient/1203625
		String answerHtml =  HttpFhirSupport.get( url );		 
		//System.out.println("searchPatient answer= " + answerHtml  );
		//System.out.println("searchPatient answer= " + HttpFhirSupport.prettyJson(answerJson)  );
	}
	
	public void searchId(String resourceType, String id) {
		String url   = serverBase+"/"+resourceType+"/"+id;
		System.out.println("searchId url= " + url );
		String answerHtml =  HttpFhirSupport.get( url );		 
		System.out.println("searchId answer= " + answerHtml  );
		//System.out.println("searchId answer= " + HttpFhirSupport.prettyJson(answerJson)  );
	}
	public void searchResource(String query) {
		String url   = serverBase+"/"+query;
		System.out.println("searchResource query= " + url );
		String answer =  HttpFhirSupport.get( url );
		if( answer.contains("ERROR") ){
			System.out.println("searchResource: "  + answer  );
		}else {
			if ( query.contains("&_format=json") ) answer = HttpFhirSupport.prettyJson(answer);
			System.out.println("searchResource answer= " + answer  );
		}
	}
	
 	public void delete_patient(String id) {
 		String res = HttpFhirSupport.delete( serverBase+"/Patient/"+id  );
		System.out.println( res );
 	}
	
 	public void answerReceived( String answer ) {
 		System.out.println("answerReceived :" +  answer  );
 	}
 	
 	public void doingSomethingElse() throws InterruptedException {
 		for( int i=1; i<=5; i++) {
 	 		System.out.println("doingSomethingElse"    );
 			Thread.sleep(500);
		}
 	}
 	
 	public void waitUserInput() {
 			Scanner in = new Scanner(System.in);
			in.nextLine();
 	}
	
 	public Long createOrganizationHl7() {
 		System.out.println("CREATE ORGANIZATION HL7");
 		Long orgid 		= createResource("Organization","FhirExamples/PatientExample/organization-hl7.json" );
		System.out.println("CREATED orgid= " + orgid );
 		return orgid;
 	}	
 	public Long createOrganization () {
 		System.out.println("CREATE ORGANIZATION (WITH ENDPOINT)");
  		Long orgid 		= createResource("Organization","FhirExamples/PatientExample/organization-example.json" );
   		System.out.println("CREATED orgid= " + orgid );
   		return orgid;
 	} 	
 	public Long createEndPoint() {
 		System.out.println("CREATE END POINT");
 		Long endpointid = createResource("Endpoint","FhirExamples/PatientExample/endpoint-example.json" );
 		System.out.println("CREATED endpointid= " + endpointid );
 		return endpointid;
 	}
 	public Long createPatient() {
 		System.out.println("CREATE PATIENT");
 		Long patientid 	= createResource( "Patient", "FhirExamples/PatientExample/patient-example.json" );	
   		System.out.println("CREATED patientid= " + patientid );
   		return patientid;
 	}
 	
 	public Long createEncounter() {
 		System.out.println("CREATE ENCOUNTER");
 		Long encounterid 	= createResource( "Encounter", "FhirExamples/PatientExample/encounter-example.json" );	
   		System.out.println("CREATED encounterid= " + encounterid );
   		return encounterid;
 		
 	}
 	public Long createObservation() {
 		System.out.println("CREATE OBSERVATION");
 		Long id 	= createResource( "Encounter", "FhirExamples/PatientExample/observation-example.json" );	
   		System.out.println("CREATED encounterid= " + id );
   		return id;
 		
 	}
 	
 	public void patientExample() {
 		//1)
 		createOrganizationHl7();
 		System.out.println("INSERT ORGANIZATION REFERENCE IN ENDPOINT");waitUserInput();
 		//2)
 		createEndPoint();
 		System.out.println("INSERT ENDPOINT REFERENCE IN ORGANIZATION");waitUserInput();
 		//3)
 		createOrganization ();
 		System.out.println("INSERT ORGANIZATION REFERENCE IN PATIENT");waitUserInput();
 		//4)
 		createPatient();
 	} 	
 	
 	public void observationExample() {
 		//1)
 		createEncounter();
 		System.out.println("INSERT ENCOUNTER REFERENCE IN OBSERVATION");waitUserInput();
 		//2)
  		createObservation();
  	}
 	
 	
 	
 	public static void main(String[] args) throws Exception {
 		HealthFhirpublicClient appl = new HealthFhirpublicClient();
		
//  		appl.patientExample();
//  		appl.observationExample();
 		
//  		appl.searchResource( "Patient/1471023?_format=json" );
  		appl.searchResource( "Observation?subject=Patient/1471023&_format=json" );
// 		appl.searchResource( "/Observation?subject:patient.gender=male&_format=json" );
 		

//   		System.out.println(" %%% SEARCH ----------------------------- ");
// 		appl.searchPatient( "ElenaBologna" );
// 		appl.searchId( "Observation", "1203666" );
// 		appl.searchObservationForPatient("1203625");

//		System.out.println(" %%% READ  ------------------------------ ");
// 		appl.readPatient( id );
// 		System.out.println(" %%% DELETE ----------------------------- ");
//  	appl.delete_patient( id.toString() );

// 		System.out.println(" %%% CREATE ASYNCH ------------------------------");
//   	appl.createPatientAsynch( );
// 		appl.doingSomethingElse();
   		
   
	}
}

/*
Observations
 "fullUrl": "http://hapi.fhir.org/baseR4/Observation/1203664",
 "fullUrl": "http://hapi.fhir.org/baseR4/Observation/1203666",
 "fullUrl": "http://hapi.fhir.org/baseR4/Observation/1203667",
http://hapi.fhir.org/baseR4/Observation/1203669
Patient/1203625

*/