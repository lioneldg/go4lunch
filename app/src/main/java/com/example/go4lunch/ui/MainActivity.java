package com.example.go4lunch.ui;

import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.di.DI;
import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PlaceSearchHelper;
import com.example.go4lunch.tools.UrlRequest;
import com.example.go4lunch.ui.manager.UserManager;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RestaurantsListAdapter.ClickListener, WorkmateListAdapter.ClickListener, MapFragment.ClickListener{
    private ActivityMainBinding binding;
    private FragmentManager fm;
    private MapFragment mapFragment;
    private SpotsFragment spotsFragment;
    private WorkmatesFragment workmatesFragment;
    private SearchView searchView;
    private DrawerLayout drawer;
    private final UserManager userManager = UserManager.getInstance();
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private ActivityResultLauncher<Intent> activityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        FrameLayout mainFrameLayout = binding.mainFrameLayout;

        int orientation = getResources().getConfiguration().orientation;
        mainFrameLayout.setLayoutParams(new LinearLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, orientation == Configuration.ORIENTATION_LANDSCAPE ? 1.5f : 1.0f));
        setContentView(binding.getRoot());

        fm = getSupportFragmentManager();
        mapFragment = new MapFragment();
        spotsFragment = new SpotsFragment();
        workmatesFragment = new WorkmatesFragment();

        activityResultLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                this::onSignInResult
        );
        signIn();
    }

    private void setDrawer() {
        drawer = binding.drawer;
        ActionBarDrawerToggle drawerToggle =  new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        MenuItem yourLunch = binding.navigationView.getMenu().findItem(R.id.your_lunch);
        MenuItem settings = binding.navigationView.getMenu().findItem(R.id.settings);
        MenuItem logout = binding.navigationView.getMenu().findItem(R.id.logout);
        yourLunch.setOnMenuItemClickListener(menuItem -> {
            ArrayList<User> workmates = service.getWorkmatesList();
            for (int i = 0; i < workmates.size(); i++) {
                boolean isMe = workmates.get(i).getUid().equals(userManager.getCurrentUser().getUid());
                if(isMe){
                    String myRestId = workmates.get(i).getRestaurantId();
                    if(myRestId.equals("")){
                        Snackbar.make(binding.getRoot(), getString(R.string.hasn_t_decided_yet), Snackbar.LENGTH_SHORT).show();
                    } else {
                        placeSearchExecutor(myRestId, false, true);
                    }
                    break;
                }
            }
            return false;
        });

        settings.setOnMenuItemClickListener(menuItem -> {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
            return false;
        });

        logout.setOnMenuItemClickListener(menuItem -> {
                   userManager.signOut(getApplicationContext()).addOnSuccessListener(task -> signIn());
                    return false;
                }
        );
    }

    //menu button listener: open/close drawer
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        drawer.openDrawer(GravityCompat.START);
        return super.onOptionsItemSelected(item);
    }

    //SearchBar
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setQueryHint(getString(R.string.search_restaurant));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                mapFragment.clearMap();
                if(s.length() > 1) {
                    autocompleteExecutor(s);
                } else {
                    service.clearNearbySearchResult();
                    Location lastKnownLocation = service.getLastKnowLocation();
                    mapFragment.spotsFragment = spotsFragment;
                    mapFragment.placeSearchNearBy(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                }
                return false;
            }
        });
        return true;
    }

    //bottomBar listener
    private void setNavigationListener(){
        binding.bottomBar.setOnItemSelectedListener(item -> bottomBarChangeView(item.getItemId()));
    }

    @SuppressLint("NonConstantResourceId")
    private Boolean bottomBarChangeView(Integer integer){
        switch (integer) {
            case R.id.action_map:
                setTitle(getString(R.string.i_m_hungry));
                changeFragment(mapFragment);
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.action_spots_list:
                setTitle(getString(R.string.i_m_hungry));
                changeFragment(spotsFragment);
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.action_workmates_list:
                changeFragment(workmatesFragment);
                searchView.setVisibility(View.INVISIBLE);
                setTitle(getString(R.string.available_workmates));
        }
        return true;
    }

    protected void changeFragment(Fragment frag){
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(binding.mainFrameLayout.getId(),frag);
        ft.commit();
    }

    private void signIn() {
        if(!userManager.isCurrentUserLogged()){
            activityResultLauncher.launch(userManager.signInIntent());
        } else {
            //set search bar layout
            setSupportActionBar(binding.toolbar);
            //set drawer menu
            setDrawer();
            //set bottom bar
            setNavigationListener();
            setAllWorkmates();
            getReceiveNotifications();
        }
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            //set search bar layout
            setSupportActionBar(binding.toolbar);
            //set drawer menu
            setDrawer();
            //set bottom bar
            setNavigationListener();
            setAllWorkmates();
            getReceiveNotifications();
        } else {
            signIn();
        }
    }

    private void setProfilePicture(Uri profilePictureUrl){
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into((ImageView) binding.navigationView.getHeaderView(0).findViewById(R.id.profilePicture));
    }

    private void updateUIWithUserData(FirebaseUser user){
        if(userManager.isCurrentUserLogged()){
            if(user.getPhotoUrl() != null){
                setProfilePicture(user.getPhotoUrl());
            }
            setTextUserData(user);
        }
    }

    private void setTextUserData(FirebaseUser user){

        //Get email & username from User
        String email = TextUtils.isEmpty(user.getEmail()) ? "" : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? "" : user.getDisplayName();

        //Update views with data
        TextView title = binding.navigationView.getHeaderView(0).findViewById(R.id.firstAndLastName);
        title.setText(username);

        TextView emailTV = binding.navigationView.getHeaderView(0).findViewById(R.id.email);
        emailTV.setText(email);
    }

    @Override
    public void listClicked(String spotId) {
        if(!spotId.equals("")){
            placeSearchExecutor(spotId, false, true);
        }
    }

    private void placeSearchExecutor(String spotId, boolean isFromAutoComplete, boolean isSearchFinished){
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + spotId + "&fields=name%2Crating%2Cformatted_phone_number%2Cphoto%2Cvicinity%2Cwebsite%2Cgeometry%2Copening_hours&key=" + MAPS_API_KEY;
        //execute query
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            String urlRequestResult = UrlRequest.execute(url);
            try {
                Restaurant spot = PlaceSearchHelper.placeResultToRestaurant(urlRequestResult, spotId);
                if(isFromAutoComplete){
                    Location lastKnownLocation = service.getLastKnowLocation();
                    float[] distanceBetweenArray = new float[1];
                    Location.distanceBetween(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), spot.getLat(), spot.getLng(), distanceBetweenArray);
                    int distanceBetween = Math.round(distanceBetweenArray[0]);
                    if(distanceBetween <= 2000) {
                        NearbySearchResult nearbySearchResult = new NearbySearchResult();
                        nearbySearchResult.setName(spot.getName());
                        nearbySearchResult.setVicinity(spot.getVicinity());
                        nearbySearchResult.setPlace_id(spotId);
                        nearbySearchResult.setRating(spot.getRating());
                        nearbySearchResult.setLat(spot.getLat());
                        nearbySearchResult.setLng(spot.getLng());
                        nearbySearchResult.setOpen_now(spot.getOpen_now());
                        nearbySearchResult.setPhoto_reference(spot.getPhoto_reference());
                        nearbySearchResult.setDistanceBetween(distanceBetween);
                        service.addNearbySearchResult(nearbySearchResult);
                    }
                } else {
                    service.setDetailSearchResult(new DetailSearchResult(spotId, spot.getName(), spot.getPhoto_reference(), spot.getRating(), spot.getVicinity(), spot.getWebsite(), spot.getPhone()));
                    Intent detailIntent = new Intent(MainActivity.this, SpotDetailActivity.class);
                    startActivity(detailIntent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        try {
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            if(isSearchFinished && isFromAutoComplete && mapFragment.isVisible()){
                for(int i = 0; service.getNearbySearchResults().size() > i; i++){
                    NearbySearchResult nearbySearchResult = service.getNearbySearchResults().get(i);
                    LatLng position = new LatLng(nearbySearchResult.getLat(), nearbySearchResult.getLng());
                    mapFragment.addMarkerOption(position, nearbySearchResult.getName(), nearbySearchResult.getPlace_id());
                }
            }
            if(isSearchFinished && spotsFragment.isVisible()) {
                spotsFragment.onDataSetChange();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void autocompleteExecutor(String input){
        service.clearAutoCompleteList();
        service.clearNearbySearchResult();
        Location lastKnowLocation = service.getLastKnowLocation();
        if(!input.equals("") && lastKnowLocation != null) {
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + input
                    + "&location="
                    + lastKnowLocation.getLatitude()
                    + "%2C"
                    + lastKnowLocation.getLongitude()
                    + "&radius=2000&types=establishment&key="
                    + MAPS_API_KEY;

            //execute query
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                String urlRequestResult = UrlRequest.execute(url);
                ArrayList<Restaurant> restaurantList = PlaceSearchHelper.placeResultToPredictions(urlRequestResult);
                for(int i = 0; i < restaurantList.size(); i++){
                    service.addAutoCompleteList(restaurantList.get(i));
                }
            });
            try {
                executor.shutdown();
                executor.awaitTermination(1, TimeUnit.SECONDS);
                //now autocompleteList contains all Restaurants of filter with no distance limitation
                //we need to search details for each with placeSearchExecutor function
                //and put each result in NearBySearchResult list with 2000m distance limitation
                ArrayList<Restaurant> autoCompleteList = service.getAutoCompleteList();
                for(int i = 0; i <autoCompleteList.size(); i++){
                    String placeId = autoCompleteList.get(i).getId();
                    boolean isSearchFinished = i+1 == autoCompleteList.size();
                    placeSearchExecutor(placeId, true, isSearchFinished);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setAllWorkmates(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        service.clearWorkmatesList();
        final Observer<String[]> workmateListEveryWhereObserver = workmateListObserved -> {
            String restId = workmateListObserved[0];
            String restName = workmateListObserved[1];
            String userId = workmateListObserved[2];
            String userName = workmateListObserved[3];
            String urlPicture = workmateListObserved[4];
            String restAddress = workmateListObserved[5];
            if(!userId.equals("")) {
                service.addWorkmatesList(new User(userId, userName, urlPicture, restName, restAddress, restId));
                updateUIWithUserData(user);
                changeFragment(mapFragment);
            }
        };

        userManager.getWorkmatesListEveryWhere().observe(this, workmateListEveryWhereObserver);
    }

    private void getReceiveNotifications() {
        final Observer<Boolean> getReceiveNotificationsObserver = service::setReceiveNotifications;
        userManager.getReceiveNotifications().observe(this, getReceiveNotificationsObserver);
    }
}