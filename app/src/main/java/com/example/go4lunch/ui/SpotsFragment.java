package com.example.go4lunch.ui;
import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.go4lunch.DI.DI;
import com.example.go4lunch.databinding.SpotsFragmentBinding;
import com.example.go4lunch.models.NearbySearchResult;
import com.example.go4lunch.service.InterfaceSearchResultApiService;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SpotsFragment extends Fragment {
    private SpotsFragmentBinding binding;
    private RecyclerView rv;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SpotsFragmentBinding.inflate(inflater, container, false);

        rv = binding.list;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(new RvAdapter());

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
