package com.example.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.models.User;
import com.example.go4lunch.ui.manager.UserManager;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment {
    private FragmentWorkmatesBinding binding;
    private UserManager userManager = UserManager.getInstance();
    private RecyclerView rv;
    private MainActivity mainActivity;

    public WorkmatesFragment(MainActivity ma) {
        mainActivity = ma;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        rv = binding.list;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        View view = binding.getRoot();

        ArrayList<User> workmateList = new ArrayList<>();

        final Observer<ArrayList<User>> workmateListObserver = workmateListObserved -> {
            String uid = workmateListObserved.get(0).getUid();
            String urlPicture = workmateListObserved.get(0).getUrlPicture();
            String userName = workmateListObserved.get(0).getUsername();
            String restaurantName = workmateListObserved.get(0).getRestaurantName();
            String restaurantId = workmateListObserved.get(0).getRestaurantId();
            workmateList.add(new User(uid, userName, urlPicture, restaurantName, restaurantId));
            rv.setAdapter(new WorkmateListAdapter(workmateList, getContext(), true, mainActivity));
        };

        final Observer<String[]> workmateListEveryWhereObserver = workmateListObserved -> {
            String restId = workmateListObserved[0];
            String restName = workmateListObserved[1];
            userManager.getWorkmatesList(restId, restName).observe(getViewLifecycleOwner(), workmateListObserver);
        };

        userManager.getWorkmatesListEveryWhere().observe(getViewLifecycleOwner(), workmateListEveryWhereObserver);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
