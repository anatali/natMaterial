package it.unibo.Handlebars;

import java.util.ArrayList;
import java.util.List;

public class Person {
    private String name;  
    private Address address      = new Address(); 
    private List<Person> friends = new ArrayList<>();
    
    public List<Person> getFriends() { return friends; }
    public void setName(String v) { name = v; }    
    public String getName() { return name;    }
    public Address getAddress() { return address; }
    
    
    
    public static class Address {
        private String street;       
        public void setStreet(String v) { street = v; }    
        public String getStreet() { return street;    }
    }    
    
}
