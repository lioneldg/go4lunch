package com.example.go4lunch.ui;
import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.MapFragmentBinding;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.service.InterfaceSearchResultApiService;

import com.example.go4lunch.tools.UrlRequest;
import com.example.go4lunch.tools.VectorToBitmap;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapFragmentBinding binding;
    private Boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap googleMap;
    private ArrayList<String> placesIds;
    private SupportMapFragment mapFragment;
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MapFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        locationPermissionGranted = false;

        //test if google play is available
        // Can be ConnectionResult.SUCCESS, SERVICE_MISSING, SERVICE_UPDATING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED, SERVICE_INVALID
        boolean googleApiAvailability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS;

        mapFragment = SupportMapFragment.newInstance();
        //register the onMapReady callback method
        mapFragment.getMapAsync(this);
        //add map fragment to current view
        getParentFragmentManager().beginTransaction().add(view.getId(), mapFragment).commit();
        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        return view;
    }

   @Override
    public void onMapReady(@NonNull @NotNull GoogleMap gm) {
        googleMap = gm;
        getLocationPermission();
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
            updateLocationUI();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            locationPermissionGranted = true;
            updateLocationUI();
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
                        if (lastKnownLocation != null) {
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            placeSearchNearBy(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
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

    private void placeSearchNearBy(double lat, double lng){
        placesIds = new ArrayList<String>();
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
                try {
                    //parse results to JSON + add each result to searchResultArrayList
                    JSONObject resultObject = new JSONObject(urlRequestResult);
                    JSONArray placesArray = resultObject.getJSONArray("results");
                    for (int i = 0; placesArray.length() > i; i++) {
                        NearbySearchResult nearbySearchResult = new NearbySearchResult();

                        JSONObject place = new JSONObject(placesArray.get(i).toString());
                        String name = place.getString("name");
                        String address = place.getString("vicinity");
                        String place_id = place.getString("place_id");
                        int rating = (int) Math.round((place.getDouble("rating") / 5) * 3);

                        JSONObject geometry = place.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        Double lat = location.getDouble("lat");
                        Double lng = location.getDouble("lng");

                        JSONObject opening_hours = place.getJSONObject("opening_hours");
                        boolean open_now = opening_hours.getBoolean("open_now");

                        JSONObject photos = new JSONObject(place.getJSONArray("photos").get(0).toString());
                        String photoRef = photos.getString("photo_reference");

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
        });
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            for(int i = 0; service.getNearbySearchResults().size() > i; i++){
                NearbySearchResult nearbySearchResult = service.getNearbySearchResults().get(i);
                LatLng position = new LatLng(nearbySearchResult.getLat(), nearbySearchResult.getLng());
                addMarkerOption(position, nearbySearchResult.getName());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void addMarkerOption(LatLng position, String title){
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(title).icon(VectorToBitmap.convert(getResources(), R.drawable.ic_baseline_restaurant_24)));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
