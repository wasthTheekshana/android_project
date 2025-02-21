package com.example.weatheralertapp.model;

public class WeatherData {
    private double temperature;
    private String description;
    private double windSpeed;
    private int humidity;

    // Constructor
    public WeatherData(double temperature, String description, double windSpeed, int humidity) {
        this.temperature = temperature;
        this.description = description;
        this.windSpeed = windSpeed;
        this.humidity = humidity;
    }

    // Getters and setters
    // ... (add standard getters/setters)


    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }
}
