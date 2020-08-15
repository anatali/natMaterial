package it.unibo.HealthAdapter.Clients;
/*
 * ------------------------------------------------------------------------
 * Interacts with the HealthProduct or with the FHIR server
 * by using the org.springframework.web.reactive.function.client.WebClient
 * ------------------------------------------------------------------------
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
 
 
public class HttpClientHealthAdapter {
	public final static FhirContext fhirctx = FhirContext.forR4();

	private String serverBase="http://localhost:8081"; 
  	
	//From https://www.baeldung.com/httpurlconnection-post
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
	public Patient createFhirPatientFromFileJson(String fileName) {
		try {			
			FileInputStream fis = new FileInputStream(fileName);
		    String data         = IOUtils.toString(fis, "UTF-8");
		    //System.out.println("createPatientFromFile data"+data );
		    IParser parser      = fhirctx.newJsonParser();
		    Patient newPatient  = parser.parseResource(Patient.class, data);	
		    return newPatient;
		} catch (Exception e) {
			System.out.println("createFhirPatientFromFileJson ERROR"+ e.getMessage() );
			return null;
		}
	}

	public Long createPatient( String  name ) {
 		System.out.println("WARNING: createPatient IS NOT POSSIBLE "  );
		return 0L;
 	}
	
	public void readPatient(Long id)   {
		String answerJson =  get( serverBase+"/readResource/"+id );		 
 		//String answerXml  =  get(serverBase+"/readResource/"+id+"?_format=xml");		 //HealthProduct answers in Json only
 		System.out.println( answerJson );
		//System.out.println( answerXml );
//  		JSONObject jo = new JSONObject(answerJson);
// 		String prettyJson = jo.toString(4);
//		System.out.println(prettyJson);
		
	}
	
	public void searchPatient(String patientName) {
		String answerJson =  get( serverBase+"/searchPatient/"+patientName );		 
 		System.out.println( answerJson );
	}
	
 	public void delete_patient(String id) {
 		String res = post( serverBase+"/deleteResource", id ); 
		System.out.println("----------------- deletePatient result:" + res );
 	}
 
	public static void main(String[] args) throws Exception {
		HttpClientHealthAdapter appl = new HttpClientHealthAdapter();
//		System.out.println(" %%% CREATE ------------------------------");
// 		Long id = appl.createPatient();		
		System.out.println(" %%% READ  ------------------------------ ");
//		appl.readPatient( id );
 		appl.readPatient(1435799L);
		System.out.println(" %%% SEARCH ----------------------------- ");
  		appl.searchPatient( "ElenaBologna" ); 
 		System.out.println(" %%% DELETE ----------------------------- ");
  		appl.delete_patient( "1435805" );
		System.out.println(" %%% SEARCH ----------------------------- ");
 	}
}