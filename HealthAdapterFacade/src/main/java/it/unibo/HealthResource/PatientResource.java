package it.unibo.HealthResource;

import java.io.FileInputStream;

import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;

public class PatientResource {
/*
* See https://www.hl7.org/fhir/R4/patient.html, https://www.hl7.org/fhir/patient-examples.html
* Patient:	DomainResource
* 	identifier 				: Identifier
* 	active     				: boolean
* 	name       				: HumanName				Element use,text,family,given,prefix,suffix,period
* 	telecom    				: ContactPoint			Element system:code, value:string, use:code,rank:positiveInt,period:Period
*   gender     				: code
*   deceasedBoolean 		: boolean
*   deceasedDateTime		: dateTime
*   address         		: Address
*   maritalStatus   		: CodeableConcept		Element coding:Coding text:string
*   multipleBirthBoolean	: boolean
*   multipleBirthInteger    : integer
*   photo           		: Attachment
*   contact         		: BackboneElement 		Element (relationship:language:CodeableConcept,name:HumanName,
*   	 													telecom:ContactPoint,address:Address,gender:code, 
*                                                            organization:Reference,period:Period)
*   communication   		: BackboneElement 		Element (language:CodeableConcept,preferred:boolean)
*   generalPractitioner 	: Reference
*   managingOrganization 	: Reference
*   link            		: BackboneElement 		Element (other: Reference, type:Code
*/
	
private final String familyNameDefault   = "Unibo";
private final String givenNameDefault    = "Unknown";		

/*
 * CURRENT VALUES	
 */
private String 	familyName  = null;
private String 	giventName = null;  
	

//	private FhirContext ctx = FhirContext.forR4();

/*
 * The Argument list should be as much complete as possible.
 */
	private void setCurrentValues( String familyNameArg, String giventNameArg ) {
		
		familyName = familyNameArg == null ? familyNameDefault : familyNameArg;
		giventName = giventNameArg == null ? givenNameDefault : familyNameArg;
 
	}
	

	public Patient createFhirPatient(String familyNameArg,String giventNameArg) {
		
		setCurrentValues( familyNameArg,  giventNameArg );
		
		Patient newPatient = new Patient();
		newPatient
		.addName()
			.setFamily( familyName )
			.addGiven(  giventName );
			//.addGiven("anothername");
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
	//	newPatient
	//		.addChild("AnnaBologna");
		newPatient
			.addTelecom()
			.setValue("Contact Unibo");
		return newPatient;
		
	}

}
