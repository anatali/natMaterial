/*
 * ------------------------------------------------------------------------
 * Utilizza  HealthServiceFhir per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade.Pojo;
 
import java.io.IOException;
import org.hl7.fhir.r4.model.DomainResource;
import it.unibo.HealthAdapter.Clients.HttpFhirSupport;
import it.unibo.HealthAdapterFacade.HealthServiceFhir;
import it.unibo.HealthAdapterFacade.HealthServiceInterface;
import it.unibo.HealthResource.ResourceUtility;


public class HealthServiceFhirUsageSynch {
 	
  	private HealthServiceInterface healthService;
  	private String serverBase="https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";   
    			
 	public HealthServiceFhirUsageSynch() {
 		healthService = new HealthServiceFhir( serverBase ) ;	
  	} 
 	
//CREATE  	
	public String createResourceFromFile( String fname ) {
		String jsonRep      = HttpFhirSupport.readFromFileJson( fname );
		String id           = healthService.createResourceSynch( jsonRep );
		return id;
 	}

//READ	
	public void readResource( String resourceType, String id )   {
		String answer = healthService.readResourceSynch(resourceType, id );
		System.out.println("readResource answer=" + answer );
  	}

//SEARCH
 	public void searchResource(String queryJson) {
 		String answer =  healthService.searchResourceSynch(queryJson);
		System.out.println("searchResource answer=" + answer );
  	}

 //UPDATE
 	public void updateResourceFromFile( String fname, String id ) {
 		DomainResource newresource = ResourceUtility.createResourceFromFileJson( fname );
 		//Inject the id
 		ResourceUtility.injectId(newresource, id  );
 		String newresourceJsonStr =  ResourceUtility.getJsonRep(newresource);
 		String answer = healthService.updateResourceSynch(newresourceJsonStr);
		System.out.println("updateResourceFromFile answer=" + answer );
  	}

//DELETE
 	public void deleteResource(  String resourceType, String id ) {
 		String answer = healthService.deleteResourceSynch(resourceType, id );
		System.out.println("deleteResource answer=" + answer );
  	}
	
	/*
	 * CRUD - synch: one step a the time ...
	 */	
	public static void main( String[] args) throws IOException {
		HealthServiceFhirUsageSynch appl = new HealthServiceFhirUsageSynch();
		String resourceFileName       = "src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejson.txt";
		String updateResourceFileName = "src/main/java/it/unibo/HealthResource/datafiles/PatientAlicejsonUpdate.txt";
		String queryStr         	  = 
				"{ \"resourceType\": \"Patient\", \"address\": { \"city\": \"Cesena\", \"country\": \"Italy\" } }"; 
  
		System.out.println(" %%% CREATE  ------------------------------ "); 		
		String id = appl.createResourceFromFile(resourceFileName);		 
		
		System.out.println(" %%% READ    ------------------------------ ");
		appl.readResource( "Patient", id );	
  		
		System.out.println(" %%% SEARCH  ------------------------------ ");
		appl.searchResource( queryStr );
		 		
		System.out.println(" %%% UPDATE  ------------------------------ ");
		appl.updateResourceFromFile(updateResourceFileName, id);
		 
		System.out.println(" %%% DELETE  ------------------------------ ");
 		appl.deleteResource( "Patient", id );	//
				
 		System.out.println(" %%% END  ------------------------------ ");
 
	}
}
