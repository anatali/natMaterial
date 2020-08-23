package it.unibo.HealthResource;

import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.*;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.uhn.fhir.parser.IParser;
import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.DirectProcessor;
import reactor.core.publisher.Flux;
 

/*
 * Given a DomainResource template we must identify the specific resource type R
 * and inspect R for each specific ITel-ralated info
 */
public class ResourceUtility {
	
	public static Class<? extends DomainResource> getTheClass(String resourceType) {
		switch( resourceType ) {
			case "CarePlan" : return CarePlan.class;
			case "Patient"  : return Patient.class;
			default: return null;
		}
	}
	
	
	public static DomainResource createResourceFromFileJson(String fileName) {
 		 String resjsonStr   = getResourceJsonRepFromFile(fileName);
		 if( resjsonStr != null) return buildResource(resjsonStr);
		 else return null;
	}

	
	public static void injectId(DomainResource resource, String id) {
			resource.setId( new IdType(resource.fhirType(), id)  );
 	}
	
	public static String getResourceJsonRepFromFile(String fileName) {
		try {			
			FileInputStream fis = new FileInputStream(fileName);
		    String resjsonStr   = IOUtils.toString(fis, "UTF-8");
 		    return resjsonStr;
		} catch (Exception e) {
			System.out.println("getResourceJsonRepFromFile ERROR"+ e.getMessage() );
			return null;
		}
	}
	
	
 	public static DomainResource createResourceFromJson( String resourceType, String jsonrep ) {
 		    IParser parserfhir      = HealthService.fhirctx.newJsonParser();
			switch( resourceType ) {
				case "Patient"  : return parserfhir.parseResource(Patient.class, jsonrep); 
				case "CarePlan" : return parserfhir.parseResource(CarePlan.class, jsonrep);
				default        : { 
					System.out.println("HealthServiceFhir | createResourceFromJson resourceType UNKNONN"  );
					return null;
 				}
			}
	}
 	
 	public static String getJsonRep( DomainResource resource) {
 		return HealthService.fhirctx.newJsonParser().encodeResourceToString(resource);
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
	
	public static DomainResource buildResource(String resjsonStr) {
		DomainResource answer = null;
 		try {
			JSONObject resobj   = new JSONObject (resjsonStr );	
			String resourceType = resobj.getString("resourceType");
			answer              = createResourceFromJson(resourceType, resjsonStr);
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspect ERROR " + e.getMessage());
		}
		return answer;
	}
	
	
	public static DomainResource buildResource(String resjsonStr, String id) {
		DomainResource resource = null;
 		try {
			JSONObject resobj   = new JSONObject (resjsonStr );	
			String resourceType = resobj.getString("resourceType");
			resource            = createResourceFromJson(resourceType, resjsonStr);
			//Inject the id
			resource.setId( new IdType(resourceType, id) );
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspect ERROR " + e.getMessage());
		}
		return resource;
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

 /*
 * =========================================================================
 * DATAFLUX    
 * =========================================================================
 */	  
	
	private static int datafluxcount = 0;
	
	public static Flux<String> createColdFlux() {
		Flux<String> dataflux = Flux.push(sink -> {
		   new Thread() {
			int n = 0;
			public void run() {
			  while( n < 10 ) {
 				String s = "cold_"+datafluxcount+"_"+n+++" " ;
 				sink.next( s );
		 		System.out.println("ResourceUtility | createColdFlux generates=" + s);
				HealthService.delay(1000);
			  }//while
			  sink.complete();
			}//run
		   }.start();		 
		 });	
		return dataflux;
	}
	
	
	public static Flux<String> createHotFlux() {
 		DirectProcessor<String> hotSource = DirectProcessor.create();
 		Flux<String> hotFlux              = hotSource.map(String::toUpperCase);
 		new Thread() {
 			public void run() {
 		 		for( int i=1; i<=10; i++) {
 		 			String s = "hot_"+datafluxcount+"_"+i+" " ;
 		 			hotSource.onNext(s  );
 		 			System.out.println("ResourceUtility | createHotFlux generates=" + s);
 		 			HealthService.delay(750);
 		 		}
 		 		hotSource.onComplete();				
 			}
 		}.start();
 		return hotFlux;		
	}
	
	
 	public static Flux<String> startDataflux(  String args  )  { //method=POST 
 		System.out.println("ResourceUtility | startDataflux args=" + args);
 		datafluxcount++;
 		if( args.equals("hot") ) return createHotFlux();	
 		else return createColdFlux();
 		
 		//return Flux.just("1","2","3"); 
 		
 	}	
 	public static Flux<String> stopDataflux(  String args  )  { //method=POST 
 		System.out.println("ResourceUtility | stopDataflux args=" + args);
 		return Flux.just("dataflux stopped to do");
 	}	

}
