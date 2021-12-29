package com.example.go4lunch.ui.repository;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.models.Restaurant;
import com.example.go4lunch.models.User;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class UserRepository {

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
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
    }

    public Task<Void> signOut(Context context){
        return AuthUI.getInstance().signOut(context);
    }

    // Create restaurant in Firestore if needed and add myself as workmate
    private User createWorkmate(String restaurantId, String restaurantName, String restAddress) {
        FirebaseUser user = getCurrentUser();
        if(user != null){
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String username = user.getDisplayName();
            String uid = user.getUid();
            return new User(uid, username, urlPicture, restaurantName, restaurantId, restAddress);
        } else{
            return null;
        }
    }

    public void addWorkmate(String restId, String restaurantName, String vicinity) {
        User userToCreate = createWorkmate(restId, restaurantName, vicinity);
        Map<String, Object> restaurant = new HashMap<>();
        restaurant.put("id", restId);
        restaurant.put("name", restaurantName);
        this.getRestaurantsCollection().document(restId).set(restaurant);
        assert userToCreate != null;
        this.getRestaurantsCollection().document(restId).collection("workmates").document(userToCreate.getUid()).set(userToCreate);
        Map<String, Object>user = new HashMap<>();
        user.put("rest_id", restId);
        user.put("user_id", userToCreate.getUid());
        user.put("rest_name", restaurantName);
        user.put("user_name", userToCreate.getUsername());
        user.put("url_picture", userToCreate.getUrlPicture());
        user.put("rest_address", vicinity);
        user.put("receive_notifications", "true");
        this.getUsersCollection().document(userToCreate.getUid()).set(user);
    }

    public void delWorkmate(String restId) {
        this.getRestaurantsCollection().document(restId).collection("workmates").document(Objects.requireNonNull(getCurrentUser()).getUid()).delete();
        this.getUsersCollection().document(getCurrentUser().getUid()).update("rest_id", "");
        this.getUsersCollection().document(getCurrentUser().getUid()).update("rest_name", "");
    }

    public void sendLike(String restId, String restaurantName) {
        this.getUsersCollection().document(Objects.requireNonNull(getCurrentUser()).getUid()).collection("likes").add(new Restaurant(restId, restaurantName));
    }

    // Get workmates in restaurant
    public MutableLiveData<ArrayList<User>> getWorkmatesList(String restId, String restName, String restAddress){
        if(restId != null){
            MutableLiveData<ArrayList<User>> mutableLiveDataWorkmateList = new MutableLiveData<>();
            this.getRestaurantsCollection().document(restId).collection("workmates").get().addOnSuccessListener(queryDocumentSnapshots -> {
                ArrayList<User> workmateList = new ArrayList<>();
                for (DocumentSnapshot workMate: queryDocumentSnapshots) {
                    String uid = Objects.requireNonNull(workMate.get("uid")).toString();
                    String userName = Objects.requireNonNull(workMate.get("username")).toString();
                    Object urlPictureObject = workMate.get("urlPicture");
                    String urlPicture = urlPictureObject != null ? urlPictureObject.toString() : null;
                    workmateList.add(new User(uid, userName, urlPicture, restName, restAddress, restId));
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
        MutableLiveData<String[]> mutableLiveDataWorkmateList = new MutableLiveData<>();
        this.getUsersCollection().get().addOnSuccessListener(usersListSnapshots -> {
            List<DocumentSnapshot> queryDocumentSnapshots = usersListSnapshots.getDocuments();
            for (DocumentSnapshot users: queryDocumentSnapshots) {
                String restId =  Objects.requireNonNull(users.get("rest_id")).toString();
                String restName =  Objects.requireNonNull(users.get("rest_name")).toString();
                String userId = Objects.requireNonNull(users.get("user_id")).toString();
                String userName = Objects.requireNonNull(users.get("user_name")).toString();
                Object urlPictureObject = users.get("url_picture");
                String urlPicture = urlPictureObject != null ? urlPictureObject.toString() : null;
                String restAddress = Objects.requireNonNull(users.get("rest_address")).toString();
                mutableLiveDataWorkmateList.setValue(new String[]{restId, restName, userId, userName, urlPicture, restAddress});
            }
        });
        return mutableLiveDataWorkmateList;
    }

    // Get receive_notifications
    public MutableLiveData<Boolean> getReceiveNotifications(){
        MutableLiveData<Boolean> mutableLiveDataWorkmateList = new MutableLiveData<>();
        this.getUsersCollection().get().addOnSuccessListener(usersListSnapshots -> {
            List<DocumentSnapshot> queryDocumentSnapshots = usersListSnapshots.getDocuments();
            String currentUserId = Objects.requireNonNull(getCurrentUser()).getUid();
            for (DocumentSnapshot users: queryDocumentSnapshots) {
                String userId = Objects.requireNonNull(users.get("user_id")).toString();
                String receiveNotifications = Objects.requireNonNull(users.get("receive_notifications")).toString();
                if(currentUserId.equals(userId)) {
                    mutableLiveDataWorkmateList.setValue(receiveNotifications.equals("true"));
                    break;
                }
            }
        });
        return mutableLiveDataWorkmateList;
    }

    // Set receive_notifications
    public void setReceiveNotifications(boolean receive) {
        DocumentReference docRef = this.getUsersCollection().document(Objects.requireNonNull(getCurrentUser()).getUid());
        docRef.update("receive_notifications", receive ? "true" : "false");
    }
}
