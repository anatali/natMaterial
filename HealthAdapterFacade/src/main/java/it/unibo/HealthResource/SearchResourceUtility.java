package it.unibo.HealthResource;

 
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.Patient;
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
 

	public static void inspect(String queryjsonStr) {
		System.out.println("SearchResourceUtility | inspect resourceType=" + queryjsonStr );
 		try {
			JSONObject quesryobj = new JSONObject(queryjsonStr);
 			String resourceType  = quesryobj.getString("resourceType");
			System.out.println("SearchResourceUtility | inspect resourceType=" + resourceType );
			switch( resourceType ) {
				case "Patient" : inspectPatient( quesryobj );break;
				default        : System.out.println("SearchResourceUtility | inspect resourceType UNKNONN"  );
			}
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspect ERROR " + e.getMessage());
		}
	}
	public static void inspectPatient( JSONObject quesryobj ) {
		System.out.println("SearchResourceUtility | inspectPatient");
 		try {
			JSONObject adressobj = quesryobj.getJSONObject("address");
	 		System.out.println("gender?=" + quesryobj.getString("gender")
				+  "family?= " + quesryobj.getString("family") 
				+ "name?= "    + quesryobj.getString("given")
				+ "city?= "    + adressobj.getString("city")
				+ "country?= " + adressobj.getString("country")
		);
		} catch (JSONException e) {
			System.out.println("SearchResourceUtility | inspectPatient ERROR " + e.getMessage());
		}
		
	}
}
