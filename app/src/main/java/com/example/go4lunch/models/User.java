package com.example.go4lunch.models;

import androidx.annotation.Nullable;

public class User {

    private String uid;
    private String username;
    @Nullable
    private String urlPicture;
    private String restaurantName;
    private String restaurantId;

    public User() { }

    public User(String uid, String username, @Nullable String urlPicture, String restName, String restId) {
        this.uid = uid;
        this.username = username;
        this.urlPicture = urlPicture;
        this.restaurantName = restName;
        this.restaurantId = restId;
    }

    // --- GETTERS ---
    public String getUid() { return uid; }
    public String getUsername() { return username; }
    @Nullable
    public String getUrlPicture() { return urlPicture; }
    public String getRestaurantName() { return restaurantName; }
    public String getRestaurantId() { return restaurantId; }

    // --- SETTERS ---
    public void setRestaurantName(String restName) { this.restaurantName = restName; }
    public void setRestaurantId(String restId) { this.restaurantId = restId; }
}
