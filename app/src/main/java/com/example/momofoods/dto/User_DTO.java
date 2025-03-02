package com.example.momofoods.dto;

public class User_DTO {

    private String name;
    private String mobile;
    private String email;

    public User_DTO(String string, String string1, String string2, String string3, String string4, int i, String number) {
    }

    // Constructors, getters, and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
