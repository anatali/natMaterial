package it.unibo.HealthResource;

 
import java.util.Iterator;

import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Patient;
import org.json.JSONException;
import org.json.JSONObject;

import ca.uhn.fhir.model.dstu2.resource.CarePlan;
import ca.uhn.fhir.parser.IParser;
import it.unibo.HealthAdapterFacade.HealthService;
 

/*
 * Given a DomainResource template we must identify the specific resource type R
 * and inspect R for each specific ITel-ralated info
 */
public class SearchResourceUtility {
	
	
 	public static IBaseResource createResourceFromJson( String resourceType, String jsonrep ) {
// 		try {
 		    IParser parserfhir      = HealthService.fhirctx.newJsonParser();
			switch( resourceType ) {
				case "Patient"  : return parserfhir.parseResource(Patient.class, jsonrep); 
				case "CarePlan" : return parserfhir.parseResource(CarePlan.class, jsonrep);
				default        : { 
					System.out.println("HealthServiceFhir | createResourceFromJson resourceType UNKNONN"  );
					return null;
 				}
			}
//		} catch ( Exception e) {
// 			e.printStackTrace();
// 			return null;
//		}		
	}
	
	
	public static IBaseResource buildResource(String resjsonStr) {
		IBaseResource answer = null;
 		try {
			JSONObject resobj   = new JSONObject (resjsonStr );	
			String resourceType = resobj.getString("resourceType");
			answer              = createResourceFromJson(resourceType, resjsonStr);
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspect ERROR " + e.getMessage());
		}
		return answer;
	}
 
	public static String[] getBasicInfo(String resjsonStr) {
		String[] answer = new String[2];
 		try {
			answer[0]           = null;
			JSONObject resobj   = new JSONObject (resjsonStr );
  			answer[0]           =  resobj.getString("resourceType");
 			answer[1]           =  resobj.getString("id");
			System.out.println("SearchResourceUtility | getBasicInfo resourceType=" + answer[0] + " id="+answer[1] );
			return answer;
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspect ERROR " + e.getMessage());
		}
 		return answer;
	}

	public static String[] inspect(String queryjsonStr) {
		System.out.println("SearchResourceUtility | inspect resourceType=" + queryjsonStr );
		String[] answer = new String[2];
 		try {
			JSONObject queryobj = new JSONObject(queryjsonStr);
 			String resourceType  = queryobj.getString("resourceType");
			System.out.println("SearchResourceUtility | inspect resourceType=" + resourceType );
			answer[0]           = resourceType;
			switch( resourceType ) {
				case "Patient"  : answer[1] = inspectPatient( resourceType, queryobj );  break;
				case "CarePlan" : answer[1] = inspectCarePlan( resourceType, queryobj );  break;
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
 					case "given"  : name   = queryobj.getString("given");break;
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
 	 		if(name != null) 	updateQueryStr(queryStr, "given="+name);
	 		if(family != null)	updateQueryStr(queryStr, "family="+family);
	 		if(gender != null) 	updateQueryStr(queryStr, "gender="+gender);
	 		if(city != null) 	updateQueryStr(queryStr, "address="+city);
	 		if(country != null)	updateQueryStr(queryStr, "address="+country);
	 		
	 		   
	 		return queryStr.toString(); 
		} catch (Exception e) {
			System.out.println("SearchResourceUtility | inspectPatient ERROR " + e.getMessage());
			return null;
		}		
	}
	
	private static void updateQueryStr( StringBuilder sb, String s) {
		if( sb.length() == 0 ) sb.append(s); else sb.append("&"+s);		
	}
	public static String inspectCarePlan( String resourceType, JSONObject queryobj ) {
		String status  	= null;
		String intent  	= null;
 		try {
 			Iterator<String> keys = queryobj.keys();
 			while( keys.hasNext() ) {
 				String key = keys.next();
// 				System.out.println( "SearchResourceUtility | inspectPatient key=" + key);
 				switch( key ) {
 					case "status"  : status = queryobj.getString("status");break;
 					case "intent"  : intent = queryobj.getString("intent");break;
 					default:
 				}
 			}//while
 			
	 		StringBuilder queryStr = new StringBuilder();
 	 		if(status != null ) updateQueryStr(queryStr, "status="+status);
	 		if(intent != null ) updateQueryStr(queryStr, "intent="+intent);
	 		return queryStr.toString(); 
			
		} catch (Exception e) {
			System.out.println("SearchResourceUtility | inspectPatient ERROR " + e.getMessage());
			return null;
		}		

 	}
	
}
