package com.rezaul.foodrushseller.models;

import java.util.List;

public class Restaurant {

    private Long id;
    private String name;
    private String description;
    private String address;
    private Double rating;
    private Boolean open;
    private Long sellerId;
    private String bannerImageUrl;
    private List<MenuItem> menuItems;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public Double getRating() {
        return rating;
    }

    public Boolean getOpen() {
        return open;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getBannerImageUrl() {
        return bannerImageUrl;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
}
