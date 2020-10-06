package healthAdapter.port.in;

/*
----------- GLOSSARY  ----------- 
HC   		= Health Center
HA   		= Health Adapter
EVEN 		= SmartEven (I-Tel Health Application)
ITEL-FHIR 	= I-Tel Fhir Server
 */

public interface HAServiceInterface {
	/*
	 * ============================================================
	 *  SYNCH API ONLY (ate the moment?)
	 * ============================================================
	 */
	
	public String setImportPolicy( String policy);
	/*
	    Single target policy
		Prioritized targets policy
		Resource-based target policy
			: we assume Single target policy
	 */
	
/*
Return a reference to a FHIR Resource representing the status of the requested operation.
*/
	public String importPatient( String patientIdentifier );	//import is a reserved word in Java
	/*
 	Retrieve (via an HC-specific HTTP request including the given patientIdentifier) 
	the patient’s data from the HC and store the corresponding representation on ITEL-FHIR. 
	If the patient already exists on ITEL-FHIR it must be updated instead of being created. 
	HA should perform a search operation on ITEL-FHIR, using the given patientIdentifier, 
	to check whether the patient already exists. In this iteration, patient data on
	ITEL-FHIR will be overridden by those on HC in the case of an update.
	*/	
	
}