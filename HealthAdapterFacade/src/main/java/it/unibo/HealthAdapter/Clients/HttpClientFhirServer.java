package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

 
 
public class HttpClientFhirServer {
	private String serverBase="https://hapi.fhir.org/baseR4"; 

	//From https://www.baeldung.com/java-http-request
	public String get(String uri) throws Exception {
		URL url = new URL(uri);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		int status = con.getResponseCode();
		BufferedReader in = new BufferedReader(
		new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		return content.toString();
	}
	
	public void callFhirServer() throws Exception {
 		String htmlPage   = get("https://hapi.fhir.org/baseR4/Patient/1433281");					//html page
		String answerJson = get("https://hapi.fhir.org/baseR4/Patient/1433281?_format=json");		//content in json
		String answerXml  = get("https://hapi.fhir.org/baseR4/Patient/1433281?_format=xml");		//content in xml
		
		System.out.println("----------------- callFhirServer ----------------------");
		System.out.println(htmlPage);
		System.out.println(answerJson);
		System.out.println(answerXml);		
 		//https://www.baeldung.com/java-org-json
// 		JSONObject jo = new JSONObject(answerJson);
// 		String prettyJson = jo.toString(4);
//		System.out.println(prettyJson);
	}
	
	public void callHealthAdapter() throws Exception {
// 		String htmlPage   = get("https://hapi.fhir.org/baseR4"+HealthService.readResourceUri);					//html page
		String answerJson = get("http://localhost:8081/readResource/1433281");		// json
 		String answerXml  = get("http://localhost:8081/readResource/1433281?_format=xml");		//content in xml
		
		System.out.println("----------------- callHealthAdapter ----------------------"); 
 		System.out.println(answerXml);
  		JSONObject jo = new JSONObject(answerJson);
 		String prettyJson = jo.toString(4);
		System.out.println(prettyJson);
	}
	public static void main(String[] args) throws Exception {
		HttpClientFhirServer appl = new HttpClientFhirServer();
		System.out.println(" %%% CREATE ------------------------------");
// 		Long id = appl.createPatient();		
		System.out.println(" %%% READ  ------------------------------ ");
//		appl.readPatient( id );
		//appl.readPatient(1433281L);
		System.out.println(" %%% SEARCH ----------------------------- ");
//		appl.search(Patient.class, "ElenaBologna");
 		System.out.println(" %%% DELETE ----------------------------- ");
//   		appl.deletePatient("Patient", id.toString() );
		System.out.println(" %%% SEARCH ----------------------------- ");
		
		
		
//		appl.callFhirServer();
		appl.callHealthAdapter();
	}
}