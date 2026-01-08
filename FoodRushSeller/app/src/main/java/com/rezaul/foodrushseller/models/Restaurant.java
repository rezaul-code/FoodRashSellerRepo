package com.rezaul.foodrushseller.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Restaurant {
    private Long id;
    private String name;
    private String description;
    private String address;
    private Double rating;
    private Boolean open;
    private Long sellerId;

    // Kept as Enums for Restaurant functionality
    private LocationArea locationArea;
    private DeliveryType deliveryType;

    @SerializedName("bannerImageUrl")
    private String bannerImageUrl;
    private List<MenuItem> menuItems;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getRating() { return rating; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public LocationArea getLocationArea() { return locationArea; }
    public void setLocationArea(LocationArea locationArea) { this.locationArea = locationArea; }
    public DeliveryType getDeliveryType() { return deliveryType; }
    public void setDeliveryType(DeliveryType deliveryType) { this.deliveryType = deliveryType; }
    public String getBannerImageUrl() { return bannerImageUrl; }
    public void setBannerImageUrl(String bannerImageUrl) { this.bannerImageUrl = bannerImageUrl; }
    public List<MenuItem> getMenuItems() { return menuItems; }
    public void setMenuItems(List<MenuItem> menuItems) { this.menuItems = menuItems; }
}