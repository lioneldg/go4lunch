package com.example.go4lunch.ui;

import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.di.DI;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.FragmentMapBinding;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.UrlRequest;
import com.example.go4lunch.tools.VectorToBitmap;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private Boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap googleMap;
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private Set<String> restIdSet;
    private static ClickListener itemListener;
    private View view;
    protected SpotsFragment spotsFragment;
    private SupportMapFragment mapFragment;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMapBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        itemListener = (ClickListener) getActivity();
        locationPermissionGranted = false;
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        prepareRestIdSet();
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        mapFragment = SupportMapFragment.newInstance();

        getLocationPermission();

        //add map fragment to current view
        getParentFragmentManager().beginTransaction().replace(view.getId(), mapFragment).commit();
    }

    private void prepareRestIdSet(){
        restIdSet = new HashSet<>();
        ArrayList<User> workmates = service.getWorkmatesList();
        for(int i = 0; i < workmates.size(); i++){
            if(!workmates.get(i).getRestaurantId().equals("")) {
                restIdSet.add(workmates.get(i).getRestaurantId());
            }
        }
    }

   @Override
    public void onMapReady(@NonNull @NotNull GoogleMap gm) {
        googleMap = gm;
        googleMap.setOnMarkerClickListener(marker -> {
            String placeId = (String) marker.getTag();
            itemListener.listClicked(placeId);
            return false;
        });
       updateLocationUI();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            //register the onMapReady callback method
            mapFragment.getMapAsync(this);
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            locationPermissionGranted = true;
            //register the onMapReady callback method
            mapFragment.getMapAsync(this);
        }
    });

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                getDeviceLocation();
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                service.setLastKnownLocation(null);
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        try {
            if (googleMap != null && locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            lastKnownLocation = task.getResult();
                            service.setLastKnownLocation(lastKnownLocation);
                            if (lastKnownLocation != null) {
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                                placeSearchNearBy(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                            } else {
                                getDeviceLocation();
                            }
                        } else {
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    protected void placeSearchNearBy(double lat, double lng){
        String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                "json?location="+lat+","+lng+
                "&rankby=distance"+
                "&types=restaurant"+
                "&key="+
                MAPS_API_KEY;
        placeSearchExecutor(placesSearchStr);

    }

    private void placeSearchExecutor(String url){
        //execute query
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if(service.getNearbySearchResults().size() == 0) {
                String urlRequestResult = UrlRequest.execute(url);
                addNearBySearchResult(urlRequestResult);
            }
        });
        try {
            executor.shutdown();
            executor.awaitTermination(4, TimeUnit.SECONDS);
             if(this.isVisible()){
                for (int i = 0; service.getNearbySearchResults().size() > i; i++) {
                    NearbySearchResult nearbySearchResult = service.getNearbySearchResults().get(i);
                    LatLng position = new LatLng(nearbySearchResult.getLat(), nearbySearchResult.getLng());
                    addMarkerOption(position, nearbySearchResult.getName(), nearbySearchResult.getPlace_id());
                }
            }
             if(spotsFragment != null && spotsFragment.isVisible()){
                 spotsFragment.onDataSetChange();
             }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addNearBySearchResult(String urlRequestResult){
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

            float[] distanceBetweenArray = new float[1];
            Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), lat, lng, distanceBetweenArray);
            int distanceBetween = Math.round(distanceBetweenArray[0]);

            nearbySearchResult.setName(name);
            nearbySearchResult.setVicinity(address);
            nearbySearchResult.setPlace_id(place_id);
            nearbySearchResult.setRating(rating);
            nearbySearchResult.setLat(lat);
            nearbySearchResult.setLng(lng);
            nearbySearchResult.setOpen_now(open_now);
            nearbySearchResult.setPhoto_reference(photoRef);
            nearbySearchResult.setDistanceBetween(distanceBetween);
            service.addNearbySearchResult(nearbySearchResult);
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void addMarkerOption(LatLng position, String title, String placeId){
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title)
                .icon(VectorToBitmap.convert(getResources(), R.drawable.ic_baseline_restaurant_24, restIdSet.contains(placeId))));
        assert marker != null;
        marker.setTag(placeId);
    }

    protected void clearMap(){
        googleMap.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public interface ClickListener {
        void listClicked(String spotId);
    }
}
