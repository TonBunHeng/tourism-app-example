package com.example.smarttourismapp;

import android.app.Application;
import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.smarttourismapp.utils.AppPreference;
import com.example.smarttourismapp.utils.LocaleHelper;

public class TourismApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        AppPreference preference = new AppPreference(this);
        // Apply persisted dark mode preference
        if (preference.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.applyLocale(base));
    }
}
