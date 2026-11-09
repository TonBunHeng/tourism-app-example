package com.example.smarttourismapp.data.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiErrorParser;
import com.example.smarttourismapp.data.remote.BusinessApiService;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Booking;
import com.example.smarttourismapp.model.Business;
import com.example.smarttourismapp.model.BusinessBookingStats;
import com.example.smarttourismapp.model.BusinessHour;
import com.example.smarttourismapp.model.BusinessImage;
import com.example.smarttourismapp.model.BusinessPromotion;
import com.example.smarttourismapp.model.BusinessService;
import com.example.smarttourismapp.model.Review;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusinessRepository {

    public interface RepositoryCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }

    private final BusinessApiService apiService;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Gson gson = new Gson();

    public BusinessRepository(Context context) {
        this.apiService = ApiClient.getBusinessApiService(context);
    }

    // ==========================================
    // 1. Profile & Businesses Management
    // ==========================================
    public void getProfile(RepositoryCallback<Map<String, Object>> callback) {
        apiService.getProfile().enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void getBusinesses(RepositoryCallback<List<Business>> callback) {
        apiService.getBusinesses(null, null, 1, 50).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Business> list = new ArrayList<>();
                    Map<String, Object> data = response.body().getData();
                    if (data != null && data.containsKey("businesses")) {
                        Object obj = data.get("businesses");
                        String json = gson.toJson(obj);
                        Type type = new TypeToken<List<Business>>() {}.getType();
                        List<Business> parsed = gson.fromJson(json, type);
                        if (parsed != null) list.addAll(parsed);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void createBusiness(Map<String, Object> body, RepositoryCallback<Business> callback) {
        apiService.createBusiness(body).enqueue(new Callback<ApiResponse<Business>>() {
            @Override
            public void onResponse(Call<ApiResponse<Business>> call, Response<ApiResponse<Business>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Business>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void getBusiness(Long id, RepositoryCallback<Business> callback) {
        apiService.getBusiness(id).enqueue(new Callback<ApiResponse<Business>>() {
            @Override
            public void onResponse(Call<ApiResponse<Business>> call, Response<ApiResponse<Business>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Business>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void updateBusiness(Long id, Map<String, Object> body, RepositoryCallback<Business> callback) {
        apiService.updateBusiness(id, body).enqueue(new Callback<ApiResponse<Business>>() {
            @Override
            public void onResponse(Call<ApiResponse<Business>> call, Response<ApiResponse<Business>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Business>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void deleteBusiness(Long id, RepositoryCallback<Void> callback) {
        apiService.deleteBusiness(id).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(null));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 2. Services Management
    // ==========================================
    public void getServices(Long businessId, RepositoryCallback<List<BusinessService>> callback) {
        apiService.getServices(businessId).enqueue(new Callback<ApiResponse<List<BusinessService>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BusinessService>>> call, Response<ApiResponse<List<BusinessService>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BusinessService> list = response.body().getData() != null ? response.body().getData() : new ArrayList<>();
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BusinessService>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void createService(Long businessId, Map<String, Object> body, RepositoryCallback<BusinessService> callback) {
        apiService.createService(businessId, body).enqueue(new Callback<ApiResponse<BusinessService>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusinessService>> call, Response<ApiResponse<BusinessService>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusinessService>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void updateService(Long businessId, Long serviceId, Map<String, Object> body, RepositoryCallback<BusinessService> callback) {
        apiService.updateService(businessId, serviceId, body).enqueue(new Callback<ApiResponse<BusinessService>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusinessService>> call, Response<ApiResponse<BusinessService>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusinessService>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void deleteService(Long businessId, Long serviceId, RepositoryCallback<Void> callback) {
        apiService.deleteService(businessId, serviceId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(null));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 3. Operating Hours
    // ==========================================
    public void getHours(Long businessId, RepositoryCallback<List<BusinessHour>> callback) {
        apiService.getHours(businessId).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BusinessHour> list = new ArrayList<>();
                    Map<String, Object> data = response.body().getData();
                    if (data != null && data.containsKey("hours")) {
                        Object obj = data.get("hours");
                        String json = gson.toJson(obj);
                        Type type = new TypeToken<List<BusinessHour>>() {}.getType();
                        List<BusinessHour> parsed = gson.fromJson(json, type);
                        if (parsed != null) list.addAll(parsed);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void updateHours(Long businessId, List<BusinessHour> hours, RepositoryCallback<Void> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("hours", hours);
        apiService.updateHours(businessId, body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(null));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 4. Promotions Management
    // ==========================================
    public void getPromotions(Long businessId, RepositoryCallback<List<BusinessPromotion>> callback) {
        apiService.getPromotions(businessId).enqueue(new Callback<ApiResponse<List<BusinessPromotion>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BusinessPromotion>>> call, Response<ApiResponse<List<BusinessPromotion>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BusinessPromotion> list = response.body().getData() != null ? response.body().getData() : new ArrayList<>();
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BusinessPromotion>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void createPromotion(Long businessId, Map<String, Object> body, RepositoryCallback<BusinessPromotion> callback) {
        apiService.createPromotion(businessId, body).enqueue(new Callback<ApiResponse<BusinessPromotion>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusinessPromotion>> call, Response<ApiResponse<BusinessPromotion>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusinessPromotion>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void updatePromotion(Long businessId, Long promoId, Map<String, Object> body, RepositoryCallback<BusinessPromotion> callback) {
        apiService.updatePromotion(businessId, promoId, body).enqueue(new Callback<ApiResponse<BusinessPromotion>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusinessPromotion>> call, Response<ApiResponse<BusinessPromotion>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusinessPromotion>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void deletePromotion(Long businessId, Long promoId, RepositoryCallback<Void> callback) {
        apiService.deletePromotion(businessId, promoId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(null));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 5. Photos & Images
    // ==========================================
    public void getImages(Long businessId, RepositoryCallback<List<BusinessImage>> callback) {
        apiService.getImages(businessId).enqueue(new Callback<ApiResponse<List<BusinessImage>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<BusinessImage>>> call, Response<ApiResponse<List<BusinessImage>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<BusinessImage> list = response.body().getData() != null ? response.body().getData() : new ArrayList<>();
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<BusinessImage>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void addImage(Long businessId, String imageUrl, String caption, boolean isCover, RepositoryCallback<BusinessImage> callback) {
        Map<String, Object> body = new HashMap<>();
        body.put("image_url", imageUrl);
        body.put("caption", caption);
        body.put("is_cover", isCover);

        apiService.addImage(businessId, body).enqueue(new Callback<ApiResponse<BusinessImage>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusinessImage>> call, Response<ApiResponse<BusinessImage>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusinessImage>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void deleteImage(Long businessId, Long imageId, RepositoryCallback<Void> callback) {
        apiService.deleteImage(businessId, imageId).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(null));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 6. Reviews & Owner Replies
    // ==========================================
    public void getReviews(Long businessId, RepositoryCallback<List<Review>> callback) {
        apiService.getReviews(businessId).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Review> list = new ArrayList<>();
                    Map<String, Object> data = response.body().getData();
                    if (data != null && data.containsKey("reviews")) {
                        Object obj = data.get("reviews");
                        String json = gson.toJson(obj);
                        Type type = new TypeToken<List<Review>>() {}.getType();
                        List<Review> parsed = gson.fromJson(json, type);
                        if (parsed != null) list.addAll(parsed);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void replyReview(Long businessId, Long reviewId, String reply, RepositoryCallback<Void> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("reply", reply);

        apiService.replyReview(businessId, reviewId, body).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful()) {
                    mainHandler.post(() -> callback.onSuccess(null));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 7. Bookings Management
    // ==========================================
    public void getBookings(Long businessId, String status, String search, Integer page, RepositoryCallback<List<Booking>> callback) {
        apiService.getBookings(businessId, status, null, search, page, 20).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<Booking> list = new ArrayList<>();
                    Map<String, Object> data = response.body().getData();
                    if (data != null && data.containsKey("bookings")) {
                        Object obj = data.get("bookings");
                        String json = gson.toJson(obj);
                        Type type = new TypeToken<List<Booking>>() {}.getType();
                        List<Booking> parsed = gson.fromJson(json, type);
                        if (parsed != null) list.addAll(parsed);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void getBookingStatistics(Long businessId, RepositoryCallback<BusinessBookingStats> callback) {
        apiService.getBookingStatistics(businessId).enqueue(new Callback<ApiResponse<BusinessBookingStats>>() {
            @Override
            public void onResponse(Call<ApiResponse<BusinessBookingStats>> call, Response<ApiResponse<BusinessBookingStats>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<BusinessBookingStats>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void confirmBooking(Long bookingId, RepositoryCallback<Booking> callback) {
        apiService.confirmBooking(bookingId).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void rejectBooking(Long bookingId, String reason, RepositoryCallback<Booking> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("rejection_reason", reason != null ? reason : "Service capacity full");

        apiService.rejectBooking(bookingId, body).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void completeBooking(Long bookingId, RepositoryCallback<Booking> callback) {
        Map<String, String> body = new HashMap<>();
        body.put("payment_status", "paid");

        apiService.completeBooking(bookingId, body).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    // ==========================================
    // 8. Reports & Analytics
    // ==========================================
    public void getReports(String timeframe, Long businessId, RepositoryCallback<Map<String, Object>> callback) {
        apiService.getReports(timeframe, businessId).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call, Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    mainHandler.post(() -> callback.onSuccess(response.body().getData()));
                } else {
                    String msg = ApiErrorParser.parseMessage(response);
                    mainHandler.post(() -> callback.onError(msg));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                mainHandler.post(() -> callback.onError(ApiErrorParser.parseThrowable(t)));
            }
        });
    }
}
