package com.example.newsfetcherapp.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsApiClient {
    private static final String API_URL = "https://newsapi.org/v2/top-headlines";
    private static final String API_KEY = "3636a4faeb0848759a010dd4581cbe19"; // Use sample key for exam

    public static String fetchNews() {
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();

        try {
            String urlString = String.format("%s?country=us&apiKey=%s", API_URL, API_KEY);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result.toString();
    }
}
