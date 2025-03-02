package com.example.momofoods.model;

public class Validations {

    public static boolean isEmailValid(String email){
        return email.matches("^[a-zA-Z0-9_.±]+@[a-zA-Z0-9-]+.[a-zA-Z0-9-.]+$");
    }

    public static boolean isPasswordValid(String password){
        return password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,10}$");
    }

    public static boolean isDouble(String price){
        return price.matches("^\\d+(\\.\\d{2})?$");
    }

    public static boolean isInteger(String price){
        return price.matches("^\\d+$");
    }
}
