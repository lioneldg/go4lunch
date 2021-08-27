package com.example.go4lunch.service;

import com.example.go4lunch.models.NearbySearchResult;

import java.util.List;

public interface InterfaceSearchResultApiService {
    List<NearbySearchResult> getNearbySearchResults();

    void addNearbySearchResult(NearbySearchResult nearbySearchResult);
    void clearNearbySearchResult();
}
