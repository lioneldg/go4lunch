package com.example.go4lunch.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.example.go4lunch.R;
import com.example.go4lunch.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    private List<AuthUI.IdpConfig> providers;
    private Intent signInIntent;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MapFragment mapFragment;
    private SpotsFragment spotsFragment;
    private WorkmatesFragment workmatesFragment;
    private SearchView searchView;
    private DrawerLayout drawer;

    //connection
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> onSignInResult(result)
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        //drawer
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
                signOut();
                return false;
            }
        );

        //providers
        providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.GoogleBuilder().build(),new AuthUI.IdpConfig.FacebookBuilder().build(), new AuthUI.IdpConfig.TwitterBuilder().build());

        //signIn intent
        signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        //lunch sign in
        signInLauncher.launch(signInIntent);

        mapFragment = new MapFragment();
        spotsFragment = new SpotsFragment();
        workmatesFragment = new WorkmatesFragment();

        fm = getSupportFragmentManager();

        this.setNavigationListener();
    }

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

    private void setNavigationListener(){
        binding.bottomBar.setOnItemSelectedListener(item -> bottomBarChangeView(item.getItemId()));
    }

    private void changeFragment(Fragment frag){
        ft = fm.beginTransaction();
        ft.replace(binding.mainFrameLayout.getId(),frag);
        ft.commit();
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

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Snackbar.make(binding.getRoot(), "CONNECTÉ", Snackbar.LENGTH_SHORT).show();
            changeFragment(mapFragment);
        } else {
            Snackbar.make(binding.getRoot(), "NON CONNECTÉ", Snackbar.LENGTH_SHORT).show();
        }
    }

    //LOGOUT
    private void signOut(){
        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Snackbar.make(binding.getRoot(), "DÉCONNECTÉ", Snackbar.LENGTH_SHORT).show();
                    }
                });
        signInLauncher.launch(signInIntent);
    }
}