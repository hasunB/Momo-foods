package com.example.momofoods.dto;

public class Employees_DTO {
    private String name;
    private String phone;
    private String email;
    private String password;
    private String id;
    private int role;
    private String datetime;

    public Employees_DTO(String name, String phone, String email, String id, int role, String password, String datetime) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.id = id;
        this.role = role;
        this.password = password;
        this.datetime = datetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
