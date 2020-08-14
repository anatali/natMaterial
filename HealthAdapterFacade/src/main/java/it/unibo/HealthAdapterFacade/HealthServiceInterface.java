package it.unibo.HealthAdapterFacade;

import org.hl7.fhir.r4.model.Patient;

public interface HealthServiceInterface {
/*
 * For each operation, the argument list here
 * is related to dynamic data inserted by an operator using a GUI
 */
	public Long create_patient( Patient newPatient );
	public Long createPatientFromFile(String fileName );
	public Long create_patient(String familyName,String name);
	public String search_for_patients_named(String name, boolean usejson);
	public void delete_patient(String id);
	public String read_a_resource(Long id);

}
