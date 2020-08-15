package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

 
 
public class HttpClientFhirServer {
	private String serverBase="https://hapi.fhir.org/baseR4"; 

	//From https://www.baeldung.com/java-http-request
	public String post(String uri, String body )  {
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			//con.setRequestProperty("Content-Type", "application/json; utf-8");
			con.setRequestProperty("Content-Type", "plain/text; utf-8");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);

//write the body
 			OutputStream os = con.getOutputStream();
			byte[] input    = body.getBytes("utf-8");
			os.write(input, 0, input.length);

			int status = con.getResponseCode();
			System.out.println( "HttpClientHealthAdapter post " + url +" status=" + status);
//read the answer			
			BufferedReader in = new BufferedReader(
			new InputStreamReader( con.getInputStream(), "utf-8"));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine.trim());
			}
			in.close();
			return response.toString();
		}catch(Exception e) {
 			return "";
		}
		
	}
	//From https://www.baeldung.com/java-http-request
	public String get(String uri)  {
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int status = con.getResponseCode();
			System.out.println( "HttpClientHealthAdapter get " + url +" status=" + status);
			
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

/*
 * ------------------------------------------------------------------------	
 */	
	public Long createPatient( String  name ) {
 		System.out.println("WARNING: createPatient IS NOT POSSIBLE "  );
		return 0L;
 	}

	public void readPatient(Long id)   {
		String answerJson =  get( serverBase+"/Patient/"+id+"?_format=json" );		 
 		//String answerXml  =  get(serverBase+"/readResource/"+id+"?_format=xml");		  
 		System.out.println( answerJson );
 	}
	
	public void searchPatient(String patientName) {
		System.out.println("WARNING: searchPatient IS NOT POSSIBLE "  );
	}
	
 	public void delete_patient(String id) {
		System.out.println("WARNING: delete_patient IS NOT POSSIBLE "  );
 	}
	
 	
 	public static void main(String[] args) throws Exception {
		HttpClientFhirServer appl = new HttpClientFhirServer();
		System.out.println(" %%% CREATE ------------------------------");
// 		Long id = appl.createPatient("ElenaBologna");		
		System.out.println(" %%% READ  ------------------------------ ");
//		appl.readPatient( id );
		appl.readPatient(1435799L);
		System.out.println(" %%% SEARCH ----------------------------- ");
//		appl.search( "ElenaBologna" );
 		System.out.println(" %%% DELETE ----------------------------- ");
  		appl.delete_patient("1435804"  );
   
	}
}