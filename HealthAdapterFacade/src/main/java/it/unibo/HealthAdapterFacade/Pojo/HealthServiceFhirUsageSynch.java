/*
 * ------------------------------------------------------------------------
 * Utilizza  HealthServiceFhir per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade.Pojo;
 
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
import reactor.core.publisher.Flux;
 

public class HealthServiceFhirUsageSynch {
 	
  	private HealthServiceInterface healthService;
  	private String serverBase="https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";   
    			
 	public HealthServiceFhirUsageSynch() {
 		healthService = new HealthServiceFhir( serverBase ) ;	
  	}
  	
   	
 	private void extractValues( String jsonRep ) {
		try {
			JSONObject jsonobj = new JSONObject( jsonRep );
			String idstr        = jsonobj.getString("id");
//			currentResourceType = jsonobj.getString("resourceType");
//	  		currentResourceId   = Long.parseLong( idstr );
//	  		System.out.println("readPatient id=:" + currentResourceId + " currentResourceType="  + currentResourceType);
		} catch (JSONException e) {
			System.out.println("extractValues ERROR" + e.getMessage() );
		}	 			 				
 	}
 	
//CREATE  	
	public Long createResourceFromFile( String fname ) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		Long id             = healthService.createResourceSynch( jsonRep );
		return id;
 	}

//READ	
	public void readResource( Long id )   {
  	}

//SEARCH
 	public void searchResource(String queryJson) {
  	}

 //UPDATE
 	public void updateResourceFromFile( String fname, Long id ) {
  	}

//DELETE
 	public void deleteResource(  String resourceType, Long id ) {
  	}

	
	
	/*
	 * CRUD - the callback hell
	 */	
	public static void main( String[] args) throws IOException {
		HealthServiceFhirUsageSynch appl = new HealthServiceFhirUsageSynch();
		String resourceFileName       = "src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejson.txt";
		String updateResourceFileName = "src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejsonUpdate.txt";
		String queryStr         = "{ \"resourceType\": \"Patient\", \"address\": { \"city\": \"Cesena\", \"country\": \"Italy\" } }"; 
  
		System.out.println(" %%% CREATE  ------------------------------ "); 		
		Long id = appl.createResourceFromFile(resourceFileName);		 
		
		System.out.println(" %%% READ    ------------------------------ ");
		appl.readResource( id );
		
/* 		
		System.out.println(" %%% SEARCH  ------------------------------ ");
		appl.searchResource( queryStr );
		
 		
		System.out.println(" %%% UPDATE  ------------------------------ ");
		appl.updateResourceFromFile(updateResourceFileName, 1439336L);
		
*/ 
		System.out.println(" %%% DELETE  ------------------------------ ");
//		appl.currentResourceType = "Patient";
//		appl.deleteResource( appl.currentResourceType, appl.currentResourceId );	//
		
//		HealthService.delay(2000);  //To avoid premature termination
		
 		System.out.println(" %%% END  ------------------------------ ");
 
		}
}
