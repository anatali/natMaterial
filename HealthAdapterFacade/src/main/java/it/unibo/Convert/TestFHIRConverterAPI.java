/*
 * ------------------------------------------------------------------------
 * Interagisce con the HealthProduct server
 * usando org.springframework.web.reactive.function.client.WebClient.
 * 
 * Evidenzia che, contraramente a JavaScipt fetch, si possono ottenere 
 * da HealthProduct le informazioni 'on the fly' senza
 * attendere il completamento del Flux.
 * ------------------------------------------------------------------------
 */
package it.unibo.Convert;


import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import ca.uhn.fhir.model.dstu2.resource.Bundle;
import it.unibo.HealthAdapterFacade.HealthService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class TestFHIRConverterAPI {
	private static String hostaddr = "localhost:2019";
	private static WebClient webClient = WebClient
	    	  .builder()
	    	  .build();

	public static String getFromFile(String fileName) {
		try {			
			FileInputStream fis = new FileInputStream(fileName);
		    String str          = IOUtils.toString(fis, "UTF-8");
//		    System.out.println( "getFromFile " + str);
 		    return str;
		} catch (Exception e) {
			System.out.println("getFromFile ERROR"+ e.getMessage() );
			return null;
		}
	}

	public static String prettyJson(String jsonStr) {
 		JSONObject json;
		try {
			json = new JSONObject(jsonStr);
			JSONObject res = (JSONObject) json.get("fhirResource");
			return res.toString(2);
		} catch (JSONException e) {
			return "prettyJson ERROR for:"+jsonStr;
		}	 		
 	}
	
    
 	private static void subscribeAndHandleCompletion( String msg, Flux<String> answer ) {
 		System.out.println(msg + " IS BUILDING THE ANSWER ... " + msg);
		final StringBuilder strbuild = new StringBuilder();  
 		answer.subscribe(			
 				item  -> {System.out.println("%%% "+ msg + " "+ item ); strbuild.append(item); },
 				error -> System.out.println(msg + " error= " + error ),
 				()    -> {
 					System.out.println(msg +    prettyJson( strbuild.toString() ) );
 				}
 		);			
 	}
 	
  
  	public static void doGet( String path ) {
  		Flux<String> flux   = readResource( path );
		subscribeAndHandleCompletion("get_" , flux); 		
 	}
	public static Flux<String> readResource( String path ) { 
		System.out.println( "readResource " + path);
		String addr = hostaddr+ path; 	 
     	Flux<String> answer = webClient.get()
				.uri( addr )   
                .retrieve()
                .bodyToFlux(String.class);
    	return answer;
	}
 	
  	public static void doPost( String path, String data ) {
  		String addr = hostaddr+ path; 
  		System.out.println("doPost addr=" +  addr ) ;
    	Flux<String> flux = webClient.post()
				.uri( addr )   
				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just(data), String.class )
                .retrieve()
                .bodyToFlux(String.class);
    	subscribeAndHandleCompletion("post_" , flux ); 	//.filter( v -> ... )
 	}
  	
  	public static void doPut( String path, String args ) {
  		String addr = hostaddr+ path; 
    	Flux<String> flux = webClient.put()
				.uri( addr )   
				.contentType(MediaType.TEXT_PLAIN)
				.body( Mono.just(args), String.class )
                .retrieve()
                .bodyToFlux(String.class);
		subscribeAndHandleCompletion("post_" , flux); 		
 	}
  	
  	
    public static void main(String[] args)   {
    	
//    	doGet( "/api-docs/" );							//ok
//    	doGet( "/#/default/get_api_sample_data");		//ok 
    	//curl -X GET "http://localhost:2019/api/sample-data?code=123" -H "accept: application/json"
    	//http://localhost:2019/api/sample-data?code=nat
    	
//    	doGet( "/api/sample-data");						//ok

    	
/*
 * SAMPLE DATA    	
*/    	
    	//    	doGet( "/api/sample-data/hl7v2/ADT01-23.hl7");					//ok
    	//curl -X GET "http://localhost:2019/api/sample-data/hl7v2%2FADT01-23.hl7?code=nat" -H "accept: text/plain"
    	//http://localhost:2019/api/sample-data/hl7v2%2FADT01-23.hl7?code=nat

/*
 * TEMPLATES    	
 */
    	
    		doGet( "/api/templates");					//ok    {"templateName": "hl7v2/ADT_A01.hbs"},
    	//curl -X GET "http://localhost:2019/api/templates?code=nat" -H "accept: application/json"
    	//http://localhost:2019/api/templates?code=nat
 
    	//doGet( "/api/templates/hl7v2/ADT_A01.hbs");					//ok    {"templateName": "hl7v2/ADT_A01.hbs"},
    	//curl -X GET "http://localhost:2019/api/templates/hl7v2%2FADT_A01.hbs?code=nat" -H "accept: text/plain"
    	//http://localhost:2019/api/templates/hl7v2%2FADT_A01.hbs?code=nat
    	
 /*
 * POST
 */
     	
    	String hl7_data = getFromFile("src/main/java/it/unibo/HL7/datafiles/ADT01-23.hl7");
//      	doPost( "/api/convert/hl7v2/ADT_A01.hbs", hl7_data );
    	
     	HealthService.delay(3000);
    	System.out.println( "BYE");		
    }
}

/*
TEMPLATE ADT_A01.hbs
{    "resourceType": "Bundle",    "type": "transaction",    "entry": [        {{#with (getFirstSegments msg.v2 'PID' 'PD1' 'PV1' 'PV2' 'PR1' 'AVR')}}                {{>Resources/Patient.hbs PID=PID PD1=PD1 NK1=NK1 ID=(generateUUID PID-3-1)}},            {{>Resources/Encounter.hbs PV1=PV1 PV2=PV2 ID=(generateUUID PV1)}},            {{>Resources/Procedure.hbs PR1=PR1 ID=(generateUUID PR1)}},            {{>References/Encounter/subject.hbs ID=(generateUUID PV1) REF=(concat 'Patient/' (generateUUID PID-3-1))}},             {{>References/Procedure/subject.hbs ID=(generateUUID PR1) REF=(concat 'Patient/' (generateUUID PID-3-1))}},	                        {{#with (getSegmentLists ../msg.v2 'NK1')}}                {{#each NK1 as |NK1Instance|}}                        {{>Resources/RelatedPerson.hbs NK1=NK1Instance ID=(generateUUID NK1Instance)}},                    {{>References/RelatedPerson/patient.hbs ID=(generateUUID NK1Instance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},                    {{/each}}            {{/with}} 	                        {{#with (getSegmentLists ../msg.v2 'OBX')}}                {{#each OBX as |OBXInstance|}}                        {{>Resources/Observation.hbs OBX=OBXInstance ID=(generateUUID OBXInstance)}},                    {{>References/Observation/subject.hbs ID=(generateUUID OBXInstance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},                    {{/each}}            {{/with}}             {{#with (getSegmentLists ../msg.v2 'AL1')}}                {{#each AL1 as |AL1Instance|}}                        {{>Resources/AllergyIntolerance.hbs AL1=AL1 ID=(generateUUID AL1Instance)}},                    {{>References/AllergyIntolerance/patient.hbs ID=(generateUUID AL1Instance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},                    {{/each}}            {{/with}}                 {{#with (getSegmentLists ../msg.v2 'DG1')}}                {{#each DG1 as |DG1Instance|}}                        {{>Resources/Condition.hbs DG1=DG1Instance ID=(generateUUID DG1Instance)}},                    {{>References/Condition/subject.hbs ID=(generateUUID DG1Instance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},                    {{>References/Encounter/diagnosis.condition.hbs ID=(generateUUID ../../PV1) REF=(concat 'Condition/' (generateUUID DG1Instance))}},                    {{/each}}            {{/with}}             {{/with}}    ] }

{
    "resourceType": "Bundle",
    "type": "transaction",
    "entry": [
        {{#with (getFirstSegments msg.v2 'PID' 'PD1' 'PV1' 'PV2' 'PR1' 'AVR')}}
    
            {{>Resources/Patient.hbs PID=PID PD1=PD1 NK1=NK1 ID=(generateUUID PID-3-1)}},
            {{>Resources/Encounter.hbs PV1=PV1 PV2=PV2 ID=(generateUUID PV1)}},
            {{>Resources/Procedure.hbs PR1=PR1 ID=(generateUUID PR1)}},
            {{>References/Encounter/subject.hbs ID=(generateUUID PV1) REF=(concat 'Patient/' (generateUUID PID-3-1))}}, 
            {{>References/Procedure/subject.hbs ID=(generateUUID PR1) REF=(concat 'Patient/' (generateUUID PID-3-1))}},	
            
            {{#with (getSegmentLists ../msg.v2 'NK1')}}
                {{#each NK1 as |NK1Instance|}}
    
                    {{>Resources/RelatedPerson.hbs NK1=NK1Instance ID=(generateUUID NK1Instance)}},
                    {{>References/RelatedPerson/patient.hbs ID=(generateUUID NK1Instance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},
    
                {{/each}}
            {{/with}} 	
            
            {{#with (getSegmentLists ../msg.v2 'OBX')}}
                {{#each OBX as |OBXInstance|}}
    
                    {{>Resources/Observation.hbs OBX=OBXInstance ID=(generateUUID OBXInstance)}},
                    {{>References/Observation/subject.hbs ID=(generateUUID OBXInstance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},
    
                {{/each}}
            {{/with}} 

            {{#with (getSegmentLists ../msg.v2 'AL1')}}
                {{#each AL1 as |AL1Instance|}}
    
                    {{>Resources/AllergyIntolerance.hbs AL1=AL1 ID=(generateUUID AL1Instance)}},
                    {{>References/AllergyIntolerance/patient.hbs ID=(generateUUID AL1Instance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},
    
                {{/each}}
            {{/with}} 
    
            {{#with (getSegmentLists ../msg.v2 'DG1')}}
                {{#each DG1 as |DG1Instance|}}
    
                    {{>Resources/Condition.hbs DG1=DG1Instance ID=(generateUUID DG1Instance)}},
                    {{>References/Condition/subject.hbs ID=(generateUUID DG1Instance) REF=(concat 'Patient/' (generateUUID ../../PID-3-1))}},
                    {{>References/Encounter/diagnosis.condition.hbs ID=(generateUUID ../../PV1) REF=(concat 'Condition/' (generateUUID DG1Instance))}},
    
                {{/each}}
            {{/with}} 
    
        {{/with}}
    ] 
}
*/
