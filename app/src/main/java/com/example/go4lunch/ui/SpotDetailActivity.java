package com.example.go4lunch.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.di.DI;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivitySpotDetailBinding;
import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PhotoRefToBitmap;
import com.example.go4lunch.ui.manager.UserManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class SpotDetailActivity extends AppCompatActivity {
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private final UserManager userManager = UserManager.getInstance();
    private DetailSearchResult detailSearchResult;
    boolean hasDecided = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.go4lunch.databinding.ActivitySpotDetailBinding binding = ActivitySpotDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageView image = binding.detailImage;
        TextView name = binding.detailName;
        TextView address = binding.detailAddress;
        TextView star1 = binding.star1;
        TextView star2 = binding.star2;
        TextView star3 = binding.star3;
        TextView call = binding.detailTextCall;
        TextView like = binding.detailTextLike;
        TextView website = binding.detailWebsite;
        fab = binding.detailFloatingButton;
        recyclerView = binding.list;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        detailSearchResult = service.getDetailSearchResult();

        Bitmap photo = PhotoRefToBitmap.getBitmap(detailSearchResult.getPhoto_reference(), 800);

        image.setImageBitmap(photo);
        name.setText(detailSearchResult.getName());
        address.setText(detailSearchResult.getVicinity());

        int rating = detailSearchResult.getRating();
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
            setAllWorkmates();
        });

        call.setOnClickListener(view->{
            if(!detailSearchResult.getPhone().equals("")) {
                Uri telephone = Uri.parse("tel:" + detailSearchResult.getPhone());
                startActivity(new Intent(Intent.ACTION_DIAL, telephone));
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.no_tel), Snackbar.LENGTH_SHORT).show();
            }
        });

        like.setOnClickListener(view -> {
            userManager.sendLike(detailSearchResult.getPlace_id(), detailSearchResult.getName());
            Snackbar.make(binding.getRoot(), getString(R.string.like_sent), Snackbar.LENGTH_SHORT).show();
        });

        website.setOnClickListener(view -> {
            if(!detailSearchResult.getWebsite().equals("")) {
                Uri web = Uri.parse(detailSearchResult.getWebsite());
                startActivity(new Intent(Intent.ACTION_VIEW, web));
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.no_web_site), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void setAllWorkmates(){
        service.clearWorkmatesList();
        final Observer<String[]> workmateListEveryWhereObserver = workmateListObserved -> {
            String restId = workmateListObserved[0];
            String restName = workmateListObserved[1];
            String userId = workmateListObserved[2];
            String userName = workmateListObserved[3];
            String urlPicture = workmateListObserved[4];
            if(!userId.equals("")) {
                service.addWorkmatesList(new User(userId, userName, urlPicture, restName, restId));
            }
        };

        userManager.getWorkmatesListEveryWhere().observe(this, workmateListEveryWhereObserver);
    }
}
