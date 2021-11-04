package com.example.go4lunch.ui.manager;

import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.go4lunch.models.User;
import com.example.go4lunch.ui.repository.UserRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class UserManager extends ViewModel {

    private static volatile UserManager instance;
    private  UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Intent signInIntent() {
        return userRepository.signInIntent();
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return userRepository.deleteUser(context);
    }

    public void addWorkmate(String restId, String name) { userRepository.addWorkmate(restId, name); }

    public void delWorkmate(String restId) { userRepository.delWorkmate(restId); }

    public void sendLike(String restId, String restaurantName) {userRepository.sendLike(restId, restaurantName);}

    public MutableLiveData<ArrayList<User>> getWorkmatesList(String restId, String restName) { return userRepository.getWorkmatesList(restId, restName); }

    public MutableLiveData<String[]> getWorkmatesListEveryWhere() { return userRepository.getWorkmatesListEveryWhere(); }

}
