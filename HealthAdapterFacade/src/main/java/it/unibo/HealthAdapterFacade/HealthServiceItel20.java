package it.unibo.HealthAdapterFacade;

import reactor.core.publisher.Flux;

public class HealthServiceItel20 implements HealthServiceInterface{

	@Override
	public Flux<String> createResourceAsynch(String jsonStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<String> readResourceAsynch(String resourceType, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<String> searchResourceAsynch(String jsonTemplate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<String> updateResourceAsynch(String newresourceJsonStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<String> deleteResourceAsynch(String resourceType, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String createResourceSynch(String jsonStr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String readResourceSynch(String resourceType, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String searchResourceSynch(String queryjson) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String updateResourceSynch(String newresourceJsonStr) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String deleteResourceSynch(String resourceType, String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<String> cvthl7tofhir(String template, String data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Flux<String> docvthl7tofhir(String path, String data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setHealthService(String choice, String serverAddr) {
		// TODO Auto-generated method stub
	}

}
