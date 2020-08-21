package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */

import reactor.core.publisher.Flux;

public class HttpClientFhirServer {
	private String serverBase			= "https://hapi.fhir.org/baseR4"; 
	private String patientElenaJson     = "src/main/java/it/unibo/HealthResource/datafiles/PatientElenaJson.txt";//"src/main/java/it/unibo/HealthResource/PatientElenaJson.txt";


	public Long createPatient( ) {
		String pjson = HttpFhirSupport.readFromFileJson( patientElenaJson );
		String res   = HttpFhirSupport.post( serverBase+"/Patient", pjson, "application/json; utf-8" );
 		System.out.println( "createPatient post res=" + res  ); 		
		return HttpFhirSupport.getPatientId(res);
 	}
	public Flux<String> createPatientAsynch( ) {
		String pjson = HttpFhirSupport.readFromFileJson( patientElenaJson );
		Flux<String> res   = HttpFhirSupport.postAsynch( serverBase+"/Patient", pjson, "application/json; utf-8" );
 		System.out.println( "createPatientAsynch post res=" + res  ); 		
		return res;
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
 	
 	
 	public static void main(String[] args) throws Exception {
		HttpClientFhirServer appl = new HttpClientFhirServer();
// 		System.out.println(" %%% CREATE ------------------------------");
//   		Long id = appl.createPatient( );	
//   		System.out.println(" id= " + id );

 		System.out.println(" %%% CREATE ASYNCH ------------------------------");
   		Flux<String> creationflux = appl.createPatientAsynch( );
   		
   		final StringBuilder strbuild = new StringBuilder(); 
   		creationflux.subscribe( 				
   			item  -> { /*System.out.println("-> " + item);*/ strbuild.append(item); },
			error -> System.out.println("error= " + error ),
			()    -> {  appl.answerReceived(strbuild.toString());   }
		);
 		System.out.println("NOW BUILDING THE ANSWER ... ");
 		appl.doingSomethingElse();
// 		Thread.sleep(5000);  //TO AVOID PREMATURE TERMINATION
 		//creationflux.blockLast(); 
  		//System.out.println("readPatient ANSWER:" + HealthService.prettyJson( strbuild.toString() ) );
//  		System.out.println("readPatient ANSWER:" +  strbuild.toString()  );
   		
//		System.out.println(" %%% READ  ------------------------------ ");
// 		appl.readPatient( id );
//		System.out.println(" %%% SEARCH ----------------------------- ");
// 		appl.searchPatient( "ElenaBologna" );
// 		System.out.println(" %%% DELETE ----------------------------- ");
//  		appl.delete_patient( id.toString() );
   
	}
}