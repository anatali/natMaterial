package HapiPOST;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.api.SearchStyleEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.util.BundleUtil;

public class SearchWithPost {

	public final static FhirContext fhirctx = FhirContext.forR4();
	public final static String SERVER_BASE = "https://hapi.fhir.org/baseR4";

	public static void main(String[] args) {
		// FhirServiceClient
		IGenericClient client = fhirctx.newRestfulGenericClient(SERVER_BASE);

		// Costruisce la query come una Map (chiave-valore)
		Map<String, List<String>> query = new HashMap<>();
		query.put("given", Arrays.asList("Smith"));
		query.put("family", Arrays.asList("Liam"));

		// In questo caso richiede tutti i pazienti con nominativo = Smith Liam
		List<Patient> patients = searchPatients(client, query);

		patients.stream()
			.map(p -> p.getNameFirstRep().getNameAsSingleString())
			.forEach(System.out::println);
	}


	private static List<Patient> searchPatients(final IGenericClient client, final Map<String, List<String>> query) {
		Bundle response = client.search()
				.forResource("Patient")
				.whereMap(query)
				.usingStyle(SearchStyleEnum.POST) // Forza il metodo HTTP a POST
				.returnBundle(Bundle.class)
				.execute();

		return BundleUtil.toListOfResourcesOfType(fhirctx, response, Patient.class);
	}

	/*
	 * Parametri utilizzabili all'interno della query
	 *
	 * "[
	 *    FinancialTransactionDate, SearchByDate, SearchByMsgDate, _id, _language,
	 *    active, address, address-city, address-country, address-postalcode, address-state,
	 *    address-use, addresscontenttype, birthdate, birthplace-city, birthtime,
	 *    commentExtension, death-date, deceased, email, eyecolour, family, gender,
	 *    gender2, general-practitioner, given, identifier, language, link, middlename,
	 *    mothersMaidenName, nachname, name, organization, organizationIdentifier, phone,
	 *    phonetic, race, sex, telecom, verfall
	 * ]"
	 */

}
