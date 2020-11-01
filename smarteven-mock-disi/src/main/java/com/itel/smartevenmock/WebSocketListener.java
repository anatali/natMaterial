package com.itel.smartevenmock;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itel.healthadapter.api.Constants;
import com.itel.healthadapter.api.HealthAdapterAPI;
//import com.itel.healthadapter.api.Constants;
//import com.itel.healthadapter.api.HealthAdapterAPI;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import healthAdapter.itelData.ImportPayload;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Predicate;

import com.itel.healthadapter.api.StatusReference;

@Component
public class WebSocketListener {

    private Logger logger = LoggerFactory.getLogger(WebSocketListener.class);

    private final HealthAdapterAPI healthAdapterClient; //Miracles of @FeignClient 
    private final IGenericClient genericFhirClient;
    private final SmartEvenMockConfigurationProperties configurationProperties;

    public WebSocketListener(
            HealthAdapterAPI healthAdapterClient,
            IGenericClient genericFhirClient,
            SmartEvenMockConfigurationProperties configurationProperties
    ) {
        this.healthAdapterClient     = healthAdapterClient;
        this.genericFhirClient       = genericFhirClient;
        this.configurationProperties = configurationProperties;
    }

     
    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException, WebSocketException {
        WebSocketFactory factory = new WebSocketFactory();
        WebSocket ws = factory.createSocket(configurationProperties.getFhirNotificationWebSocketUrl());
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {
                IbmFhirEvent event =  new ObjectMapper().readValue(message, IbmFhirEvent.class);
                logger.info("Received message on websocket " + configurationProperties.getFhirNotificationWebSocketUrl());
                System.out.println("WebSocketListener | message=" + message);
                String resourceType = event.getResourceType();
                System.out.println( "WebSocketListener | resourceType=" + resourceType);
                String op           = event.getOperationType();
                System.out.println( "WebSocketListener | OPERATION="+op  );

                if (resourceType.equals(Patient.class.getSimpleName())) {
                 	if( op.equals("update")) return;    //AVOID LOOP
                    Bundle bundle = genericFhirClient.search()
                            .forResource(Patient.class)
                            .where(Patient.RES_ID.exactly().code(event.getResourceId()))
                            .returnBundle(Bundle.class)
                            .execute();

                    Patient patient  = (Patient)bundle.getEntry().get(0).getResource();        
                    String patientid = patient.getIdElement().getIdPart();

                    Optional<String> taxCode = patient.getIdentifier().stream()
                            .filter(taxCodeIdentifier())
                            .findAny().map(Identifier::getValue);

                    //Handle import result See com.itel.healthadapter.api.HealthAdapterAPI
                    System.out.println("WebSocketListener | import businessId=" + taxCode.get() + " for patientid=" + patientid);
                    //healthAdapterClient._import(patientid, taxCode.get());
                    ImportPayload payload = new ImportPayload();
                    payload.setResourceId(patientid);
                    payload.setBusinessId(taxCode.get());
                    System.out.println("WebSocketListener | import payload=" + payload );
                    //StatusReference sr = healthAdapterClient._import( payload.toString() );
                    //System.out.println("WebSocketListener | sr= " + sr );
                    StatusReference res =
                            taxCode.map(s -> healthAdapterClient._importExisting( payload.toString() ))
                            .orElseThrow(() -> 
                            new IllegalStateException("Tax code identifier not present in patient " + patient.getId()));
                
                    System.out.println("WebSocketListener | import " + res.getLocation() );
                
                }
            }
        }); 
 
        logger.info("Connecting to websocket " + configurationProperties.getFhirNotificationWebSocketUrl());
        ws.connect();
    }
 
    private Predicate<Identifier> taxCodeIdentifier() {
        return identifier -> identifier.getType().getCoding().stream().anyMatch(
                coding -> coding.getSystem().equals(Constants.FHIR_IDENTIFIER_TYPE) && coding.getCode().equals(Constants.FHIR_IDENTIFIED_TYPE_TAX));
    }
}