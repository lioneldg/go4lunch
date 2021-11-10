package com.example.go4lunch.service;

import android.location.Location;

import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.ui.WorkmateListAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchResultApiService implements  InterfaceSearchResultApiService{
    private List<NearbySearchResult> nearbySearchResultList = new ArrayList<>();
    private DetailSearchResult detailSearchResult = null;
    private ArrayList<User> workmatesList = new ArrayList<>();
    private ArrayList<Restaurant> autocompleteList = new ArrayList<>();
    private Location lastKnownLocation = null;

    @Override
    public List<NearbySearchResult> getNearbySearchResults() {
        return nearbySearchResultList;
    }

    public DetailSearchResult getDetailSearchResult() { return detailSearchResult; }

    @Override
    public ArrayList<Restaurant> getAutoCompleteList() {
        return autocompleteList;
    }

    @Override
    public void addNearbySearchResult(NearbySearchResult nearbySearchResult) {
        nearbySearchResultList.add(nearbySearchResult);
    }

    public void setDetailSearchResult(DetailSearchResult dsr){
        detailSearchResult = dsr;
    }

    public ArrayList<User> getWorkmatesList() {
        return workmatesList;
    }

    public void addWorkmatesList(User workmate) {
        this.workmatesList.add(workmate);
    }

    @Override
    public void addAutoCompleteList(Restaurant restaurant) {
        this.autocompleteList.add(restaurant);
    }

    @Override
    public void delWorkmatesList(int index) {
        this.workmatesList.remove(index);
    }

    @Override
    public void delAutoCompleteList(int index) {
        this.autocompleteList.remove(index);
    }

    @Override
    public void clearNearbySearchResult() {
        nearbySearchResultList = new ArrayList<>();
    }

    @Override
    public void clearWorkmatesList() {
        workmatesList = new ArrayList<>();
    }

    @Override
    public void clearAutoCompleteList() {
        this.autocompleteList = new ArrayList<>();
    }

    @Override
    public void setLastKnownLocation(Location location) {
        lastKnownLocation = location;
    }

    @Override
    public Location getLastKnowLocation() {
        return lastKnownLocation;
    }
}
