package com.example.newsfetcherapp.db;

import android.content.Context;
import android.content.SharedPreferences;

public class NewsPreferences {
    private static final String PREF_NAME = "NewsPrefs";
    private static final String KEY_LAST_HEADLINE = "lastHeadline";
    private SharedPreferences prefs;

    public NewsPreferences(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveLastHeadline(String headline) {
        prefs.edit().putString(KEY_LAST_HEADLINE, headline).apply();
    }

    public String getLastHeadline() {
        return prefs.getString(KEY_LAST_HEADLINE, "");
    }
}