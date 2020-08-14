package it.unibo.HealthAdapterFacade;

import org.hl7.fhir.instance.model.api.IIdType;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.OperationOutcome;
import org.hl7.fhir.r4.model.Patient;
import ca.uhn.fhir.rest.api.MethodOutcome;


public class HealthServiceFhir implements HealthServiceInterface {
  	
	private FhirServiceClient fhirclient;	 
	
	public HealthServiceFhir(String serverBase) {
 		System.out.println("HealthServiceFhir created for " + serverBase  );
		fhirclient = new FhirServiceClient(serverBase,true);	//true => UseJson
	}
	 
	public Long create_patient(String name) {
		try {
	    // Create a client. See https://hapifhir.io/hapi-fhir/docs/client/generic_client.html
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
 		MethodOutcome outcome = fhirclient.create(newPatient);		
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
	
	public String search_for_patients_named(String name, boolean usejson) {
		Bundle results = fhirclient.searchPatient( Patient.class, name );		
		//System.out.println("First page: ");
		String res = fhirclient.cvt(results);
		return res;
/*		
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
*/ 		
	}
	
	
	public void delete_patient(String id) {
		System.out.println("delete_patient id=" + id);
		try {
		MethodOutcome response = fhirclient.delete("Patient", id);
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
	
	/*
	 * The FHIR client returns an object of type Patient that can be converted in XML or JSON
	 * curl http://localhost:8081/readResource/1432878 -i -X GET
	 */
	public String read_a_resource( Long id ) { 
 		try { 
 			Patient patient = fhirclient.readPatient(Patient.class, id);
 			if( patient == null ) {
 				return "<resource><text>Resource " + id + "</text><text>resource not found</text></resource>";
 			} 			
 			System.out.println( "HealthService patient name="+patient.getName().toString() ); 			
 			String res = fhirclient.cvt(patient);
 			return res;			
		} catch ( Exception e) {	//ResourceNotFoundException
			System.out.println("HealthService Resource " + id + " ERROR " + e.getMessage());
			return "<resource><text>Resource " + id + "</text><text>" +  e.getMessage() +"</text></resource>";
		}

	}

 
	
}
