package com.itel.healthadapter.sandbox.smartevenmock;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "smarteven.mock")
public class SmartEvenMockConfigurationProperties {

    private String healthAdapterBaseUrl;
    private String fhirServerBaseUrl;
    private String fhirServerUsername;
    private String fhirServerPassword;
    private String fhirNotificationWebSocketUrl;

    public String getHealthAdapterBaseUrl() {
        return healthAdapterBaseUrl;
    }

    public void setHealthAdapterBaseUrl(String healthAdapterBaseUrl) {
        this.healthAdapterBaseUrl = healthAdapterBaseUrl;
    }

    public String getFhirServerBaseUrl() {
        return fhirServerBaseUrl;
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

    public void setFhirServerBaseUrl(String fhirServerBaseUrl) {
        this.fhirServerBaseUrl = fhirServerBaseUrl;
    }

    public String getFhirNotificationWebSocketUrl() {
        return fhirNotificationWebSocketUrl;
    }

    public void setFhirNotificationWebSocketUrl(String fhirNotificationWebSocketUrl) {
        this.fhirNotificationWebSocketUrl = fhirNotificationWebSocketUrl;
    }
}