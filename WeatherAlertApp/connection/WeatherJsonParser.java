package com.example.weatheralertapp.connection;

import com.example.weatheralertapp.model.WeatherData;

import org.json.JSONException;
import org.json.JSONObject;

public class WeatherJsonParser {
    public static WeatherData parseWeatherData(String jsonString) {
        try {
            JSONObject json = new JSONObject(jsonString);
            JSONObject main = json.getJSONObject("main");
            JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
            JSONObject wind = json.getJSONObject("wind");

            double temp = main.getDouble("temp");
            String description = weather.getString("description");
            double windSpeed = wind.getDouble("speed");
            int humidity = main.getInt("humidity");

            return new WeatherData(temp, description, windSpeed, humidity);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
