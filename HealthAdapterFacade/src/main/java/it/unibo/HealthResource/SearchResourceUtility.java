package it.unibo.HealthResource;

 
import java.util.Iterator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Patient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import it.unibo.HealthAdapterFacade.HealthService;

/*
 * Given a DomainResource template we must identify the specific resource type R
 * and inspect R for each specific ITel-ralated info
 */
public class SearchResourceUtility {
 

	public static String[] inspect(String queryjsonStr) {
		System.out.println("SearchResourceUtility | inspect resourceType=" + queryjsonStr );
		String[] answer = new String[2];
 		try {
			JSONObject queryobj = new JSONObject(queryjsonStr);
 			String resourceType  = queryobj.getString("resourceType");
			System.out.println("SearchResourceUtility | inspect resourceType=" + resourceType );
			answer[0]           = resourceType;
			switch( resourceType ) {
				case "Patient" : answer[1] = inspectPatient( resourceType, queryobj );  break;
				default        : { 
					System.out.println("SearchResourceUtility | inspect resourceType UNKNONN"  );
					answer[0]       = null;
					}
			}
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspect ERROR " + e.getMessage());
			answer[0]       = null;
		}
 		return answer;
	}
	public static String inspectPatient( String resourceType, JSONObject queryobj ) {
//		System.out.println("SearchResourceUtility | inspectPatient " + queryobj );
		String gender  	= null;
		String family  	= null;
		String name  	= null;
		String city  	= null;
		String country  = null;
		
 		try {
 			Iterator<String> keys = queryobj.keys();
 			while( keys.hasNext() ) {
 				String key = keys.next();
// 				System.out.println( "SearchResourceUtility | inspectPatient key=" + key);
 				switch( key ) {
 					case "name" :   name   = queryobj.getString("given");break;
 					case "family" : family = queryobj.getString("family");break;
 					case "gender" : gender = queryobj.getString("gender");break;
					case "address" : 
 						JSONObject adressobj = queryobj.getJSONObject("address"); 
 						city                 = adressobj.getString("city");
 						country              = adressobj.getString("country");
 						break;
 					default:
 				}
 			}
// WARNING: city,  country are not   search parameters
	 		StringBuilder queryStr = new StringBuilder();
	 		queryStr.append( "active" );
	 		if(name != null) 	queryStr.append("&name="+name);
	 		if(family != null)	queryStr.append("&family="+family);
	 		if(gender != null) 	queryStr.append("&gender="+gender);
	 		if(city != null) 	queryStr.append("&address="+city);
	 		if(country != null)	queryStr.append("&address="+country);
	 		
	 		   
	 		return queryStr.toString(); 
		} catch (Exception e) {
			System.out.println("SearchResourceUtility | inspectPatient ERROR " + e.getMessage());
			return null;
		}
		
	}
}
