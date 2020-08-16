package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */
 
 
public class HttpClientHealthAdapter {
 	private String serverBase 			= "http://localhost:8081"; 
 
 	public Long createPatient( String  name ) {
//		String pjson = HttpFhirSupport.readPatientFromFileJson( fname );
//		HttpFhirSupport.getPatientId(pjson);
		String res = HttpFhirSupport.post( serverBase+"/createPatient", name, "plain/text; utf-8" );
 		System.out.println( "createPatient post res=" + res  );
		return Long.parseLong( res );
 	}
	
	public void readPatient(Long id)   {
		String answerJson =  HttpFhirSupport.get( serverBase+"/readResource/"+id );		 
 		//String answerXml  =  get(serverBase+"/readResource/"+id+"?_format=xml");		 //HealthProduct answers in Json only
 		System.out.println( answerJson );
		//System.out.println( answerXml );
//  		JSONObject jo = new JSONObject(answerJson);
// 		String prettyJson = jo.toString(4);
//		System.out.println(prettyJson);
		
	}
	
	public void searchPatient(String patientName) {
		String answerJson =  HttpFhirSupport.get( serverBase+"/searchPatient/"+patientName );		 
 		System.out.println( answerJson );
	}
	
 	public void delete_patient(String id) {
 		String res = HttpFhirSupport.post( serverBase+"/deleteResource", id, "plain/text; utf-8" ); 
		System.out.println("----------------- deletePatient result:" + res );
 	}
 
	public static void main(String[] args) throws Exception {
		HttpClientHealthAdapter appl = new HttpClientHealthAdapter();
 		System.out.println(" %%% CREATE ------------------------------");
  		Long id = appl.createPatient( "ElenaBologna" );		
		System.out.println(" %%% READ  ------------------------------ ");
 		appl.readPatient( id );
// 		appl.readPatient(1435799L);
		System.out.println(" %%% SEARCH ----------------------------- ");
  		appl.searchPatient( "ElenaBologna" ); 
 		System.out.println(" %%% DELETE ----------------------------- ");
  		appl.delete_patient( id.toString() );
		System.out.println(" %%% SEARCH ----------------------------- ");
 	}
}