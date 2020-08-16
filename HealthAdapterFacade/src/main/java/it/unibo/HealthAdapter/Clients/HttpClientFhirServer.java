package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */

  
public class HttpClientFhirServer {
	private String serverBase			= "https://hapi.fhir.org/baseR4"; 
	private String patientElenaJson     = "PatientElenaJson.txt";//"src/main/java/it/unibo/HealthResource/PatientElenaJson.txt";


	public Long createPatient( ) {
		String pjson = HttpFhirSupport.readPatientFromFileJson( patientElenaJson );
		String res   = HttpFhirSupport.post( serverBase+"/Patient", pjson, "application/json; utf-8" );
 		System.out.println( "createPatient post res=" + res  ); 		
		return HttpFhirSupport.getPatientId(res);
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
	
 	
 	public static void main(String[] args) throws Exception {
		HttpClientFhirServer appl = new HttpClientFhirServer();
//		System.out.println(" %%% CREATE ------------------------------");
//  		Long id = appl.createPatient( );		
//		System.out.println(" %%% READ  ------------------------------ ");
// 		appl.readPatient( id );
		System.out.println(" %%% SEARCH ----------------------------- ");
 		appl.searchPatient( "ElenaBologna" );
// 		System.out.println(" %%% DELETE ----------------------------- ");
//  		appl.delete_patient( id.toString() );
   
	}
}