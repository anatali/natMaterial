package healthAdapter.healthAdapterAppl;
import healthAdapter.UseCase;
import healthAdapter.port.in.HAServiceInterface;

@UseCase
public class HAService implements HAServiceInterface {
	
	public HAService() {
		System.out.println("			 %%% HAService CREATED");
	}


	/*
	 *  IMPLEMENTATION SYNCH
	 */
	
	public String setImportPolicy( String  policy) {
		String outS = "HAService setImportPolicy with policy=" + policy;
		System.out.println(outS);
		return outS;
	}
	
	public String importPatient(String  patientIdentifier )  {
		//String outS = "HAService importPatient for patientIdentifier=" + patientIdentifier;
		String outS = "importPatient: search in HCServer a patient with patientIdentifier=" + patientIdentifier 
				+" and next do a PUT of (part of) the answer) to ITEL-FHIR";
		System.out.println(outS);
		return patientIdentifier+"(todo)";		
	}
}