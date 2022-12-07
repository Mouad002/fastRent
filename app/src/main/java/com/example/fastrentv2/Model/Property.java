package com.example.fastrentv2.Model;

public class Property
{
    // attributes

    private String propertyId;
    private String propertyTitle;
    private String city;
    private double price;
    private String description;
    private String category;
    private String image;
    private String ownerId;

    // constractors

    public Property(){}

    public Property(String propertyId, String propertyTitle, String city, double price, String description, String category, String image, String ownerId) {
        this.propertyId = propertyId;
        this.propertyTitle = propertyTitle;
        this.city = city;
        this.price = price;
        this.description = description;
        this.category = category;
        this.image = image;
        this.ownerId = ownerId;
    }

    // getters and setters

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyTitle() {
        return propertyTitle;
    }

    public void setPropertyTitle(String propertyTitle) {
        this.propertyTitle = propertyTitle;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String categoriy) {
        this.category = categoriy;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }
}
