package com.example.smarttourismapp.data.remote;

import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.AuthResponse;
import com.example.smarttourismapp.model.Booking;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.Category;
import com.example.smarttourismapp.model.GoogleLoginRequest;
import com.example.smarttourismapp.model.LoginRequest;
import com.example.smarttourismapp.model.Place;
import com.example.smarttourismapp.model.Province;
import com.example.smarttourismapp.model.RegisterRequest;
import com.example.smarttourismapp.model.Review;
import com.example.smarttourismapp.model.ReviewRequest;
import com.example.smarttourismapp.model.User;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
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
    Call<ApiResponse<User>> updateProfile(@Body Map<String, Object> updates);

    @PUT("auth/password")
    Call<ApiResponse<Void>> changePassword(@Body Map<String, String> body);

    @Multipart
    @POST("auth/avatar")
    Call<ApiResponse<User>> uploadAvatar(@Part MultipartBody.Part avatar);

    @DELETE("auth/avatar")
    Call<ApiResponse<Void>> deleteAvatar();

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout();

    @POST("deletion-requests")
    Call<ApiResponse<Void>> requestAccountDeletion(@Body Map<String, String> body);

    // ==========================================
    // 2. Destinations & Directory (travelService)
    // ==========================================
    @GET("places")
    Call<ApiResponse<List<Place>>> getPlaces(
            @Query("category_id") Long categoryId,
            @Query("province_id") Long provinceId,
            @Query("search") String search,
            @Query("sort_by") String sortBy,
            @Query("min_rating") Float minRating,
            @Query("featured") Boolean featured,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    @GET("places/{id}")
    Call<ApiResponse<Place>> getPlace(@Path("id") Long id);

    @GET("categories")
    Call<ApiResponse<List<Category>>> getCategories();

    @GET("categories/{id}")
    Call<ApiResponse<Category>> getCategory(@Path("id") Long id);

    @GET("provinces")
    Call<ApiResponse<List<Province>>> getProvinces();

    @GET("provinces/{id}")
    Call<ApiResponse<Province>> getProvince(@Path("id") Long id);

    // ==========================================
    // 3. Businesses & Directory
    // ==========================================
    @GET("businesses")
    Call<ApiResponse<Map<String, Object>>> getBusinesses(
            @Query("category_id") Long categoryId,
            @Query("province_id") Long provinceId,
            @Query("search") String search,
            @Query("sort") String sort,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    @GET("businesses/{id}")
    Call<ApiResponse<Map<String, Object>>> getBusiness(@Path("id") Long id);

    @GET("businesses/{id}/services")
    Call<ApiResponse<List<Map<String, Object>>>> getBusinessServices(@Path("id") Long id);

    @GET("businesses/{id}/hours")
    Call<ApiResponse<List<Map<String, Object>>>> getBusinessHours(@Path("id") Long id);

    @GET("businesses/{id}/reviews")
    Call<ApiResponse<List<Review>>> getBusinessReviews(@Path("id") Long id);

    @POST("businesses/{id}/reviews")
    Call<ApiResponse<Review>> postBusinessReview(@Path("id") Long id, @Body ReviewRequest request);

    @POST("businesses/{id}/bookings")
    Call<ApiResponse<Booking>> createBusinessBooking(@Path("id") Long businessId, @Body Map<String, Object> body);

    // ==========================================
    // 4. Bookings
    // ==========================================
    @GET("bookings")
    Call<ApiResponse<Map<String, Object>>> getBookings(
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    @GET("bookings/{id}")
    Call<ApiResponse<Booking>> getBooking(@Path("id") Long id);

    @POST("bookings")
    Call<ApiResponse<Booking>> createBooking(@Body Map<String, Object> body);

    @POST("bookings/{id}/cancel")
    Call<ApiResponse<Booking>> cancelBooking(@Path("id") Long id, @Body Map<String, String> body);

    // ==========================================
    // 5. Reviews
    // ==========================================
    @GET("reviews")
    Call<ApiResponse<List<Review>>> getReviews(
            @Query("place_id") Long placeId,
            @Query("page") Integer page
    );

    @POST("reviews")
    Call<ApiResponse<Review>> createReview(@Body ReviewRequest request);

    @PUT("reviews/{id}")
    Call<ApiResponse<Review>> updateReview(@Path("id") Long id, @Body ReviewRequest request);

    @DELETE("reviews/{id}")
    Call<ApiResponse<Void>> deleteReview(@Path("id") Long id);

    // ==========================================
    // 6. Favorites / Wishlist
    // ==========================================
    @GET("favorites")
    Call<ApiResponse<List<com.example.smarttourismapp.model.FavoriteItem>>> getFavorites();

    @POST("favorites")
    Call<ApiResponse<Void>> addFavorite(@Body Map<String, Object> body);

    @POST("favorites/toggle/{placeId}")
    Call<ApiResponse<Void>> toggleFavorite(@Path("placeId") Long placeId);

    @DELETE("favorites/{placeId}")
    Call<ApiResponse<Void>> removeFavorite(@Path("placeId") Long placeId);

    @PATCH("favorites/{id}/toggle-visited")
    Call<ApiResponse<Void>> toggleVisited(@Path("id") Long id);

    // ==========================================
    // 7. Notifications
    // ==========================================
    @GET("notifications")
    Call<ApiResponse<List<Map<String, Object>>>> getNotifications(
            @Query("search") String search,
            @Query("category") String category,
            @Query("unread_only") Boolean unreadOnly
    );

    @GET("notifications/unread-count")
    Call<ApiResponse<Map<String, Object>>> getUnreadNotificationsCount();

    @PATCH("notifications/{id}/read")
    Call<ApiResponse<Void>> markNotificationRead(@Path("id") Long id);

    @PATCH("notifications/read-all")
    Call<ApiResponse<Void>> markAllNotificationsRead();

    @DELETE("notifications/{id}")
    Call<ApiResponse<Void>> deleteNotification(@Path("id") Long id);

    // ==========================================
    // 8. Settings & Support
    // ==========================================
    @GET("settings")
    Call<ApiResponse<Map<String, Object>>> getSettings();

    @POST("contact")
    Call<ApiResponse<Void>> sendContact(@Body Map<String, String> body);

    // ==========================================
    // 9. AI Assistant (AngkorVers)
    // ==========================================
    @POST("ai/chat")
    Call<ApiResponse<Map<String, Object>>> chatWithAi(@Body Map<String, Object> body);

    @GET("ai/suggestions")
    Call<ApiResponse<List<String>>> getAiSuggestions();
}
