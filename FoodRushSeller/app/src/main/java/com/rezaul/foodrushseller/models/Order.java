package com.rezaul.foodrushseller.models;

import java.util.List;

public class Order {

    private Long id;
    private Long restaurantId;
    private Double totalAmount;
    private String status;
    private List<MenuItem> items;

    public Long getId() {
        return id;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public List<MenuItem> getItems() {
        return items;
    }
}
