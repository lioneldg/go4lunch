package com.example.go4lunch.ui;
import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.databinding.ActivitySpotDetailBinding;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PhotoRefToBitmap;
import com.example.go4lunch.tools.UrlRequest;
import com.example.go4lunch.ui.manager.UserManager;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SpotDetailActivity extends AppCompatActivity {
    private ActivitySpotDetailBinding binding;
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private NearbySearchResult nearbySearchResult;
    private Bitmap photo;
    private ImageView image;
    private TextView name;
    private TextView address;
    private FloatingActionButton fab;
    private UserManager userManager = UserManager.getInstance();
    private ArrayList<User> workmateList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpotDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        image = binding.detailImage;
        name = binding.detailName;
        address = binding.detailAddress;
        fab = binding.detailFloatingButton;

        int spotId = getIntent().getIntExtra("spotId", -1);
        nearbySearchResult = service.getNearbySearchResults().get(spotId);
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + nearbySearchResult.getPlace_id() + "&fields=formatted_phone_number%2Cwebsite&key=" + MAPS_API_KEY;
        placeSearchExecutor(url);
        photo = PhotoRefToBitmap.getBitmap(nearbySearchResult.getPhoto_reference(), 800);

        image.setImageBitmap(photo);
        name.setText(nearbySearchResult.getName());
        address.setText(nearbySearchResult.getVicinity());
        fab.setOnClickListener(view -> {
            userManager.addWorkmate(nearbySearchResult.getPlace_id());
        });

        //workmateList observer
        userManager.getWorkmatesList(nearbySearchResult.getPlace_id());
        final Observer<ArrayList<User>> workmateListObserver = _workmateList -> {
            workmateList = _workmateList;
            //actualiser la rv avec les nouveaux workmates!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //nbr de workmates affiché!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            //recherche de restau!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        };
        userManager.getWorkmatesList(nearbySearchResult.getPlace_id()).observe(this, workmateListObserver);
    }

    private void placeSearchExecutor(String url){
        //execute query
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            if(nearbySearchResult.getPhone() == " "  && nearbySearchResult.getWebsite() == " ") {
                String urlRequestResult = UrlRequest.execute(url);
                JSONObject result = null;
                try {
                    //parse results to JSON + add result to searchResult
                    JSONObject resultObject = new JSONObject(urlRequestResult);
                    result = resultObject.getJSONObject("result");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String phone = result.getString("formatted_phone_number");
                    nearbySearchResult.setPhone(phone);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    String website = result.getString("website");
                    nearbySearchResult.setWebsite(website);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
