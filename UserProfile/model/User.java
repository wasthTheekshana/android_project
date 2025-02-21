package com.example.userprofilefetcher.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String avatarColor;

    // Constructor and getters/setters
    public User(String id, String name, String email, String phone, String avatarColor) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.avatarColor = avatarColor;
    }

    public User(String id, String name, String email, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    // Add getters and setters


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatarColor() {
        return avatarColor;
    }

    public void setAvatarColor(String avatarColor) {
        this.avatarColor = avatarColor;
    }
}