package com.example.momofoods.model;

public class User {

    private String name;
    private String email;
    private String password;
    private String mobile;
    private int vcode;
    private String address;
    private String datetime;

    public User(String name, String datetime, String address, int vcode, String mobile, String password, String email) {
        this.name = name;
        this.datetime = datetime;
        this.address = address;
        this.vcode = vcode;
        this.mobile = mobile;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getVcode() {
        return vcode;
    }

    public void setVcode(int vcode) {
        this.vcode = vcode;
    }
}
