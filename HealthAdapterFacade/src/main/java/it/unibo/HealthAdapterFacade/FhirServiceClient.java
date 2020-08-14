package it.unibo.HealthAdapterFacade;

import org.hl7.fhir.instance.model.api.IDomainResource;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FhirServiceClient {
	private String serverBase=""; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";  http://localhost:9001/r4
	private FhirContext ctx ;  
    // Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
 	IGenericClient client ; //= ctx.newRestfulGenericClient(serverBase);
 	boolean useJson = true;
 	
 	public FhirServiceClient(String serverBase, boolean useJson) {
 		this.serverBase = serverBase;
 		this.useJson    = useJson;
 		ctx             = FhirContext.forR4();
 		client          = ctx.newRestfulGenericClient(serverBase);
		System.out.println("FhirServiceClient created for " + serverBase + " useJson=" + useJson);
  	}

 	public FhirContext  getFhirContext() {
 		return ctx; 		
 	}
 	
 	public MethodOutcome create( IDomainResource theResource ) {
 		try { 
			MethodOutcome outcome = client
					.create()
					.resource(theResource)
					.execute(); 
	 		return outcome;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient create  ERROR " + e.getMessage());
			return null;
		}
 	}
 	
 	public DomainResource read( Class<DomainResource> resourceClass, Long id ) {
 		try { 
	 		DomainResource resource   = client.read()
				.resource( resourceClass )
				.withId(id)
				.execute(); //Construct a read for the given resource type 
	 		return resource;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient read " + id + " ERROR " + e.getMessage());
			return null;
		}
 	}
 	public Patient readPatient( Class<Patient> resourceClass, Long id ) {
 		try { 
	 		Patient resource   = client.read()
				.resource( resourceClass )
				.withId(id)
				.execute(); //Construct a read for the given resource type 
	 		return resource;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient readPatient " + id + " ERROR " + e.getMessage());
			return null;
		}
 	}
 	
 	public Bundle search( Class<DomainResource> resourceClass, String name ) { 
 		try { 
	 		Bundle results = client
	 				.search()
	 				.forResource( resourceClass  )
	 				.where(Patient.NAME.matches().value(name))
	 				.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
	 				.execute(); 
	 		return results;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient search " + name + " ERROR " + e.getMessage());
			return null;
		}
	}
 	
	public Bundle searchPatient( Class<Patient> resourceClass, String name) {
 	try { 
 		Bundle results = client
 				.search()
 				.forResource( resourceClass  )
 				.where(Patient.NAME.matches().value(name))
 				.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
 				.execute(); 
 		return results;
	} catch ( Exception e) {	//ResourceNotFoundException
		System.out.println("FhirServiceClient searchPatient  ERROR " + e.getMessage());
		return null;
	}
	}
	
	public MethodOutcome delete(String className, String id) {
 	try { 
		MethodOutcome response = client
		   .delete()
		   .resourceById(new IdType(className, id))
		   .execute();
		OperationOutcome outcome = (OperationOutcome) response.getOperationOutcome();
		System.out.println("FhirServiceClient delete  outcome " + outcome);
		return response;
	} catch ( Exception e) {	//ResourceNotFoundException
		System.out.println("FhirServiceClient delete  ERROR " + e.getMessage());
		return null;
	}
	}
 	
/*
 * CONVERSIONS	
 */
 	public String cvt( IDomainResource theResource ) {
 		if( useJson ) return cvtJson(theResource);
 		else return cvtXml(theResource);
 	}
 	public String cvt( Bundle bundle ) {
 		if( useJson ) return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
 		else return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
 	}

 	public String cvtJson( IDomainResource theResource ) {
 		return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(theResource);
 	}

 	public String cvtXml( IDomainResource theResource ) {
 		return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(theResource);
 	}

}
