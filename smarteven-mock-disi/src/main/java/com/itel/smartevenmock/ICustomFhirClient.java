package com.itel.smartevenmock;

import ca.uhn.fhir.rest.annotation.RequiredParam;
import ca.uhn.fhir.rest.annotation.Search;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

import java.util.List;

public interface ICustomFhirClient extends IBasicClient {

    @Search
    public List<Patient> findPatientsByIdentifier(@RequiredParam(name = Patient.SP_IDENTIFIER) IdType theIdentifier);
}
