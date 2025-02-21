package com.example.weatheralertapp.db;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "WeatherPrefs";
    private static final String KEY_CITY = "saved_city";
    private static final String KEY_NOTIFICATION_ENABLED = "notifications_enabled";

    private SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveCity(String city) {
        prefs.edit().putString(KEY_CITY, city).apply();
    }

    public String getSavedCity() {
        return prefs.getString(KEY_CITY, "London"); // Default city
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_NOTIFICATION_ENABLED, enabled).apply();
    }

    public boolean areNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATION_ENABLED, true);
    }
}