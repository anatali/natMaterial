package com.itel.healthadapter.api;

public class StatusReference {

    private String location;
    
    //default constructor to overcome Jackson
    //ERROR: cannot deserialize from Object value (no delegate- or property-based Creator)
    public StatusReference() {
    	super();
    	location = "todo";
    }

    public StatusReference(String location) {
        this.location = location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }
}