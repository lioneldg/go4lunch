package com.example.go4lunch.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
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
import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.example.go4lunch.ui.manager.UserManager;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements RestaurantsListAdapter.RecyclerViewClickListener, WorkmateListAdapter.RecyclerViewClickListener{
    private ActivityMainBinding binding;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MapFragment mapFragment;
    private SpotsFragment spotsFragment;
    private WorkmatesFragment workmatesFragment;
    private SearchView searchView;
    private DrawerLayout drawer;
    private UserManager userManager = UserManager.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fm = getSupportFragmentManager();
        mapFragment = new MapFragment();
        spotsFragment = new SpotsFragment(this);
        workmatesFragment = new WorkmatesFragment(this);

        //set search bar layout
        setSupportActionBar(binding.toolbar);
        //set drawer menu
        setDrawer();
        //set bottom bar
        setNavigationListener();
        userManager.signOut(getApplicationContext());
        signIn();
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

    private void changeFragment(Fragment frag){
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
    public void recyclerViewListClicked(int position) {
        Intent detailIntent = new Intent(MainActivity.this, SpotDetailActivity.class);
        detailIntent.putExtra("rvPosition", position);
        startActivity(detailIntent);
    }

    @Override
    public void recyclerViewListClicked(String spotId) {
        Intent detailIntent = new Intent(MainActivity.this, SpotDetailActivity.class);
        detailIntent.putExtra("spotId", spotId);
        startActivity(detailIntent);
    }
}