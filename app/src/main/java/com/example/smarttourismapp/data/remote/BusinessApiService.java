package com.example.smarttourismapp.data.remote;

import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Booking;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.BusinessBookingStats;
import com.example.smarttourismapp.model.BusinessHour;
import com.example.smarttourismapp.model.BusinessImage;
import com.example.smarttourismapp.model.BusinessPromotion;
import com.example.smarttourismapp.model.BusinessService;
import com.example.smarttourismapp.model.Review;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BusinessApiService {

    // ==========================================
    // 1. Business Profile & Businesses Management
    // ==========================================
    @GET("profile")
    Call<ApiResponse<Map<String, Object>>> getProfile();

    @GET("businesses")
    Call<ApiResponse<Map<String, Object>>> getBusinesses(
            @Query("verification_status") String verificationStatus,
            @Query("status") String status,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    @POST("businesses")
    Call<ApiResponse<Business>> createBusiness(@Body Map<String, Object> body);

    @GET("businesses/{id}")
    Call<ApiResponse<Business>> getBusiness(@Path("id") Long id);

    @PUT("businesses/{id}")
    Call<ApiResponse<Business>> updateBusiness(@Path("id") Long id, @Body Map<String, Object> body);

    @DELETE("businesses/{id}")
    Call<ApiResponse<Void>> deleteBusiness(@Path("id") Long id);

    // ==========================================
    // 2. Photos & Gallery
    // ==========================================
    @GET("businesses/{id}/images")
    Call<ApiResponse<List<BusinessImage>>> getImages(@Path("id") Long id);

    @POST("businesses/{id}/images")
    Call<ApiResponse<BusinessImage>> addImage(@Path("id") Long id, @Body Map<String, Object> body);

    @DELETE("businesses/{id}/images/{imageId}")
    Call<ApiResponse<Void>> deleteImage(@Path("id") Long id, @Path("imageId") Long imageId);

    // ==========================================
    // 3. Services / Offerings
    // ==========================================
    @GET("businesses/{id}/services")
    Call<ApiResponse<List<BusinessService>>> getServices(@Path("id") Long id);

    @POST("businesses/{id}/services")
    Call<ApiResponse<BusinessService>> createService(@Path("id") Long id, @Body Map<String, Object> body);

    @PUT("businesses/{id}/services/{serviceId}")
    Call<ApiResponse<BusinessService>> updateService(
            @Path("id") Long id,
            @Path("serviceId") Long serviceId,
            @Body Map<String, Object> body
    );

    @DELETE("businesses/{id}/services/{serviceId}")
    Call<ApiResponse<Void>> deleteService(@Path("id") Long id, @Path("serviceId") Long serviceId);

    // ==========================================
    // 4. Operating Hours
    // ==========================================
    @GET("businesses/{id}/hours")
    Call<ApiResponse<Map<String, Object>>> getHours(@Path("id") Long id);

    @PUT("businesses/{id}/hours")
    Call<ApiResponse<Void>> updateHours(@Path("id") Long id, @Body Map<String, Object> body);

    // ==========================================
    // 5. Promotions & Discounts
    // ==========================================
    @GET("businesses/{id}/promotions")
    Call<ApiResponse<List<BusinessPromotion>>> getPromotions(@Path("id") Long id);

    @POST("businesses/{id}/promotions")
    Call<ApiResponse<BusinessPromotion>> createPromotion(@Path("id") Long id, @Body Map<String, Object> body);

    @PUT("businesses/{id}/promotions/{promoId}")
    Call<ApiResponse<BusinessPromotion>> updatePromotion(
            @Path("id") Long id,
            @Path("promoId") Long promoId,
            @Body Map<String, Object> body
    );

    @DELETE("businesses/{id}/promotions/{promoId}")
    Call<ApiResponse<Void>> deletePromotion(@Path("id") Long id, @Path("promoId") Long promoId);

    // ==========================================
    // 6. Reviews & Owner Replies
    // ==========================================
    @GET("businesses/{id}/reviews")
    Call<ApiResponse<Map<String, Object>>> getReviews(@Path("id") Long id);

    @POST("businesses/{id}/reviews/{reviewId}/reply")
    Call<ApiResponse<Void>> replyReview(
            @Path("id") Long id,
            @Path("reviewId") Long reviewId,
            @Body Map<String, String> body
    );

    // ==========================================
    // 7. Statistics & KPIs
    // ==========================================
    @GET("businesses/{id}/statistics")
    Call<ApiResponse<Map<String, Object>>> getStatistics(@Path("id") Long id);

    // ==========================================
    // 8. Bookings Management
    // ==========================================
    @GET("bookings")
    Call<ApiResponse<Map<String, Object>>> getBookings(
            @Query("business_id") Long businessId,
            @Query("status") String status,
            @Query("payment_status") String paymentStatus,
            @Query("search") String search,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );

    @GET("bookings/statistics")
    Call<ApiResponse<BusinessBookingStats>> getBookingStatistics(@Query("business_id") Long businessId);

    @GET("bookings/{id}")
    Call<ApiResponse<Booking>> getBooking(@Path("id") Long id);

    @POST("bookings/{id}/confirm")
    Call<ApiResponse<Booking>> confirmBooking(@Path("id") Long id);

    @POST("bookings/{id}/reject")
    Call<ApiResponse<Booking>> rejectBooking(@Path("id") Long id, @Body Map<String, String> body);

    @POST("bookings/{id}/complete")
    Call<ApiResponse<Booking>> completeBooking(@Path("id") Long id, @Body Map<String, String> body);

    // ==========================================
    // 9. Financial & Sales Reports
    // ==========================================
    @GET("reports")
    Call<ApiResponse<Map<String, Object>>> getReports(
            @Query("timeframe") String timeframe,
            @Query("business_id") Long businessId
    );

    @GET("businesses/{id}/reports")
    Call<ApiResponse<Map<String, Object>>> getBusinessReports(
            @Path("id") Long id,
            @Query("timeframe") String timeframe
    );
}

