package com.example.go4lunch.tools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlRequest {

    public static String execute(String url) {
        StringBuilder placesBuilder = new StringBuilder();
        try {

            URL requestUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                if (inputStream != null) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        placesBuilder.append(line).append("\n");
                    }
                }
            } else {
                Log.i("test", "Unsuccessful HTTP Response Code: " + responseCode);
            }
        } catch (
                MalformedURLException e) {
            Log.e("test", "Error processing Places API URL", e);
        } catch (
                IOException e) {
            Log.e("test", "Error connecting to Places API", e);
        }
        return placesBuilder.toString();
    }
}
