package com.example.go4lunch;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

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

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private List<AuthUI.IdpConfig> providers;
    private Intent signInIntent;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private MapFragment mapFragment;
    private SpotsFragment spotsFragment;
    private WorkmatesFragment workmatesFragment;

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

        //providers
        providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),new AuthUI.IdpConfig.GoogleBuilder().build(),new AuthUI.IdpConfig.FacebookBuilder().build());
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
        ft = fm.beginTransaction();
        ft.add(binding.activityMain.getId(),mapFragment);
        ft.commit();
        this.setNavigationListener();
    }

    private void setNavigationListener(){
        binding.bottomBar.setOnItemSelectedListener(item -> changeView(item.getItemId()));
    }

    private Boolean changeView(Integer integer){
        ft = fm.beginTransaction();
        switch (integer) {
            case R.id.action_map:
                ft.replace(binding.activityMain.getId(),mapFragment);
                ft.commit();
                break;
            case R.id.action_spots_list:
                ft.replace(binding.activityMain.getId(),spotsFragment);
                ft.commit();
                break;
            case R.id.action_workmates_list:
                ft.replace(binding.activityMain.getId(),workmatesFragment);
                ft.commit();
        }
        return true;
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Snackbar.make(binding.getRoot(), "CONNECTÉ", Snackbar.LENGTH_SHORT).show();
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
    }

    /*@Override
    protected void onRestart() {
        super.onRestart();
        signOut();
        signInLauncher.launch(signInIntent);
    }*/
}