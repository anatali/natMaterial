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

 
 
public class HttpClientHealthAdapter {
	private String serverBase="http://localhost:8081/"; 
	
	//From https://www.baeldung.com/java-http-request
	public String get(String uri)  {
		try {
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
		}catch(Exception e) {
 			return "";
		}
	}
	
	public void readPatient(Long id)   {
		String answerJson = get( serverBase+"/readResource/"+id );		 
 		String answerXml  =  get(serverBase+"/readResource/"+id+"?_format=xml");		 
 		System.out.println( answerJson );
		System.out.println( answerXml );
//  		JSONObject jo = new JSONObject(answerJson);
// 		String prettyJson = jo.toString(4);
//		System.out.println(prettyJson);
		
	}
 
	public static void main(String[] args) throws Exception {
		HttpClientHealthAdapter appl = new HttpClientHealthAdapter();
		System.out.println(" %%% CREATE ------------------------------");
// 		Long id = appl.createPatient();		
		System.out.println(" %%% READ  ------------------------------ ");
//		appl.readPatient( id );
		appl.readPatient(1435799L);
		System.out.println(" %%% SEARCH ----------------------------- ");
//		appl.search(Patient.class, "ElenaBologna");
 		System.out.println(" %%% DELETE ----------------------------- ");
//   		appl.deletePatient("Patient", id.toString() );
		System.out.println(" %%% SEARCH ----------------------------- ");
 	}
}