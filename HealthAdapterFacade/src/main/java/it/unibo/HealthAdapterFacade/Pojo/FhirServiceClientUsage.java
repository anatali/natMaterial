package it.unibo.HealthAdapterFacade.Pojo;
/*
 * ------------------------------------------------------------------------
 * Utilizza un oggetto di tipo FhirServiceClient
 * ------------------------------------------------------------------------
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Patient;
import org.json.*;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleUtil;
import it.unibo.HealthAdapterFacade.FhirServiceClient;

 
public class FhirServiceClientUsage {

	private FhirServiceClient client = new FhirServiceClient( "https://hapi.fhir.org/baseR4" );
	
	//From https://www.baeldung.com/java-http-request
//	public String get(String uri) throws Exception {
//		URL url = new URL(uri);
//		HttpURLConnection con = (HttpURLConnection) url.openConnection();
//		con.setRequestMethod("GET");
//		int status = con.getResponseCode();
//		BufferedReader in = new BufferedReader(
//		new InputStreamReader(con.getInputStream()));
//		String inputLine;
//		StringBuffer content = new StringBuffer();
//		while ((inputLine = in.readLine()) != null) {
//			content.append(inputLine);
//		}
//		in.close();
//		return content.toString();
//	}
	
	public void readPatient() throws Exception {
// 		String htmlPage   = get("https://hapi.fhir.org/baseR4/Patient/1433281");					//html page
//		String answerXml  = get("https://hapi.fhir.org/baseR4/Patient/1433281?_format=xml");		//content in xml
		Long id = 1433281L;
		Patient p    = client.readPatient(Patient.class, id ) ;	
		String pJson = client.cvtJson( p );		
		String pXml  = client.cvtXml( p );		
		System.out.println("----------------- patient " + id + " in json  ---- ");
		System.out.println(pJson);
		System.out.println("----------------- patient " + id + " in xml   ---- ");
		System.out.println(pXml);
		
		
//		System.out.println(htmlPage);
//		System.out.println(answerXml);		
 		//https://www.baeldung.com/java-org-json
// 		JSONObject jo = new JSONObject(answerJson);
// 		String prettyJson = jo.toString(4);
//		System.out.println(prettyJson);
	}

	
	public void search( Class<Patient> resourceClass, String name ) throws Exception {
		Bundle b     = client.searchPatient(  resourceClass,  name );
		FhirContext theContext   = client.getFhirContext();
 		String bJson = client.cvtJson( b  );	
 		Iterator<Pair<String, IBaseResource>>  iter = BundleUtil.getBundleEntryUrlsAndResources(theContext, b).iterator();
		System.out.println("----------------- found bundle for " + name + "  ---- ");
		System.out.println(  bJson );
		while( iter.hasNext() ) {
			Pair<String, IBaseResource> rp = iter.next();
			IBaseResource r = rp.getRight();
			System.out.println("----------------- resource " + name + "  ---- ");
			System.out.println(  client.cvtJson(r) );
		}		
	}
	
	
	public static void main(String[] args) throws Exception {
		FhirServiceClientUsage appl = new FhirServiceClientUsage();
		appl.search(Patient.class, "ElenaBologna");
// 		appl.readPatient();
 	}
	
 }