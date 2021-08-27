package com.example.go4lunch.service;

import com.example.go4lunch.models.NearbySearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchResultApiService implements  InterfaceSearchResultApiService{
    private List<NearbySearchResult> nearbySearchResultList = new ArrayList<>();

    @Override
    public List<NearbySearchResult> getNearbySearchResults() {
        return nearbySearchResultList;
    }

    @Override
    public void addNearbySearchResult(NearbySearchResult nearbySearchResult) {
        nearbySearchResultList.add(nearbySearchResult);
    }

    @Override
    public void clearNearbySearchResult() {
        nearbySearchResultList = new ArrayList<>();
    }

}
