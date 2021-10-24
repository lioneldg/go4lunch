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
import com.example.go4lunch.models.DetailSearchResult;
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
    private DetailSearchResult detailSearchResult;

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
        detailSearchResult = service.getDetailSearchResult();

        photo = PhotoRefToBitmap.getBitmap(detailSearchResult.getPhoto_reference(), 800);

        image.setImageBitmap(photo);
        name.setText(detailSearchResult.getName());
        address.setText(detailSearchResult.getVicinity());

            //workmateList observer
        final Observer<ArrayList<User>> workmateListObserver = workmateList -> {
            recyclerView.setAdapter(new WorkmateListAdapter(workmateList, getApplicationContext(), false));
        };
        userManager.getWorkmatesList(detailSearchResult.getPlace_id(), detailSearchResult.getName()).observe(this, workmateListObserver);

        fab.setOnClickListener(view -> {
            userManager.addWorkmate(detailSearchResult.getPlace_id(), detailSearchResult.getName());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userManager.getWorkmatesList(detailSearchResult.getPlace_id(), detailSearchResult.getName()).observe(this, workmateListObserver);
        });
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
        userManager.getWorkmatesList(detailSearchResult.getPlace_id(), _name).observe(this, workmateListObserver);

        fab.setOnClickListener(view -> {
            userManager.addWorkmate(detailSearchResult.getPlace_id(), _name);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userManager.getWorkmatesList(detailSearchResult.getPlace_id(), _name).observe(this, workmateListObserver);
        });
    }
}
