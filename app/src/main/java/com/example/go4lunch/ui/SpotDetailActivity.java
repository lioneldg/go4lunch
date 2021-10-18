package com.example.go4lunch.ui;
import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.databinding.ActivitySpotDetailBinding;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PhotoRefToBitmap;
import com.example.go4lunch.tools.UrlRequest;
import com.example.go4lunch.ui.manager.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class SpotDetailActivity extends AppCompatActivity {
    private ActivitySpotDetailBinding binding;
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private NearbySearchResult nearbySearchResult;
    private Bitmap photo;
    private ImageView image;
    private TextView name;
    private TextView address;
    private TextView star1;
    private TextView star2;
    private TextView star3;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private UserManager userManager = UserManager.getInstance();
    private String spotId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySpotDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        image = binding.detailImage;
        name = binding.detailName;
        address = binding.detailAddress;
        star1 = binding.star1;
        star2 = binding.star2;
        star3 = binding.star3;
        fab = binding.detailFloatingButton;
        recyclerView = binding.list;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int rvPosition = getIntent().getIntExtra("rvPosition", -1);
        spotId = getIntent().getStringExtra("spotId");

        if(rvPosition == -1) {
            nearbySearchResult = new NearbySearchResult();
            String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + spotId + "&fields=name%2Crating%2Cformatted_phone_number%2Cphoto%2Cvicinity%2Cwebsite&key=" + MAPS_API_KEY;
            placeSearchExecutor(url);
        } else {
            nearbySearchResult = service.getNearbySearchResults().get(rvPosition);
            String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + nearbySearchResult.getPlace_id() + "&fields=name%2Crating%2Cformatted_phone_number%2Cphoto%2Cvicinity%2Cwebsite&key=" + MAPS_API_KEY;
            placeSearchExecutor(url);
            photo = PhotoRefToBitmap.getBitmap(nearbySearchResult.getPhoto_reference(), 800);

            image.setImageBitmap(photo);
            name.setText(nearbySearchResult.getName());
            address.setText(nearbySearchResult.getVicinity());

            //workmateList observer
            final Observer<ArrayList<User>> workmateListObserver = workmateList -> {
                recyclerView.setAdapter(new WorkmateListAdapter(workmateList, getApplicationContext(), false));
            };
            userManager.getWorkmatesList(nearbySearchResult.getPlace_id(), nearbySearchResult.getName()).observe(this, workmateListObserver);

            fab.setOnClickListener(view -> {
                userManager.addWorkmate(nearbySearchResult.getPlace_id(), nearbySearchResult.getName());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                userManager.getWorkmatesList(nearbySearchResult.getPlace_id(), nearbySearchResult.getName()).observe(this, workmateListObserver);
            });
        }
    }
//TODO sortir l'executor de là pour faire la requette avant d'afficher la vue. Faire la requette deans le main dans l'interface
    //voir si on peut mettre ca dans l'api service et model
    private void placeSearchExecutor(String url){
        //execute query
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String urlRequestResult = UrlRequest.execute(url);
            JSONObject result = null;
            try {
                //parse results to JSON + add result to searchResult
                JSONObject resultObject = new JSONObject(urlRequestResult);
                result = resultObject.getJSONObject("result");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(nearbySearchResult.getPhone().equals(" ") && nearbySearchResult.getWebsite().equals(" ")) {
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
            try {
                String _photoRef = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                String _name = result.getString("name");
                String _vicinity = result.getString("vicinity");
                double _rating = result.getDouble("rating");
                Bitmap _photo = PhotoRefToBitmap.getBitmap(_photoRef, 800);
                this.setUI(_name, _vicinity, _rating, _photo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setUI(String _name, String _vicinity, double _rating, Bitmap _photo){
        image.setImageBitmap(_photo);
        name.setText(_name);
        address.setText(_vicinity);
        int rating = (int) Math.round((_rating / 5) * 3);
        switch(rating){
            case 3: star3.setVisibility(View.VISIBLE);
                star1.setVisibility(View.VISIBLE);
                star1.setVisibility(View.VISIBLE);
                break;

            case 2: star3.setVisibility(View.INVISIBLE);
                star1.setVisibility(View.VISIBLE);
                star1.setVisibility(View.VISIBLE);
                break;

            case 1: star3.setVisibility(View.INVISIBLE);
                star2.setVisibility(View.INVISIBLE);
                star1.setVisibility(View.VISIBLE);
                break;

            default: star3.setVisibility(View.INVISIBLE);
                star2.setVisibility(View.INVISIBLE);
                star1.setVisibility(View.INVISIBLE);
        }
        //workmateList observer
        final Observer<ArrayList<User>> workmateListObserver = workmateList -> {
            recyclerView.setAdapter(new WorkmateListAdapter(workmateList, getApplicationContext(), false));
        };
        userManager.getWorkmatesList(spotId, _name).observe(this, workmateListObserver);

        fab.setOnClickListener(view -> {
            userManager.addWorkmate(spotId, _name);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userManager.getWorkmatesList(spotId, _name).observe(this, workmateListObserver);
        });
    }
}
