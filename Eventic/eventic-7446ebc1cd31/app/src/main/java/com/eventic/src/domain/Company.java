package com.eventic.src.domain;

import android.media.Image;


public class Company extends Customer {
    //Class attributes
    private Image logo;
    private String NIF;
    private String companyName;
    private String description;


    public Company(String email, String password,  String repeatPassword){
        setEmail(email);
        setPassword(password);
        setPassword_confirmation(password);
    }

    public Company(String username, String name, String email, String password,  String repeatPassword, String role){
        setUsername(username);
        setName(name);
        setEmail(email);
        setPassword(password);
        setPassword_confirmation(password);
        setRole(role);
    }

    public Image getLogo() {
        return logo;
    }

    public void setLogo(Image logo) {
        this.logo = logo;
    }

    public String getNIF() {
        return NIF;
    }

    public void setNIF(String NIF) {
        this.NIF = NIF;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
