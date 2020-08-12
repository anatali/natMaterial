package it.unibo.HealthAdapterFacade;

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.stereotype.Service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;

 

@Service
public class HealthService {
	
	public static final String createPatientUri  ="/createPatient";
	public static final String searchPatientUri  ="/searchPatient";
	public static final String readResourceUri   ="/readResource";
	public static final String deleteResourceUri ="/deleteResource";
	
	private String serverBase = "https://hapi.fhir.org/baseR4"; //"http://localhost:9001/r4"; //"https://hapi.fhir.org/baseR4";  http://localhost:9001/r4
	private FhirContext ctx = FhirContext.forR4();
	// Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
//	private IGenericClient client = ctx.newRestfulGenericClient(serverBase);

	
	
	public Long create_patient(String name) {
		try {
	    // Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
	 	IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		// Create a patient
		Patient newPatient = new Patient();
		// Populate the patient with fake information
		newPatient
			.addName()
				.setFamily("Unibo")
				.addGiven(name)
				.addGiven("ToUnderstand");
		newPatient
			.addIdentifier()
				.setSystem("http://it.unibo/disi")
				.setValue("987654321");
		newPatient.setGender(Enumerations.AdministrativeGender.FEMALE);
		newPatient.setBirthDateElement(new DateType("2000-11-18"));
 
		newPatient
			.addAddress()
				.setCity("Bologna")
				.setCountry("Italy")
				.setText("some text");
//		newPatient
//			.addChild("AnnaBologna");
		newPatient
			.addTelecom()
			.setValue("Contact Unibo");

		System.out.println("Created patient : " +  newPatient ); //newPatient.getBirthDateElement()
		// Create a client
//	 	IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		// Create the resource on the server
		MethodOutcome outcome = client
			.create()
			.resource(newPatient)
			.execute();
		// Log the ID that the server assigned
		IIdType id = outcome.getId();
		Long idVal = id.getIdPartAsLong();
		System.out.println("Created patient, got ID: " + id + " value=" + idVal );
		return idVal;
	} catch ( Exception e) {	//ResourceNotFoundException
		System.out.println("create_patient ERROR " + e.getMessage() );
		return 0L;
	}
	}
	
	public String search_for_patients_named(String name) {
		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		org.hl7.fhir.r4.model.Bundle results = client
			.search()
			.forResource(Patient.class)
			.where(Patient.NAME.matches().value(name))
			.returnBundle(org.hl7.fhir.r4.model.Bundle.class)
			.execute();

 		//System.out.println("First page: ");
		String res = ctx.newXmlParser().encodeResourceToString(results);
 		//System.out.println(res);
		// Load the next page (???)
 		try {
			org.hl7.fhir.r4.model.Bundle nextPage = client
				.loadPage()
				.next(results)
				.execute();
			if( nextPage != null ) {
				System.out.println("Next page: ");
				String res1 = ctx.newXmlParser().encodeResourceToString(nextPage);
				return res1;
			}else  return res;
 		}catch( Exception e) {
 			System.out.println("WARNING: " + e.getMessage() );
 			return res;
 		}

	}
	
	
	public void delete_patient(String id) {
		// Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
 		IGenericClient client = ctx.newRestfulGenericClient(serverBase);
		System.out.println("delete_patient id=" + id);
		try {
		MethodOutcome response = client
				   .delete()
				   .resourceById(new IdType("Patient", id))
				   .execute();

				// outcome may be null if the server didn't return one
				OperationOutcome outcome = (OperationOutcome) response.getOperationOutcome();
				if (outcome != null) {
				   //System.out.println("delete_patient outcome=" + outcome.getIssueFirstRep().getDetails().getCodingFirstRep().getCode());
				   System.out.println("delete_patient outcome=" + outcome.getIssueFirstRep().getDetails() );
				}else { 
					System.out.println("delete_patient outcome is null" );
				}
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("delete_patient ERROR " + e.getMessage() );
 		}
	}
	
	
	
	public String read_a_resource(Long id) { 
		// Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
 		IGenericClient client = ctx.newRestfulGenericClient(serverBase);

		Patient patient;
		try { 
 			patient       = client.read().resource(Patient.class).withId(id).execute();
 			
 			System.out.println( "Birth date="+patient.getBirthDate().toString() ); 
 			String string = ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(patient);
 			//System.out.println(string);
 			return  string;
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("Resource " + id + " ERROR " + e.getMessage());
			return "<resource><text>Resource " + id + "</text><text>" +  e.getMessage() +"</text></resource>";
		}

	}

	
	public String prettyFormat(String input, int indent) {
	    try {
	        Source xmlInput = new StreamSource(new StringReader(input));
	        StringWriter stringWriter = new StringWriter();
	        StreamResult xmlOutput = new StreamResult(stringWriter);
	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        transformerFactory.setAttribute("indent-number", indent);
	        Transformer transformer = transformerFactory.newTransformer(); 
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        transformer.transform(xmlInput, xmlOutput);
	        return xmlOutput.getWriter().toString();
	    } catch (Exception e) {
	    	System.out.println("prettyFormat ERROR " + e.getMessage());
	    	return "";
 	    }
	}
	
}
