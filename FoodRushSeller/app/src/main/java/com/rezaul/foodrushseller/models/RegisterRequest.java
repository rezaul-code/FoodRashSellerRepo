package com.rezaul.foodrushseller.models;

public class RegisterRequest {

    private String name;
    private String phone;
    private String password;
    private String businessName;

    public RegisterRequest(String name, String phone, String password, String businessName) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.businessName = businessName;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getBusinessName() {
        return businessName;
    }
}
