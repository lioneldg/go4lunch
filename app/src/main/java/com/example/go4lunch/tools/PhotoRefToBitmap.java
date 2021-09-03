package com.example.go4lunch.tools;

import static com.example.go4lunch.BuildConfig.MAPS_API_KEY;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PhotoRefToBitmap {

    public static Bitmap getBitmap(String photoRef, int maxWidth){
        return placeSearchImage(photoRef, maxWidth);
    }

    private static Bitmap placeSearchImage(String photoRef, int maxWidth){
        String photoUrl = "https://maps.googleapis.com/maps/api/place/photo?photo_reference=" + photoRef
                + "&maxwidth=" + maxWidth
                + "&key=" + MAPS_API_KEY;
        return getPlaceImageExecutor(photoUrl);
    }

    private static Bitmap getPlaceImageExecutor(String url){
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

}
