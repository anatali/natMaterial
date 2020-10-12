package com.itel.healthadapter.sandbox;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HealthAdapterSandboxConfiguration {

    private final HealthAdapterConfigurationProperties configurationProperties;

    public HealthAdapterSandboxConfiguration(HealthAdapterConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public IGenericClient genericFhirClient(FhirContext fhirContext) {
        FhirContext ctx = FhirContext.forR4();
        String serverBase = configurationProperties.getFhirServerBaseUrl();
        IGenericClient fhirClient = ctx.newRestfulGenericClient(serverBase);
        addBasicAuthInterceptor(fhirClient);
        return fhirClient;
    }

    private void addBasicAuthInterceptor(IRestfulClient client) {
        BasicAuthInterceptor authInterceptor = new BasicAuthInterceptor(configurationProperties.getFhirServerUsername(), configurationProperties.getFhirServerPassword());
        client.registerInterceptor(authInterceptor);
    }
}