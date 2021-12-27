package com.example.go4lunch.service;

import android.location.Location;

import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

public interface InterfaceSearchResultApiService {
    List<NearbySearchResult> getNearbySearchResults();
    DetailSearchResult getDetailSearchResult();
    ArrayList<Restaurant> getAutoCompleteList();

    void addNearbySearchResult(NearbySearchResult nearbySearchResult);
    void setDetailSearchResult(DetailSearchResult dsr);
    ArrayList<User> getWorkmatesList();
    void addWorkmatesList(User workmate);
    void addAutoCompleteList(Restaurant restaurant);
    void clearNearbySearchResult();
    void clearWorkmatesList();
    void clearAutoCompleteList();
    void setLastKnownLocation(Location location);
    Location getLastKnowLocation();
}
