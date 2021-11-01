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
import com.example.go4lunch.R;
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
    boolean hasDecided = false;

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

        int rating = (int) Math.round((detailSearchResult.getRating() / 5) * 3);
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
            for (int i = 0; i < workmateList.size(); i++) {
                if(workmateList.get(i).getUid().equals(userManager.getCurrentUser().getUid())){
                    hasDecided = true;
                }
            }
            fab.setImageResource(hasDecided ? R.drawable.ic_baseline_clear_24 : R.drawable.ic_baseline_check_24);
            recyclerView.setAdapter(new WorkmateListAdapter(workmateList, getApplicationContext(), false));
        };
        userManager.getWorkmatesList(detailSearchResult.getPlace_id(), detailSearchResult.getName()).observe(this, workmateListObserver);

        fab.setOnClickListener(view -> {
            ArrayList<User> workmates = service.getWorkmatesList();
            if(hasDecided){
                userManager.delWorkmate(detailSearchResult.getPlace_id());
                for (int i = 0; i < workmates.size(); i++) {
                    if(workmates.get(i).getUid().equals(userManager.getCurrentUser().getUid())){
                        //update myself in Array at service
                        workmates.get(i).setRestaurantId("");
                        workmates.get(i).setRestaurantName("");
                        break;
                    }
                }
                fab.setImageResource(R.drawable.ic_baseline_check_24);
                hasDecided=false;
            }else {
                for (int i = 0; i < workmates.size(); i++) {
                    //remove myself from the other restaurant if restaurant Id == an other restaurant
                    boolean isMe = workmates.get(i).getUid().equals(userManager.getCurrentUser().getUid());
                    String myLastRestId = workmates.get(i).getRestaurantId();
                    if(isMe && !myLastRestId.equals("") && !myLastRestId.equals(detailSearchResult.getPlace_id())){
                        userManager.delWorkmate(myLastRestId);
                        //update myself in Array at service
                        workmates.get(i).setRestaurantId(detailSearchResult.getPlace_id());
                        workmates.get(i).setRestaurantName(detailSearchResult.getName());
                        break;
                    }
                }
                userManager.addWorkmate(detailSearchResult.getPlace_id(), detailSearchResult.getName());
                fab.setImageResource(R.drawable.ic_baseline_clear_24);
                hasDecided=true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            userManager.getWorkmatesList(detailSearchResult.getPlace_id(), detailSearchResult.getName()).observe(this, workmateListObserver);
        });
    }
}
