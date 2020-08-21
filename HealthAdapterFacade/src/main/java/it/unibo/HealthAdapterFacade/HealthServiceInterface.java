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
		public Flux<String> readResourceAsynch(String resourceType,Long id);   		   		 
		public Flux<String> createResourceAsynch(String jsonStr);   		 
		public Flux<String> searchResourceAsynch(String jsonTemplate);
		public Flux<String> updateResourceAsynch( String newresourceJsonStr  );
		public Flux<String> deleteResourceAsynch( String resourceType, String id ); 

/*
 *  SYNCH
 */
		public Long createResourceSynch(String jsonStr);
}
