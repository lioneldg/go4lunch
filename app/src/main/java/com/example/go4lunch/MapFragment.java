package com.example.go4lunch;
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
import com.example.go4lunch.databinding.MapFragmentBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import org.jetbrains.annotations.NotNull;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private MapFragmentBinding binding;
    private Boolean locationPermissionGranted;
    private Location lastKnownLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private static final int DEFAULT_ZOOM = 15;
    private GoogleMap googleMap;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = MapFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        locationPermissionGranted = false;

        //test if google play is available
        // Can be ConnectionResult.SUCCESS, SERVICE_MISSING, SERVICE_UPDATING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED, SERVICE_INVALID
        boolean googleApiAvailability = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(requireContext()) == ConnectionResult.SUCCESS;

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        //register the onMapReady callback method
        mapFragment.getMapAsync(this);
        //add map fragment to current view
        getParentFragmentManager().beginTransaction().add(view.getId(), mapFragment).commit();

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Construct a PlacesClient
        Places.initialize(requireContext(), MAPS_API_KEY);
        PlacesClient placesClient = Places.createClient(requireContext());

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
            getDeviceLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            locationPermissionGranted = true;
            updateLocationUI();
            getDeviceLocation();
        }
    });

    private void updateLocationUI() {
        if (googleMap == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
