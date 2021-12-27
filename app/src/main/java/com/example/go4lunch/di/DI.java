package com.example.go4lunch.di;

import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.service.SearchResultApiService;

/**
 * Dependency injector to get instance of services
 */
public class DI {
    private static final InterfaceSearchResultApiService service = new SearchResultApiService(); //for the first call of di
    public static InterfaceSearchResultApiService getSearchResultApiService(){
        return service;
    }
}
