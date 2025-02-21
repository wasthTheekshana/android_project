package com.example.weatheralertapp.connection;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherApiClient {
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "4f5529cff052f5d7b85729298372bde4"; // You'll need to use a sample API key for exam

    public static String fetchWeatherData(String city) {
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();

        try {
            String urlString = String.format("%s?q=%s&appid=%s&units=metric",
                    API_BASE_URL, city, API_KEY);
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

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