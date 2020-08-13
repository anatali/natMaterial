package it.unibo.HealthAdapter.Clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*; 
 
public class HealthAdapterHttpClient {

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
		System.out.println(content);
		return content.toString();
	}
	
	public static void main(String[] args) throws Exception {
		HealthAdapterHttpClient appl = new HealthAdapterHttpClient();
		appl.get("https://hapi.fhir.org/baseR4/Patient/1433281");					//html page
		String answerJson = appl.get("https://hapi.fhir.org/baseR4/Patient/1433281?_format=json");		//content in json
 		
		System.out.println("----------------------------------------------------");
		
		//https://www.baeldung.com/java-org-json
 		JSONObject jo = new JSONObject(answerJson);
 		String prettyJson = jo.toString(4);
		System.out.println(prettyJson);
	}
}