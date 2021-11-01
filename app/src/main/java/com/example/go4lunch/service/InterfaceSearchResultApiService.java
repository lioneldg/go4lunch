package com.example.go4lunch.service;

import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

public interface InterfaceSearchResultApiService {
    List<NearbySearchResult> getNearbySearchResults();
    DetailSearchResult getDetailSearchResult();

    void addNearbySearchResult(NearbySearchResult nearbySearchResult);
    void setDetailSearchResult(DetailSearchResult dsr);
    ArrayList<User> getWorkmatesList();
    void addWorkmatesList(User workmate);
    void delWorkmatesList(int index);
    void clearNearbySearchResult();
    void clearWorkmatesList();
}
