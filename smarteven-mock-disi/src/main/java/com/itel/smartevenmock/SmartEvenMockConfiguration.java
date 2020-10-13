package com.itel.smartevenmock;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IBasicClient;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.IRestfulClient;
import ca.uhn.fhir.rest.client.interceptor.BasicAuthInterceptor;
//import com.itel.healthadapter.api.HealthAdapterAPI;
import feign.Feign;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.itel.healthadapter.api.HealthAdapterAPI;

@Configuration
public class SmartEvenMockConfiguration {

    private final SmartEvenMockConfigurationProperties configurationProperties;

    public SmartEvenMockConfiguration(SmartEvenMockConfigurationProperties configurationProperties) {
        this.configurationProperties = configurationProperties;
    }

    @Bean
    public HealthAdapterAPI healthAdapterClient() {
        Feign.Builder builder = Feign.builder();
        builder.contract(new SpringMvcContract());
//		builder.encoder(new JacksonEncoder());
//		builder.decoder(new JacksonDecoder());
    	HealthAdapterAPI client = 
    			builder.target(HealthAdapterAPI.class, configurationProperties.getHealthAdapterBaseUrl());
    	System.out.println("	%%% SmartEvenMockConfiguration healthAdapterClient client=" + client );
        return client;
    }

    @Bean
    public FhirContext fhirContext() {
        return FhirContext.forR4();
    }

    @Bean
    public ICustomFhirClient customFhirClient(FhirContext fhirContext) {
        String serverBase = configurationProperties.getFhirServerBaseUrl();
        ICustomFhirClient fhirClient = fhirContext.newRestfulClient(ICustomFhirClient.class, serverBase);
        addBasicAuthInterceptor(fhirClient);
    	System.out.println("	%%% SmartEvenMockConfiguration customFhirClient fhirClient CREATED"   );
        return fhirClient;
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
