package com.example.go4lunch.service;

import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.NearbySearchResult;

import java.util.List;

public interface InterfaceSearchResultApiService {
    List<NearbySearchResult> getNearbySearchResults();
    DetailSearchResult getDetailSearchResult();

    void addNearbySearchResult(NearbySearchResult nearbySearchResult);
    void setDetailSearchResult(DetailSearchResult dsr);
    void clearNearbySearchResult();
}
