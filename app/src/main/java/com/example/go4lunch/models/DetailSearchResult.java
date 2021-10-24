package com.example.go4lunch.models;

public class DetailSearchResult {
    private String name = "";
    private String place_id = "";
    private double lat;
    private double lng;
    private boolean open_now;
    private String photo_reference = "";
    private double rating;
    private String vicinity = "";
    private int distanceBetween;
    private String website = " ";
    private String phone = " ";

    public DetailSearchResult(String _place_id, String _name, String _photoRef, double _rating, String _vicinity, String _website, String _phone){
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

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public boolean getOpen_now() {
        return open_now;
    }

    public void setOpen_now(boolean open_now) {
        this.open_now = open_now;
    }

    public String getPhoto_reference() {
        return photo_reference;
    }

    public void setPhoto_reference(String photo_reference) {
        this.photo_reference = photo_reference;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public int getDistanceBetween() { return distanceBetween; }

    public void setDistanceBetween(int distanceBetween) { this.distanceBetween = distanceBetween; }

    public String getWebsite() { return website; }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }
}
