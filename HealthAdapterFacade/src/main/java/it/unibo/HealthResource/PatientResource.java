package it.unibo.HealthResource;
/*
 * ------------------------------------------------------------------------
 * Support class to handle data of the FHIR resource Patient
 * ------------------------------------------------------------------------
 */  
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
 

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
	
private final String familyNameDefault         = "Unibo";
private final String givenNameDefault          = "Unknown";		
private final String businessIdSystemDefault   = "http://it.unibo/disi";
private final String businessIdValueDefault    = "987654321";
/*
 * CURRENT VALUES	
 */
private String 	familyName         = null;
private String 	giventName         = null;  
private String 	businessIdSystem   = null;  
private String 	businessIdValue    = null;  
 

/*
 * The Argument list should be as much complete as possible.
 */
	private void setCurrentValues( String familyNameArg, String giventNameArg ) {
		
		familyName = familyNameArg == null ? familyNameDefault : familyNameArg;
		giventName = giventNameArg == null ? givenNameDefault : familyNameArg;
		businessIdSystem =  givenNameDefault  ;
		businessIdValue  =  businessIdValueDefault;
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
				.setSystem( businessIdSystem )
				.setValue(  businessIdValue );
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
