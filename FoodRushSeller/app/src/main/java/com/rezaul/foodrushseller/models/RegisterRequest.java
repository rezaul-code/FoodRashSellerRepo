package com.rezaul.foodrushseller.models;

public class RegisterRequest {
    private String name;
    private String phone;
    private String password;
    private String businessName;
    private String email;

    public RegisterRequest(String name, String phone, String password, String businessName, String email) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.businessName = businessName;
        this.email = email;
    }

    // Getters
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getBusinessName() { return businessName; }
    public String getEmail() { return email; }
}