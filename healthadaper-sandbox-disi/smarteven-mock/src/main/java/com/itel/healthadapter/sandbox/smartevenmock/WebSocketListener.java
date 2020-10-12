package com.itel.healthadapter.sandbox.smartevenmock;

import ca.uhn.fhir.rest.client.api.IGenericClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itel.healthadapter.api.Constants;
import com.itel.healthadapter.api.HealthAdapterAPI;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Predicate;

@Component
public class WebSocketListener {

    private Logger logger = LoggerFactory.getLogger(WebSocketListener.class);

    private final HealthAdapterAPI healthAdapterClient;
    private final IGenericClient genericFhirClient;
    private final SmartEvenMockConfigurationProperties configurationProperties;

    public WebSocketListener(
            HealthAdapterAPI healthAdapterClient,
            IGenericClient genericFhirClient,
            SmartEvenMockConfigurationProperties configurationProperties
    ) {
        this.healthAdapterClient = healthAdapterClient;
        this.genericFhirClient = genericFhirClient;
        this.configurationProperties = configurationProperties;
    }

/*    
    @PostConstruct
    public void init() throws NoSuchAlgorithmException, IOException, WebSocketException {
        WebSocketFactory factory = new WebSocketFactory();
        WebSocket ws = factory.createSocket(configurationProperties.getFhirNotificationWebSocketUrl());
        ws.addListener(new WebSocketAdapter() {
            @Override
            public void onTextMessage(WebSocket websocket, String message) throws Exception {

                IbmFhirEvent event =  new ObjectMapper().readValue(message, IbmFhirEvent.class);

                logger.info("Received message on websocket " + configurationProperties.getFhirNotificationWebSocketUrl());

                logger.info(message);

                if (event.getResourceType().equals(Patient.class.getSimpleName())) {
                    Bundle bundle = genericFhirClient.search()
                            .forResource(Patient.class)
                            .where(Patient.RES_ID.exactly().code(event.getResourceId()))
                            .returnBundle(Bundle.class)
                            .execute();

                    Patient patient = (Patient)bundle.getEntry().get(0).getResource();

                    Optional<String> taxCode = patient.getIdentifier().stream()
                            .filter(taxCodeIdentifier())
                            .findAny().map(Identifier::getValue);

                    // TODO Handle import result
                    taxCode.map(s -> healthAdapterClient._import(taxCode.get()))
                            .orElseThrow(() -> new IllegalStateException("Tax code identifier not present in patient " + patient.getId()));
                }
            }
        });

        logger.info("Connecting to websocket " + configurationProperties.getFhirNotificationWebSocketUrl());
        ws.connect();
    }
*/
    private Predicate<Identifier> taxCodeIdentifier() {
        return identifier -> identifier.getType().getCoding().stream().anyMatch(
                coding -> coding.getSystem().equals(Constants.FHIR_IDENTIFIER_TYPE) && coding.getCode().equals(Constants.FHIR_IDENTIFIED_TYPE_TAX));
    }
}
