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

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.databinding.FragmentWorkmatesBinding;
import com.example.go4lunch.models.User;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.example.go4lunch.ui.manager.UserManager;

import java.util.ArrayList;

public class WorkmatesFragment extends Fragment {
    private FragmentWorkmatesBinding binding;
    private UserManager userManager = UserManager.getInstance();
    private RecyclerView rv;
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        rv = binding.list;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        View view = binding.getRoot();
        rv.setAdapter(new WorkmateListAdapter(service.getWorkmatesList(), getContext(), true, (WorkmateListAdapter.ClickListener) getActivity()));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        setAllWorkmates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
