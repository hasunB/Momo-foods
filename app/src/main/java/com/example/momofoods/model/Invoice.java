package com.example.momofoods.model;

public class Invoice {

    private int InvoiceNo;
    private String userMobile;
    private String userName;
    private String address;
    private double TotalPrice;
    private double fee;
    private String productId;
    private String name;
    private String description;
    private int price;
    private int quantity;
    private String datetime;
    private int status;

    public Invoice(int invoiceNo, int quantity, int price, String description, String name, String productId, double fee, double totalPrice, String address, String userName, String userMobile, String datetime, int status) {
        InvoiceNo = invoiceNo;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
        this.name = name;
        this.productId = productId;
        this.fee = fee;
        TotalPrice = totalPrice;
        this.address = address;
        this.userName = userName;
        this.userMobile = userMobile;
        this.datetime = datetime;
        this.status = status;
    }

    public int getInvoiceNo() {
        return InvoiceNo;
    }

    public void setInvoiceNo(int invoiceNo) {
        InvoiceNo = invoiceNo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
