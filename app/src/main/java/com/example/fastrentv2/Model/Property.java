package com.example.fastrentv2.Model;

public class Property
{
    // attributes

    private int propertyId;
    private String propertyName;
    private String city;
    private boolean activated;
    private double price;
    private String description;
    private int rentDuration;

    // constractors

    public Property(){}

    // getters

    public int getPropertyId() {
        return propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public String getCity() {
        return city;
    }

    public boolean isActivated() {
        return activated;
    }

    public double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public int getRentDuration() {
        return rentDuration;
    }

    // setters

    public void setPropertyId(int propertyId) {
        this.propertyId = propertyId;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRentDuration(int rentDuration) {
        this.rentDuration = rentDuration;
    }
}
