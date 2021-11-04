package com.example.go4lunch.models;

public class Restaurant {
    private String Id;
    private String name;

    public Restaurant(String id, String name){
        this.Id = id;
        this.name = name;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
