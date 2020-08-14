package it.unibo.HealthAdapterFacade;

public interface HealthServiceInterface {

	public Long create_patient(String name);
	public String search_for_patients_named(String name, boolean usejson);
	public void delete_patient(String id);
	public String read_a_resource(Long id);
	//public void setHealthService(String choice);	
}
