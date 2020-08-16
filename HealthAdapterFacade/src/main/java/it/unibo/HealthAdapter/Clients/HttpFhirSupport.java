package it.unibo.HealthAdapter.Clients;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Patient;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class HttpFhirSupport {
	public final static FhirContext fhirctx = FhirContext.forR4();
	
	//From https://www.baeldung.com/java-http-request
	public static String post(String uri, String body, String contentType )  { 
		//contentType: "application/json; utf-8" "plain/text; utf-8"
		System.out.println( "HttpClientHealthAdapter post " + uri +" body=" + body + " contentType=" + contentType);
		try {
			URL url = new URL(uri);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", contentType);
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
			System.out.println( "HttpClientHealthAdapter post ERROR" +e.getMessage() );
 			return "";
		}
		
	}
	//From https://www.baeldung.com/java-http-request
	public static String get(String uri)  {
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
	
	public static String delete(String uri)  {
		HttpURLConnection con = null;
		String result="delete " + uri + " done";
		try {
			URL url = new URL(uri);
			con = (HttpURLConnection) url.openConnection();
		    con.setRequestProperty("Content-Type",
		                "application/x-www-form-urlencoded");
		    con.setRequestMethod("DELETE");
		    System.out.println("delete response code=" + con.getResponseCode());
		} catch (Exception e) {
			result = "delete " + uri + "error " + e.getMessage();
		    //exception.printStackTrace();
		} finally {         
		    if (con != null) {
		        con.disconnect();
		    }
		}
	return result;
	}

	public static String readPatientFromFileJson(String fileName) {
		try {			
			FileInputStream fis = new FileInputStream(fileName);
		    String data         = IOUtils.toString(fis, "UTF-8");
 		    return data;
		} catch (Exception e) {
			System.out.println("readPatientFromFileJson ERROR"+ e.getMessage() );
			return null;
		}
	}
	
	public static Patient createFhirPatientFromFileJson(String fileName) {
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

	
	public static Patient createFhirPatientFromStringJson(String pjson) {
	    IParser parser      = fhirctx.newJsonParser();
	    Patient newPatient  = parser.parseResource(Patient.class, pjson);	
	    return newPatient;		
	}
	
	public static Long getPatientId( DomainResource p) { //Patient -> DomainResource -> modelResource -> BaseResource -> Base -> Object
		String pid = p.getId();
		System.out.println("getPatientId from Patient pid="+ pid );
		return 0L;		
	}
//	public static Long getPatientId( Patient p) { //Patient -> DomainResource -> modelResource -> BaseResource -> Base -> Object
//		String pid = p.getId();
//		System.out.println("getPatientId from Patient pid="+ pid );
//		return 0L;		
//	}
	public static Long getPatientId( String pjson) {
		String pid = createFhirPatientFromStringJson(pjson).getId();
		String[] pidfields = pid.split("/");
		System.out.println("getPatientId from string pid="+ pidfields[1] );
		return Long.parseLong( pidfields[1] );		
	}
	
	
	public static String prettyJson( String sjson ) {
		String result = "";
		try {
		    ObjectMapper mapper = new ObjectMapper();
		    JsonFactory factory = mapper.getFactory();
			JsonParser parser   = factory.createParser( sjson );
		    JsonNode actualObj  = mapper.readTree(parser);
		    result              = actualObj.toPrettyString() ;		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
