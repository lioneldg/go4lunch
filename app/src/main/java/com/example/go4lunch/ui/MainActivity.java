package com.example.go4lunch.ui;

import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.DI.DI;
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.models.DetailSearchResult;
import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.tools.PhotoRefToBitmap;
import com.example.go4lunch.tools.UrlRequest;
import com.example.go4lunch.ui.manager.UserManager;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements RestaurantsListAdapter.ClickListener, WorkmateListAdapter.ClickListener, MapFragment.ClickListener{
    private ActivityMainBinding binding;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MapFragment mapFragment;
    private SpotsFragment spotsFragment;
    private WorkmatesFragment workmatesFragment;
    private SearchView searchView;
    private DrawerLayout drawer;
    private UserManager userManager = UserManager.getInstance();
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fm = getSupportFragmentManager();
        mapFragment = new MapFragment();
        spotsFragment = new SpotsFragment();
        workmatesFragment = new WorkmatesFragment();

        //set search bar layout
        setSupportActionBar(binding.toolbar);
        //set drawer menu
        setDrawer();
        //set bottom bar
        setNavigationListener();
        userManager.signOut(getApplicationContext());
        signIn();
        setAllWorkmates();
    }

    private void setDrawer() {
        drawer = binding.drawer;
        ActionBarDrawerToggle drawerToggle =  new ActionBarDrawerToggle(this, drawer, R.string.open, R.string.close);
        drawer.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MenuItem yourLunch = binding.navigationView.getMenu().findItem(R.id.your_lunch);
        MenuItem settings = binding.navigationView.getMenu().findItem(R.id.settings);
        MenuItem logout = binding.navigationView.getMenu().findItem(R.id.logout);
        yourLunch.setOnMenuItemClickListener(menuItem -> {
                    return false;
                }
        );

        settings.setOnMenuItemClickListener(menuItem -> {
                    return false;
                }
        );

        logout.setOnMenuItemClickListener(menuItem -> {
                   userManager.signOut(getApplicationContext()).addOnSuccessListener(task -> Snackbar.make(binding.getRoot(), "DÉCONNECTÉ", Snackbar.LENGTH_SHORT).show());
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
        searchView.setQueryHint("Search restaurants");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if(s.length() > 1) {
                    autocompleteExecutor(s);//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                }
                ArrayList<Restaurant> listTest = service.getAutoCompleteList();
                return false;
            }
        });
        return true;
    }

    //bottomBar listener
    private void setNavigationListener(){
        binding.bottomBar.setOnItemSelectedListener(item -> bottomBarChangeView(item.getItemId()));
    }

    private Boolean bottomBarChangeView(Integer integer){
        switch (integer) {
            case R.id.action_map:
                setTitle("I'm Hungry!");
                changeFragment(mapFragment);
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.action_spots_list:
                setTitle("I'm Hungry!");
                changeFragment(spotsFragment);
                searchView.setVisibility(View.VISIBLE);
                break;
            case R.id.action_workmates_list:
                changeFragment(workmatesFragment);
                searchView.setVisibility(View.INVISIBLE);
                setTitle("Available workmates");
        }
        return true;
    }

    protected void changeFragment(Fragment frag){
        ft = fm.beginTransaction();
        ft.replace(binding.mainFrameLayout.getId(),frag);
        ft.commit();
    }

    private void signIn() {
        registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> onSignInResult(result)
        ).launch(userManager.signInIntent());
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Snackbar.make(binding.getRoot(), "CONNECTÉ", Snackbar.LENGTH_SHORT).show();
            changeFragment(mapFragment);
            updateUIWithUserData(user);
        } else {
            Snackbar.make(binding.getRoot(), "NON CONNECTÉ", Snackbar.LENGTH_SHORT).show();
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
        String email = TextUtils.isEmpty(user.getEmail()) ? "No mail found" : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? "No username found" : user.getDisplayName();

        //Update views with data
        TextView title = binding.navigationView.getHeaderView(0).findViewById(R.id.firstAndLastName);
        title.setText(username);

        TextView emailTV = binding.navigationView.getHeaderView(0).findViewById(R.id.email);
        emailTV.setText(email);
    }

    @Override
    public void listClicked(String spotId) {
        placeSearchExecutor(spotId);

    }

    private void placeSearchExecutor(String spotId){
        String url = "https://maps.googleapis.com/maps/api/place/details/json?place_id=" + spotId + "&fields=name%2Crating%2Cformatted_phone_number%2Cphoto%2Cvicinity%2Cwebsite&key=" + MAPS_API_KEY;
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

            try {
                String _phone = result.getString("formatted_phone_number");
                String _website = result.getString("website");
                String _photoRef = result.getJSONArray("photos").getJSONObject(0).getString("photo_reference");
                String _name = result.getString("name");
                String _vicinity = result.getString("vicinity");
                double _rating = result.getDouble("rating");
                service.setDetailSearchResult(new DetailSearchResult(spotId, _name, _photoRef, _rating, _vicinity, _website, _phone));
                Intent detailIntent = new Intent(MainActivity.this, SpotDetailActivity.class);
                startActivity(detailIntent);
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

    private void autocompleteExecutor(String input){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        service.clearAutoCompleteList();
        Location lastKnowLocation = service.getLastKnowLocation();
        if(!input.equals("") && input != null && lastKnowLocation != null) {
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
                JSONArray predictions = null;
                try {
                    //parse results to JSON + add result to searchResult
                    JSONObject predictionsObject = new JSONObject(urlRequestResult);
                    predictions = predictionsObject.getJSONArray("predictions");
                    for(int i = 0; i < predictions.length(); i++){
                        boolean isRestaurant = false;
                        String name;
                        String placeId;
                        JSONObject currentPredictions = (JSONObject) predictions.get(i);
                        JSONArray currentTypesArray = currentPredictions.getJSONArray("types");
                        for(int j = 0; j < currentTypesArray.length(); j++){
                            if(currentTypesArray.get(j).equals("restaurant")){
                                isRestaurant = true;
                                name = currentPredictions.getString("description");
                                placeId = currentPredictions.getString("place_id");
                                service.addAutoCompleteList(new Restaurant(placeId, name));
                            }
                        }
                    }
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