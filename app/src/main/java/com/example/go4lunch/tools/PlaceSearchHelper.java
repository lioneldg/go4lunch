package com.example.go4lunch.tools;

import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.Restaurant;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlaceSearchHelper {

    public static Restaurant placeResultToRestaurant(String urlRequestResult, String spotId){
        Restaurant restaurant = null;

        try {
            JSONObject resultObject = new JSONObject(urlRequestResult);
            JSONObject result = resultObject.getJSONObject("result");
            String phone = result.optString("formatted_phone_number");
            String website = result.optString("website");
            JSONArray _photos =  result.optJSONArray("photos");
            JSONObject photos = _photos != null ? _photos.getJSONObject(0) : null;
            String photoRef = photos != null ? photos.optString("photo_reference") : null;

            String name = result.optString("name");
            String vicinity = result.optString("vicinity");
            int rating = (int) Math.round(result.optDouble("rating") / 5 * 3);
            JSONObject geometry = result.getJSONObject("geometry");
            JSONObject location = geometry.getJSONObject("location");
            double lat = location.getDouble("lat");
            double lng = location.getDouble("lng");
            JSONObject opening_hours = result.optJSONObject("opening_hours");
            boolean open_now = opening_hours != null && opening_hours.getBoolean("open_now");

            restaurant = new Restaurant(spotId, name);
            restaurant.setPhone(phone);
            restaurant.setWebsite(website);
            restaurant.setPhoto_reference(photoRef);
            restaurant.setVicinity(vicinity);
            restaurant.setRating(rating);
            restaurant.setLat(lat);
            restaurant.setLng(lng);
            restaurant.setOpen_now(open_now);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurant;
    }

    public static  ArrayList<Restaurant> placeResultToPredictions(String urlRequestResult){
        ArrayList<Restaurant> restaurantList = new ArrayList<>();
        JSONArray predictions;
        try {
            //parse results to JSON + add result to searchResult
            JSONObject predictionsObject = new JSONObject(urlRequestResult);
            predictions = predictionsObject.getJSONArray("predictions");
            for(int i = 0; i < predictions.length(); i++){
                String name;
                String placeId;
                JSONObject currentPredictions = (JSONObject) predictions.get(i);
                JSONArray currentTypesArray = currentPredictions.getJSONArray("types");
                for(int j = 0; j < currentTypesArray.length(); j++){
                    if(currentTypesArray.get(j).equals("restaurant")){
                        name = currentPredictions.optString("description");
                        placeId = currentPredictions.getString("place_id");
                        restaurantList.add(new Restaurant(placeId, name));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return restaurantList;
    }

    public static ArrayList<NearbySearchResult> getNearBySearchResult(String urlRequestResult){
        ArrayList<NearbySearchResult> nearbySearchResultArrayList = new ArrayList<>();
        try {
            //parse results to JSON + add each result to searchResultArrayList
            JSONObject resultObject = new JSONObject(urlRequestResult);
            JSONArray placesArray = resultObject.getJSONArray("results");
            for (int i = 0; placesArray.length() > i; i++) {
                NearbySearchResult nearbySearchResult = new NearbySearchResult();
                JSONObject place = new JSONObject(placesArray.get(i).toString());
                String name = place.optString("name");
                String address = place.optString("vicinity");
                String place_id = place.getString("place_id");
                int rating = (int) Math.round(place.optDouble("rating") / 5 * 3);
                JSONObject geometry = place.getJSONObject("geometry");
                JSONObject location = geometry.getJSONObject("location");
                double lat = location.getDouble("lat");
                double lng = location.getDouble("lng");
                JSONObject opening_hours = place.optJSONObject("opening_hours");
                boolean open_now = opening_hours != null && opening_hours.getBoolean("open_now");
                JSONArray _photos =  place.optJSONArray("photos");
                JSONObject photos = _photos != null ? _photos.getJSONObject(0) : null;
                String photoRef = photos != null ? photos.optString("photo_reference") : null;
                nearbySearchResult.setName(name);
                nearbySearchResult.setVicinity(address);
                nearbySearchResult.setPlace_id(place_id);
                nearbySearchResult.setRating(rating);
                nearbySearchResult.setLat(lat);
                nearbySearchResult.setLng(lng);
                nearbySearchResult.setOpen_now(open_now);
                nearbySearchResult.setPhoto_reference(photoRef);
                nearbySearchResultArrayList.add(nearbySearchResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nearbySearchResultArrayList;
    }
}
