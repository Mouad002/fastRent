package com.example.fastrentv2.Model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Person
{
    public static final String personIdField = "personId";
    public static final String fullNameField = "fullName";
    public static final String cityField = "city";
    public static final String phoneNumberField = "phoneNumber";
    public static final String emailField = "email";
    public static final String propertiesField = "properties";

    // attributes
    private String personId;
    private String fullName;
    private String city;
    private String phoneNumber;
    private String email;
    private ArrayList<String> properties;

    // constractors

    public Person(){

    }

    public Person(String personId, String fullName, String city, String phoneNumber, String email,  ArrayList<String> properties) {
        this.personId = personId;
        this.fullName = fullName;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.properties = properties;
    }

    // getters and setters

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getProperties() {
        return properties;
    }

    public void setProperties(ArrayList<String> properties) {
        if(this.properties!=null)
        {
            this.properties = properties;
        }
    }
}
