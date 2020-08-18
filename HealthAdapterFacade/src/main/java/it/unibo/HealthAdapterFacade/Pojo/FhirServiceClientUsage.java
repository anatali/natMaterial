package it.unibo.HealthAdapterFacade.Pojo;
/*
 * ------------------------------------------------------------------------
 * Utilizza FhirServiceClient per interagire con CentroHealthFHIR
 * ------------------------------------------------------------------------
 */
 
import java.util.Iterator;
import org.apache.commons.lang3.tuple.Pair;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.util.BundleUtil;
import it.unibo.HealthAdapterFacade.FhirServiceClient;
import it.unibo.HealthAdapterFacade.HealthService;
import it.unibo.HealthResource.PatientResource;

 
public class FhirServiceClientUsage {

	private FhirServiceClient client        = new FhirServiceClient( "https://hapi.fhir.org/baseR4" );
	private PatientResource patientresource = new PatientResource();
	private String serverBase               = "https://hapi.fhir.org/baseR4";
	private String patientElenaJson         = "src/main/java/it/unibo/HealthResource/PatientElenaJson.txt";
	
	
	public Long createPatient( )  {
		Patient p = patientresource.createFhirPatientFromFileJson( patientElenaJson );
		Long id = client.createAndGetId( p );
		System.out.println("----------------- createPatient id= "  + id);	
		return id;
	}
	
	public void deletePatient( String resourceClassName, String id )  {
		String outcome = client.delete(resourceClassName, id);  //does nothing if does not exists??
		System.out.println("----------------- deletePatient   " + outcome );
	}
	
	public void readPatient(Long id) throws Exception {
 		Patient p    = client.readPatient(Patient.class, id ) ;	
		if( p == null ) return;
		String pJson = HealthService.cvtJson( p );		
		String pXml  = HealthService.cvtXml( p );		
		System.out.println("----------------- patient " + id + " in json  ---- ");
		System.out.println(pJson);
		System.out.println("----------------- patient " + id + " in xml   ---- ");
		System.out.println(pXml);
 	}

	
	public void search( Class<Patient> resourceClass, String name ) throws Exception {
		Bundle b                 = client.searchPatient(  resourceClass,  name );
		FhirContext theContext   = client.getFhirContext();
 		String bJson = HealthService.cvtJson( b  );	
 		Iterator<Pair<String, IBaseResource>>  iter = BundleUtil.getBundleEntryUrlsAndResources(theContext, b).iterator();
		System.out.println("----------------- found bundle for " + name + "  ---- ");
//		System.out.println(  bJson );
		while( iter.hasNext() ) {
			Pair<String, IBaseResource> rp = iter.next();
			IBaseResource r = rp.getRight();
			String rid      = ((Patient)r).getId() ;   
			String rname    = ((Patient)r).getName().get(0).toString();
			System.out.println("----------------- resource " + name + " id=" + rid  );
//			System.out.println(  client.cvtJson(r) );
		}		
	}
	
/*
 * Create, Read, Search, Delete	
 */
	public static void main(String[] args) throws Exception {
		FhirServiceClientUsage appl = new FhirServiceClientUsage();
		System.out.println(" %%% CREATE ------------------------------");
 		Long id = appl.createPatient();		
		System.out.println(" %%% READ  ------------------------------ ");
		appl.readPatient( id );
		//appl.readPatient(1433281L);
		System.out.println(" %%% SEARCH ----------------------------- ");
		appl.search(Patient.class, "ElenaBologna");
 		System.out.println(" %%% DELETE ----------------------------- ");
   		appl.deletePatient("Patient", id.toString() );
		System.out.println(" %%% SEARCH ----------------------------- ");
		appl.search(Patient.class, "ElenaBologna");
 	}
	
 }