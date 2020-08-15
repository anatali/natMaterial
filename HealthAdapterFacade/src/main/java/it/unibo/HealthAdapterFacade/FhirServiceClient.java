package it.unibo.HealthAdapterFacade;

/*
 * ------------------------------------------------------------------------
 * Utility to be used internally (within the HealthProduct or appl)
 * ------------------------------------------------------------------------
 */
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DomainResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

public class FhirServiceClient {
	//private String serverBase=""; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";  http://localhost:9001/r4
	private FhirContext ctx ;  
    // Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
 	IGenericClient client ; //= ctx.newRestfulGenericClient(serverBase);
  	
 	public FhirServiceClient( String serverBase ) {
  		ctx             = FhirContext.forR4();
 		client          = ctx.newRestfulGenericClient(serverBase);
		System.out.println("FhirServiceClient created for " + serverBase  );
  	}

 	public FhirContext  getFhirContext() {
 		return ctx; 		
 	}
 	
 	public MethodOutcome create( IBaseResource theResource ) {
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
 	
 	public Long createAndGetId( IBaseResource theResource ) {
 		try { 
			MethodOutcome outcome = client
					.create()
					.resource(theResource)
					.execute(); 
			// Log the ID that the server assigned
			IIdType id = outcome.getId();
			Long idVal = id.getIdPartAsLong();
			System.out.println("createAndGetId patient ID: " + id + " value=" + idVal );
			return idVal;		
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("FhirServiceClient createAndGetId  ERROR " + e.getMessage());
			return 0L;
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
 	
 	public String readInJson( Class<DomainResource> resourceClass, Long id ) {
 		DomainResource r = read(resourceClass, id);
 		return cvtJson( r );
 	}
 	public String readInXml( Class<DomainResource> resourceClass, Long id ) {
 		DomainResource r = read(resourceClass, id);
 		return cvtXml( r );
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
 	public String cvt( IBaseResource theResource,  boolean useJson ) {
 		if( useJson ) return cvtJson(theResource);
 		else return cvtXml(theResource);
 	}
 	public String cvt( Bundle bundle,  boolean useJson ) {
 		if( useJson ) return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(bundle);
 		else return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(bundle);
 	}

 	public String cvtJson( IBaseResource theResource ) {
 		return ctx.newJsonParser().setPrettyPrint(true).encodeResourceToString(theResource);
 	}

 	public String cvtXml( IBaseResource theResource ) {
 		return ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(theResource);
 	}

}

//public String prettyFormat(String input, int indent) {
//try {
//    Source xmlInput = new StreamSource(new StringReader(input));
//    StringWriter stringWriter = new StringWriter();
//    StreamResult xmlOutput = new StreamResult(stringWriter);
//    TransformerFactory transformerFactory = TransformerFactory.newInstance();
//    transformerFactory.setAttribute("indent-number", indent);
//    Transformer transformer = transformerFactory.newTransformer(); 
//    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//    transformer.transform(xmlInput, xmlOutput);
//    return xmlOutput.getWriter().toString();
//} catch (Exception e) {
//	System.out.println("prettyFormat ERROR " + e.getMessage());
//	return "";
// }
//}
