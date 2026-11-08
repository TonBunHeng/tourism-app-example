package com.example.smarttourismapp.data.remote;

import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.AuthResponse;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.GoogleLoginRequest;
import com.example.smarttourismapp.model.LoginRequest;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.model.Province;
import com.example.smarttourismapp.model.RegisterRequest;
import com.example.smarttourismapp.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // ==========================================
    // 1. Authentication (Matching web authService)
    // ==========================================
    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @POST("auth/google")
    Call<ApiResponse<AuthResponse>> googleLogin(@Body GoogleLoginRequest request);

    @GET("auth/me")
    Call<ApiResponse<User>> me();

    @PUT("auth/profile")
    Call<ApiResponse<User>> updateProfile(@Body User user);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();

    // ==========================================
    // 2. Destinations & Directory (travelService)
    // ==========================================
    @GET("places")
    Call<ApiResponse<List<Place>>> getPlaces(
            @Query("category_id") Long categoryId,
            @Query("province_id") Long provinceId,
            @Query("search") String search
    );

    @GET("places/{id}")
    Call<ApiResponse<Place>> getPlace(@Path("id") Long id);

    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    @GET("provinces")
    Call<ApiResponse<List<Province>>> getProvinces();

    // ==========================================
    // 3. Favorites / Wishlist
    // ==========================================
    @GET("favorites")
    Call<ApiResponse<List<Place>>> getFavorites();

    @POST("favorites/toggle/{placeId}")
    Call<ApiResponse<Void>> toggleFavorite(@Path("placeId") Long placeId);
}
