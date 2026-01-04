package com.rezaul.foodrushseller.models;

import com.google.gson.annotations.SerializedName;

public class MenuItem {

    private Long id;
    private String name;
    private Double price;

    @SerializedName("veg")
    private Boolean veg;

    private Boolean available;
    private String description;
    private Long restaurantId;
    private String imagePath;

    // Constructors
    public MenuItem() {
    }

    public MenuItem(String name, Double price, Boolean veg, String description) {
        this.name = name;
        this.price = price;
        this.veg = veg;
        this.description = description;
        this.available = true;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Boolean getVeg() {
        return veg;
    }

    public Boolean getAvailable() {
        return available;
    }

    public String getDescription() {
        return description;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public String getImagePath() {
        return imagePath;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setVeg(Boolean veg) {
        this.veg = veg;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", veg=" + veg +
                ", available=" + available +
                ", description='" + description + '\'' +
                ", imagePath='" + imagePath + '\'' +
                '}';
    }
}