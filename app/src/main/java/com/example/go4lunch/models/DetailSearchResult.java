package com.example.go4lunch.models;

public class DetailSearchResult {
    private String name;
    private final String place_id;
    private final String photo_reference;
    private final int rating;
    private final String vicinity;
    private final String website;
    private final String phone;

    public DetailSearchResult(String _place_id, String _name, String _photoRef, int _rating, String _vicinity, String _website, String _phone){
        this.place_id = _place_id;
        this.name = _name;
        this.photo_reference = _photoRef;
        this.rating = _rating;
        this.vicinity = _vicinity;
        this.website = _website;
        this.phone = _phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlace_id() {
        return place_id;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public int getRating() {
        return rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public String getWebsite() { return website; }

    public String getPhone() { return phone; }
}
