package it.unibo.HealthResource;
import java.io.FileInputStream;
 
/*
 * ------------------------------------------------------------------------
 * Support class to handle data of the FHIR resource Patient
 * ------------------------------------------------------------------------
 */  
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.r4.model.DateType;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import it.unibo.HealthAdapterFacade.HealthService;
 
 

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
 

public final static FhirContext fhirctx = HealthService.fhirctx;

/*
 * The Argument list should be as much complete as possible.
 */
	private void setCurrentValues( String familyNameArg, String giventNameArg ) {
		
		familyName       = familyNameArg == null ? familyNameDefault : familyNameArg;
		giventName       = giventNameArg == null ? givenNameDefault  : giventNameArg;
		businessIdSystem =  givenNameDefault  ;
		businessIdValue  =  businessIdValueDefault;
	}
	

	public Patient createFhirPatient(String familyNameArg,String giventNameArg) {
		System.out.println("PatientResource createFhirPatient familyNameArg"+ familyNameArg + " giventNameArg=" + giventNameArg );
		setCurrentValues( familyNameArg,  giventNameArg );
		System.out.println("PatientResource createFhirPatient familyName"+ familyName + " giventName=" + giventName );
		
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
	
	public Patient createFhirPatientFromFileJson(String fileName) {
		try {			
			FileInputStream fis = new FileInputStream(fileName);
		    String data         = IOUtils.toString(fis, "UTF-8");
		    //System.out.println("createPatientFromFile data"+data );
		    IParser parser      = fhirctx.newJsonParser();
		    Patient newPatient  = parser.parseResource(Patient.class, data);	
		    return newPatient;
		} catch (Exception e) {
			System.out.println("createFhirPatientFromFileJson ERROR"+ e.getMessage() );
			return null;
		}
	}
	
	public Patient createFhirPatientFromJson( String jsonrep ) {
 		try {
// 	 	    ObjectMapper mapper = new ObjectMapper();
// 		    JsonFactory factory = mapper.getFactory();
//			JsonParser parser  = factory.createParser( jsonrep );
//		    JsonNode actualObj = mapper.readTree(parser);
		    IParser parserfhir = fhirctx.newJsonParser();
		    Patient patient    = parserfhir.parseResource(Patient.class, jsonrep);	
		    return patient;
		} catch ( Exception e) {
 			e.printStackTrace();
 			return null;
		}		
	}

}
