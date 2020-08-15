package it.unibo.HealthAdapterFacade;
/*
 * ------------------------------------------------------------------------
 * Provides a set of operations  relted to the business logic
 * ------------------------------------------------------------------------
 */
import org.hl7.fhir.r4.model.Patient;

public interface HealthServiceInterface {
	public Long create_patient( Patient newPatient );
	public Long createPatientFromFile(String fileName );
	public Long create_patient(String familyName,String name);
	public String search_for_patients_named(String name, boolean usejson);
	public void delete_patient(String id);
	public String read_a_resource(Long id);
}
