package com.example.fastrentv2.Model;

public class Person
{
    // attributes

    private String personId;
    private String fullName;
    private String city;
    private String phoneNumber;
    private String email;

    // constractors

    public Person(){

    }

    public Person(String fullName, String city, String phoneNumber, String email) {
        this.fullName = fullName;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public Person(String personId, String fullName, String city, String phoneNumber, String email) {
        this.personId = personId;
        this.fullName = fullName;
        this.city = city;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // getters

    public String getPersonId() {
        return personId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCity(){return city;};

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    // setters

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
