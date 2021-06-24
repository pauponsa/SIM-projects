package com.eventic.src.domain;

public class User extends Customer {
    //Class attributes
    private String image;
    private String location;
    private Double rating;
    //Relations with other classes
    private Company[] following;
    private Company[] reviewedCompanies;

    public User() {
    }

    public User (String name, String username, String email, String password, String repeatPassword, String image, String role) {
        setName(name);
        setUsername(username);
        setEmail(email);
        setPassword(password);
        setPassword_confirmation(repeatPassword);
        setImage(image);
        setRole(role);
    }

    public User(String email, String password) {
        setEmail(email);
        setPassword(password);
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) { this.rating = rating; }
}