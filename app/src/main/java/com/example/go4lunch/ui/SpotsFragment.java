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
    private final InterfaceSearchResultApiService service = DI.getSearchResultApiService();
    private ImageView imageTest;
    //FAIRE UN RV ET INTEGRER LES INFOS ET IMAGES POUR CHAQUE CELLULES!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = SpotsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        List<NearbySearchResult> nearbySearchResultList = service.getNearbySearchResults();
        imageTest = binding.imageTest;
        for(int i = 0; nearbySearchResultList.size() > i; i++){
            String photoRef = nearbySearchResultList.get(i).getPhoto_reference();
            Bitmap bmp = placeSearchImage(photoRef);
            if(bmp != null){
                imageTest.setImageBitmap(bmp);
            }
        }

        return view;
    }

    private Bitmap placeSearchImage(String photoRef){
        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?photo_reference=" + photoRef
                + "&maxwidth=400"
                + "&key=" + MAPS_API_KEY;
        return getPlaceImageExecutor(photoUrl);
    }

    private Bitmap getPlaceImageExecutor(String url){
        //execute query
        Bitmap result = null;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Bitmap> future = executor.submit(() -> {
            try {
                URL requestUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream in = new URL(url).openStream();
                    return BitmapFactory.decodeStream(in);
                }
                else {
                    Log.i("test", "Unsuccessful HTTP Response Code: " + responseCode);
                }
            } catch (MalformedURLException e) {
                Log.e("test", "Error processing Places API URL", e);
            } catch (IOException e) {
                Log.e("test", "Error connecting to Places API", e);
            }
            return null;
        });
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
