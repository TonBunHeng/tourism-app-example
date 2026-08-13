package com.example.smarttourismapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
public class AppPreference {
    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_FIRST_TIME = "is_first_time_launch";
    private final SharedPreferences preferences;

    public AppPreference(Context context){
        preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean isFirstTime(){
        return preferences.getBoolean(KEY_FIRST_TIME, true);
    }

    public void setKeyFirstTimeLaunch(boolean isFirstTime){
        preferences.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).apply();
    }
}
