package com.example.momofoods.dto;

public class Food_DTO {
    private String ProductName;
    private String ProductDescription;
    private String Price;
    private String ImageUrl;
    private String Rating;
    private String Calories;
    private int Quantity;
    private String Category;

    private String datetime;

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public Food_DTO(String productName, String productDescription, String price, String imageUrl, String rating, String calories, int quantity, String category, String datetime) {
        ProductName = productName;
        ProductDescription = productDescription;
        Price = price;
        ImageUrl = imageUrl;
        Rating = rating;
        Calories = calories;
        Quantity = quantity;
        Category = category;
        this.datetime = datetime;

    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getCalories() {
        return Calories;
    }

    public void setCalories(String calories) {
        Calories = calories;
    }

    public String getRating() {
        return Rating;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
