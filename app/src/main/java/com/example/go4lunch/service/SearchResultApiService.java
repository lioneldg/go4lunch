package com.example.go4lunch.service;

import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.NearbySearchResult;

import java.util.ArrayList;
import java.util.List;

public class SearchResultApiService implements  InterfaceSearchResultApiService{
    private List<NearbySearchResult> nearbySearchResultList = new ArrayList<>();
    private DetailSearchResult detailSearchResult = null;

    @Override
    public List<NearbySearchResult> getNearbySearchResults() {
        return nearbySearchResultList;
    }

    public DetailSearchResult getDetailSearchResult() { return detailSearchResult; }

    @Override
    public void addNearbySearchResult(NearbySearchResult nearbySearchResult) {
        nearbySearchResultList.add(nearbySearchResult);
    }

    public void setDetailSearchResult(DetailSearchResult dsr){
        detailSearchResult = dsr;
    }

    @Override
    public void clearNearbySearchResult() {
        nearbySearchResultList = new ArrayList<>();
    }
}
