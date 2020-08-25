package it.unibo.Convert;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.*;

 
 
public class ConverterHttpClient {

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
 
	public static void main(String[] args) throws Exception {
		ConverterHttpClient appl = new ConverterHttpClient();
		try {
// 			String answer = appl.get("http://localhost:2019");
 			String answer = appl.get("http://localhost:2019/api-docs");
 			System.out.println( answer);
		}catch( Exception e ) {
			System.out.println("ERROR " + e.getMessage());
		}
	}
}