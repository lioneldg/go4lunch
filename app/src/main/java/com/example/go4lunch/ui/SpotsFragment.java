package com.example.go4lunch.ui;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.go4lunch.databinding.FragmentSpotsBinding;


public class SpotsFragment extends Fragment {
    private FragmentSpotsBinding binding;
    private RecyclerView rv;
    private MainActivity mainActivity;

    public SpotsFragment(MainActivity ma) {
        mainActivity = ma;

    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSpotsBinding.inflate(inflater, container, false);

        rv = binding.list;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        rv.setAdapter(new RestaurantsListAdapter(getContext(), mainActivity));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
