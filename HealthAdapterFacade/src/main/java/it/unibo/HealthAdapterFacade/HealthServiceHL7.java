package it.unibo.HealthAdapterFacade;
/*
 * ------------------------------------------------------------------------
 * Provides a set of operations defined by the HealthServiceInterface  
 * by using ... TODO
 * ------------------------------------------------------------------------
 */
import org.hl7.fhir.r4.model.Patient;

public class HealthServiceHL7 implements HealthServiceInterface{

	public HealthServiceHL7(String serverBase) {
		//fhirclient = new FhirServiceClient(serverBase,true);	//true => UseJson
	}

	@Override
	public Long createPatientFromFile(String fileName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long create_patient(Patient newPatient) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long create_patient(String familyName,String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String search_for_patients_named(String name, boolean usejson) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void delete_patient(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String read_a_resource(Long id) {
		// TODO Auto-generated method stub
		return null;
	}



}
