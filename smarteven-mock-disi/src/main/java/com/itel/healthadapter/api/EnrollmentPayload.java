package com.itel.healthadapter.api;

public class EnrollmentPayload {
    private String body;

//    public EnrollmentPayload() { super(); }  
    //default constructor to overcome Jackson
    //ERROR: cannot deserialize from Object value (no delegate- or property-based Creator)
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}