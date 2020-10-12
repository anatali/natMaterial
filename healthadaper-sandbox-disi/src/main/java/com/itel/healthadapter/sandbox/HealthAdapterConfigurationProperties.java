package com.itel.healthadapter.sandbox;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "health.adapter")
public class HealthAdapterConfigurationProperties {

    private String fhirServerBaseUrl;
    private String fhirServerUsername;
    private String fhirServerPassword;

    public String getFhirServerBaseUrl() {
        return fhirServerBaseUrl;
    }

    public void setFhirServerBaseUrl(String fhirServerBaseUrl) {
        this.fhirServerBaseUrl = fhirServerBaseUrl;
    }

    public String getFhirServerUsername() {
        return fhirServerUsername;
    }

    public void setFhirServerUsername(String fhirServerUsername) {
        this.fhirServerUsername = fhirServerUsername;
    }

    public String getFhirServerPassword() {
        return fhirServerPassword;
    }

    public void setFhirServerPassword(String fhirServerPassword) {
        this.fhirServerPassword = fhirServerPassword;
    }
}
