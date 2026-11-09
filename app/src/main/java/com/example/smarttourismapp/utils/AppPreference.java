package com.example.smarttourismapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.example.smarttourismapp.model.User;
import com.google.gson.Gson;

public class AppPreference {
    private static final String TAG = "AppPreference";
    private static final String PREF_NAME = "tourism_app_prefs";
    private static final String SECURE_PREF_NAME = "tourism_app_secure_prefs";

    private static final String KEY_FIRST_TIME = "is_first_time_launch";
    private static final String KEY_AUTH_TOKEN = "auth_token";
    private static final String KEY_USER_DATA = "user_data";
    private static final String KEY_REMEMBER_ME = "remember_me";
    private static final String KEY_SAVED_EMAIL = "saved_email";
    private static final String KEY_BASE_URL = "custom_base_url";
    private static final String KEY_LANGUAGE = "app_language";
    private static final String KEY_DARK_MODE = "app_dark_mode";
    private static final String KEY_SELECTED_BUSINESS = "selected_business_data";

    private final SharedPreferences preferences;
    private SharedPreferences securePreferences;
    private final Gson gson;

    public AppPreference(Context context) {
        Context appContext = (context != null && context.getApplicationContext() != null)
                ? context.getApplicationContext()
                : context;

        if (appContext != null) {
            preferences = appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        } else {
            preferences = null;
        }
        gson = new Gson();

        if (appContext != null) {
            try {
                MasterKey masterKey = new MasterKey.Builder(appContext)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                securePreferences = EncryptedSharedPreferences.create(
                        appContext,
                        SECURE_PREF_NAME,
                        masterKey,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );
            } catch (Exception e) {
                Log.w(TAG, "EncryptedSharedPreferences initialization failed, falling back to standard prefs", e);
                securePreferences = preferences;
            }
        }
    }

    public boolean isFirstTime() {
        return preferences != null ? preferences.getBoolean(KEY_FIRST_TIME, true) : true;
    }

    public void setKeyFirstTimeLaunch(boolean isFirstTime) {
        if (preferences != null) {
            preferences.edit().putBoolean(KEY_FIRST_TIME, isFirstTime).apply();
        }
    }

    public void saveAuthToken(String token) {
        if (securePreferences != null) {
            securePreferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
        }
        // Also keep sync in standard prefs as fallback if needed
        if (preferences != null) {
            preferences.edit().putString(KEY_AUTH_TOKEN, token).apply();
        }
    }

    public String getAuthToken() {
        if (securePreferences != null) {
            String token = securePreferences.getString(KEY_AUTH_TOKEN, null);
            if (token != null && !token.trim().isEmpty()) {
                return token;
            }
        }
        return preferences != null ? preferences.getString(KEY_AUTH_TOKEN, null) : null;
    }

    public boolean isLoggedIn() {
        String token = getAuthToken();
        return token != null && !token.trim().isEmpty() && !token.equals("null");
    }

    public void saveUser(User user) {
        if (user != null && preferences != null) {
            String json = gson.toJson(user);
            preferences.edit().putString(KEY_USER_DATA, json).apply();
        }
    }

    public User getUser() {
        if (preferences == null) return null;
        String json = preferences.getString(KEY_USER_DATA, null);
        if (json != null && !json.trim().isEmpty()) {
            try {
                return gson.fromJson(json, User.class);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public void setRememberMe(boolean remember, String email) {
        if (preferences != null) {
            preferences.edit()
                    .putBoolean(KEY_REMEMBER_ME, remember)
                    .putString(KEY_SAVED_EMAIL, remember ? email : "")
                    .apply();
        }
    }

    public boolean isRememberMe() {
        return preferences != null ? preferences.getBoolean(KEY_REMEMBER_ME, false) : false;
    }

    public String getSavedEmail() {
        return preferences != null ? preferences.getString(KEY_SAVED_EMAIL, "") : "";
    }

    public void setCustomBaseUrl(String url) {
        if (preferences != null) {
            preferences.edit().putString(KEY_BASE_URL, url).apply();
        }
    }

    public String getCustomBaseUrl() {
        return preferences != null ? preferences.getString(KEY_BASE_URL, null) : null;
    }

    public void setLanguage(String langCode) {
        if (preferences != null) {
            preferences.edit().putString(KEY_LANGUAGE, langCode).apply();
        }
    }

    public String getLanguage() {
        return preferences != null ? preferences.getString(KEY_LANGUAGE, "en") : "en";
    }

    public void setDarkMode(boolean enabled) {
        if (preferences != null) {
            preferences.edit().putBoolean(KEY_DARK_MODE, enabled).apply();
        }
    }

    public boolean isDarkMode() {
        return preferences != null ? preferences.getBoolean(KEY_DARK_MODE, false) : false;
    }

    public void saveSelectedBusiness(com.example.smarttourismapp.model.Business business) {
        if (preferences != null) {
            if (business != null) {
                preferences.edit().putString(KEY_SELECTED_BUSINESS, gson.toJson(business)).apply();
            } else {
                preferences.edit().remove(KEY_SELECTED_BUSINESS).apply();
            }
        }
    }

    public com.example.smarttourismapp.model.Business getSelectedBusiness() {
        if (preferences == null) return null;
        String json = preferences.getString(KEY_SELECTED_BUSINESS, null);
        if (json != null && !json.trim().isEmpty()) {
            try {
                return gson.fromJson(json, com.example.smarttourismapp.model.Business.class);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    public Long getSelectedBusinessId() {
        com.example.smarttourismapp.model.Business b = getSelectedBusiness();
        return b != null ? b.getId() : null;
    }

    public void clearSession() {
        if (securePreferences != null) {
            securePreferences.edit().remove(KEY_AUTH_TOKEN).apply();
        }
        if (preferences != null) {
            preferences.edit()
                    .remove(KEY_AUTH_TOKEN)
                    .remove(KEY_USER_DATA)
                    .remove(KEY_SELECTED_BUSINESS)
                    .apply();
        }
    }
}
