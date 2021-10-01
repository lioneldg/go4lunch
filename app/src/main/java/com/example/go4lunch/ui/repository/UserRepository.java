package com.example.go4lunch.ui.repository;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class UserRepository {

    private static volatile UserRepository instance;

    private UserRepository() { }

    // Get the Collection Reference
    private CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection("restaurants");
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Intent signInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(), new AuthUI.IdpConfig.GoogleBuilder().build(), new AuthUI.IdpConfig.FacebookBuilder().build(), new AuthUI.IdpConfig.TwitterBuilder().build());
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        return signInIntent;
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return AuthUI.getInstance().delete(context);
    }

    // Create restaurant in Firestore if needed and add myself as workmate
    private User createWorkmate() {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();

            return new User(uid, username, urlPicture);
        } else{
            return null;
        }
    }

    public void addWorkmate(String restId) {
        User userToCreate = createWorkmate();
        Task<DocumentSnapshot> restaurantData = getRestaurantData(restId);
        restaurantData.addOnSuccessListener(documentSnapshot -> {
            this.getRestaurantsCollection().document(restId).collection("workmates").document(userToCreate.getUid()).set(userToCreate);
        });
    }

    // Get restaurant Data from Firestore
    private Task<DocumentSnapshot> getRestaurantData(String restId){
        if(restId != null){
            return this.getRestaurantsCollection().document(restId).get();
        }else{
            return null;
        }
    }

    // Get workmates in restaurant
    public MutableLiveData<ArrayList<User>> getWorkmatesList(String restId){
        if(restId != null){
            MutableLiveData<ArrayList<User>> mutableLiveDataWorkmateList = new MutableLiveData();
            Task<QuerySnapshot> workmatesData = this.getRestaurantsCollection().document(restId).collection("workmates").get();
            workmatesData.addOnSuccessListener(documentSnapshot -> {
                documentSnapshot.getDocuments();
            }).addOnSuccessListener(queryDocumentSnapshots -> {
                ArrayList<User> workmateList = new ArrayList<User>();
                for (DocumentSnapshot workMate: queryDocumentSnapshots) {
                    String uid = Objects.requireNonNull(workMate.get("uid")).toString();
                    String userName = Objects.requireNonNull(workMate.get("username")).toString();
                    String urlPicture = Objects.requireNonNull(workMate.get("urlPicture")).toString();
                    workmateList.add(new User(uid, userName, urlPicture));
                }
                mutableLiveDataWorkmateList.setValue(workmateList);
            });
            return mutableLiveDataWorkmateList;
        }else{
            return null;
        }
    }

    // Update User Username
    /*public Task<Void> updateUsername(String username) {
        String uid = getCurrentUser().getUid();
        if(uid != null){
            return this.getUsersCollection().document(uid).update(USERNAME_FIELD, username);
        }else{
            return null;
        }
    }*/

    // Delete the User from Firestore
    /*public void deleteUserFromFirestore() {
        String uid = getCurrentUser().getUid();
        if(uid != null){
            this.getUsersCollection().document(uid).delete();
        }
    }*/


}
