package com.example.smarttourismapp.data.remote;

import android.content.Context;
import android.content.Intent;

import com.example.smarttourismapp.BuildConfig;
import com.example.smarttourismapp.ui.auth.SignInActivity;
import com.example.smarttourismapp.utils.AppPreference;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Singleton Retrofit & OkHttpClient factory.
 * Manages BASE_URL from BuildConfig, Bearer token authorization,
 * 401 unauthenticated redirect, timeouts, and JSON conversion.
 * Supports both Traveler API (/api/travel/) and Business Owner API (/api/business/).
 */
public class ApiClient {

    public static final String DEFAULT_BASE_URL = BuildConfig.BASE_URL;

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    private static Retrofit businessRetrofit = null;
    private static BusinessApiService businessApiService = null;

    public static synchronized ApiService getApiService(Context context) {
        if (apiService == null) {
            Retrofit retrofitInstance = getRetrofit(context);
            apiService = retrofitInstance.create(ApiService.class);
        }
        return apiService;
    }

    public static synchronized BusinessApiService getBusinessApiService(Context context) {
        if (businessApiService == null) {
            Retrofit retrofitInstance = getBusinessRetrofit(context);
            businessApiService = retrofitInstance.create(BusinessApiService.class);
        }
        return businessApiService;
    }

    public static synchronized void resetClient() {
        retrofit = null;
        apiService = null;
        businessRetrofit = null;
        businessApiService = null;
    }

    public static String getBaseUrl(Context context) {
        if (context != null) {
            AppPreference prefs = new AppPreference(context);
            String custom = prefs.getCustomBaseUrl();
            if (custom != null && !custom.trim().isEmpty()) {
                if (!custom.endsWith("/")) {
                    custom += "/";
                }
                return custom;
            }
        }
        return DEFAULT_BASE_URL;
    }

    public static String getBusinessBaseUrl(Context context) {
        String base = getBaseUrl(context);
        if (base.endsWith("/travel/")) {
            return base.substring(0, base.length() - "/travel/".length()) + "/business/";
        } else if (base.endsWith("/travel")) {
            return base.substring(0, base.length() - "/travel".length()) + "/business/";
        } else if (base.endsWith("/")) {
            return base + "business/";
        }
        return base + "/business/";
    }

    private static OkHttpClient buildOkHttpClient(Context context) {
        final Context appContext = (context != null && context.getApplicationContext() != null)
                ? context.getApplicationContext()
                : context;
        final AppPreference prefs = new AppPreference(appContext);

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE);

        Interceptor authInterceptor = chain -> {
            Request original = chain.request();
            Request.Builder builder = original.newBuilder()
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json");

            String token = prefs.getAuthToken();
            if (token != null && !token.trim().isEmpty() && !token.equals("null")) {
                builder.header("Authorization", "Bearer " + token);
            }

            Response response = chain.proceed(builder.build());

            // If unauthorized or session expired (401), clear local session and navigate to SignInActivity
            if (response.code() == 401) {
                prefs.clearSession();
                Intent intent = new Intent(appContext, SignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                appContext.startActivity(intent);
            }

            return response;
        };

        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    private static synchronized Retrofit getRetrofit(Context context) {
        if (retrofit == null) {
            final Context appContext = (context != null && context.getApplicationContext() != null)
                    ? context.getApplicationContext()
                    : context;

            retrofit = new Retrofit.Builder()
                    .baseUrl(getBaseUrl(appContext))
                    .client(buildOkHttpClient(appContext))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static synchronized Retrofit getBusinessRetrofit(Context context) {
        if (businessRetrofit == null) {
            final Context appContext = (context != null && context.getApplicationContext() != null)
                    ? context.getApplicationContext()
                    : context;

            businessRetrofit = new Retrofit.Builder()
                    .baseUrl(getBusinessBaseUrl(appContext))
                    .client(buildOkHttpClient(appContext))
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return businessRetrofit;
    }
}
