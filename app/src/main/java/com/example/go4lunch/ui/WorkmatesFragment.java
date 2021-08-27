package com.example.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.go4lunch.databinding.SpotsFragmentBinding;
import com.example.go4lunch.databinding.WorkmatesFragmentBinding;

public class WorkmatesFragment extends Fragment {
    private WorkmatesFragmentBinding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = WorkmatesFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
