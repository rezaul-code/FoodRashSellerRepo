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

    // Getters
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

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public void setOpen(Boolean open) {
        this.open = open;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public void setBannerImageUrl(String bannerImageUrl) {
        this.bannerImageUrl = bannerImageUrl;
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }
}