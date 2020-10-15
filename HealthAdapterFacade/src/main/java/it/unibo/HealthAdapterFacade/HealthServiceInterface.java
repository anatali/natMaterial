/*
 * ------------------------------------------------------------------------
 * Provides a set of operations  related to the business logic
 * ------------------------------------------------------------------------
 */
package it.unibo.HealthAdapterFacade;
import reactor.core.publisher.Flux;

public interface HealthServiceInterface {
/*
* ASYNCH: String are JsonString
*/
		public Flux<String> createResourceAsynch(String jsonStr);   		 
		public Flux<String> readResourceAsynch(String resourceType,String id);   		   		 
		public Flux<String> searchResourceAsynch(String jsonTemplate);
		public Flux<String> updateResourceAsynch( String newresourceJsonStr  );
		public Flux<String> deleteResourceAsynch( String resourceType, String id ); 

/*
 *  SYNCH
 */
		public String createResourceSynch(String jsonStr);
		//public Long   createResourceSynchLong(String jsonStr);
		public String readResourceSynch(String resourceType,String id);   		   		 
		public String searchResourceSynch(String queryjson);
		public String updateResourceSynch( String newresourceJsonStr );   		   		 
		public String deleteResourceSynch( String resourceType, String id ); 
		
/*
 * SELECT
 */
		public void setHealthService(String choice, String serverAddr);
		
/*
 *  CONVERT
*/
		public  Flux<String> cvthl7tofhir( String template, String data );
		public  Flux<String> docvthl7tofhir( String path, String data ) ;
}

