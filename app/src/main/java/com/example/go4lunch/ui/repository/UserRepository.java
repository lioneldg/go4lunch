package com.example.go4lunch.ui.repository;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.ui.WorkmateListAdapter;
import com.example.go4lunch.ui.WorkmatesFragment;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UserRepository {

    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private static volatile UserRepository instance;

    private UserRepository() { }

    // Get the Collection Reference
    private CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection("restaurants");
    }
    private CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection("users");
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
    private User createWorkmate(String restaurantId, String restaurantName) {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            return new User(uid, username, urlPicture, restaurantName, restaurantId);
        } else{
            return null;
        }
    }

    public void addWorkmate(String restId, String restaurantName) {
        User userToCreate = createWorkmate(restId, restaurantName);
        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put("id", restId);
        restaurant.put("name", restaurantName);
        this.getRestaurantsCollection().document(restId).set(restaurant);
        this.getRestaurantsCollection().document(restId).collection("workmates").document(userToCreate.getUid()).set(userToCreate);
        Map<String, Object>user = new HashMap<>();
        user.put("rest_id", restId);
        user.put("user_id", userToCreate.getUid());
        user.put("rest_name", restaurantName);
        user.put("user_name", userToCreate.getUsername());
        user.put("url_picture", userToCreate.getUrlPicture());
        this.getUsersCollection().document(userToCreate.getUid()).set(user);
    }

    public void delWorkmate(String restId) {
        this.getRestaurantsCollection().document(restId).collection("workmates").document(getCurrentUser().getUid()).delete();
        this.getUsersCollection().document(getCurrentUser().getUid()).update("rest_id", "");
        this.getUsersCollection().document(getCurrentUser().getUid()).update("rest_name", "");
    }

    // Get restaurant Data from Firestore
    private Task<DocumentSnapshot> getRestaurantData(String restId){
        if(restId != null){
            return this.getRestaurantsCollection().document(restId).get();
        }else{
            return null;
        }
    }

    // Get restaurant Data from Firestore
    private Task<DocumentSnapshot> getUserData(String userId){
        if(userId != null){
            return this.getUsersCollection().document(userId).get();
        }else{
            return null;
        }
    }

    // Get workmates in restaurant
    public MutableLiveData<ArrayList<User>> getWorkmatesList(String restId, String restName){
        if(restId != null){
            MutableLiveData<ArrayList<User>> mutableLiveDataWorkmateList = new MutableLiveData();
            this.getRestaurantsCollection().document(restId).collection("workmates").get().addOnSuccessListener(queryDocumentSnapshots -> {
                ArrayList<User> workmateList = new ArrayList<User>();
                for (DocumentSnapshot workMate: queryDocumentSnapshots) {
                    String uid = Objects.requireNonNull(workMate.get("uid")).toString();
                    String userName = Objects.requireNonNull(workMate.get("username")).toString();
                    String urlPicture = Objects.requireNonNull(workMate.get("urlPicture")).toString();
                    workmateList.add(new User(uid, userName, urlPicture, restName, restId));
                }
                mutableLiveDataWorkmateList.setValue(workmateList);
            });
            return mutableLiveDataWorkmateList;
        }else{
            return null;
        }
    }

    // Get workmates in restaurant
    public MutableLiveData<String[]> getWorkmatesListEveryWhere(){
        MutableLiveData<String[]> mutableLiveDataWorkmateList = new MutableLiveData();
        this.getUsersCollection().get().addOnSuccessListener(usersListSnapshots -> {
            List<DocumentSnapshot> queryDocumentSnapshots = usersListSnapshots.getDocuments();
            for (DocumentSnapshot users: queryDocumentSnapshots) {
                String restId =  Objects.requireNonNull(users.get("rest_id")).toString();
                String restName =  Objects.requireNonNull(users.get("rest_name")).toString();
                String userId = Objects.requireNonNull(users.get("user_id")).toString();
                String userName = Objects.requireNonNull(users.get("user_name")).toString();
                String urlPicture = Objects.requireNonNull(users.get("url_picture")).toString();
                mutableLiveDataWorkmateList.setValue(new String[]{restId, restName, userId, userName, urlPicture});
            }
        });
        return mutableLiveDataWorkmateList;
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
