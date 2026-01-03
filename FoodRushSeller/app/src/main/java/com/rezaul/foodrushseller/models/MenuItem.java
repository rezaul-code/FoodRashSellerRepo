package com.rezaul.foodrushseller.models;

public class MenuItem {

    private Long id;
    private String name;
    private Double price;
    private Boolean veg;
    private Boolean available;
    private String description;
    private Long restaurantId;
    private String imagePath;

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
}
