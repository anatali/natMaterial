package com.itel.healthadapter.sandbox.smartevenmock;

public class IbmFhirEvent {

    public static final String OP_TYPE_CREATE = "create";

    private String lastUpdated;
    private String location;
    private String operationType;
    private String resourceId;

    public String getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        if (location == null)
            throw new IllegalStateException("This method should be called after initialization");
        String[] tokens = this.location.split("/");
        if (tokens.length == 0)
            throw new IllegalStateException("Wrong location format");
        return tokens[0];
    }
}
