package com.example.smarttourismapp.ui.booking;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.smarttourismapp.data.remote.ApiClient;
import com.example.smarttourismapp.data.remote.ApiErrorParser;
import com.example.smarttourismapp.data.remote.ApiService;
import com.example.smarttourismapp.data.remote.Resource;
import com.example.smarttourismapp.model.ApiResponse;
import com.example.smarttourismapp.model.Booking;
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

public class BookingsViewModel extends AndroidViewModel {

    private final ApiService apiService;
    private final Gson gson = new Gson();

    private final MutableLiveData<Resource<List<Booking>>> bookingsState = new MutableLiveData<>();
    private final MutableLiveData<Resource<Booking>> cancelState = new MutableLiveData<>();

    private String currentStatusFilter = null;

    public BookingsViewModel(@NonNull Application application) {
        super(application);
        this.apiService = ApiClient.getApiService(application);
    }

    public LiveData<Resource<List<Booking>>> getBookingsState() {
        return bookingsState;
    }

    public LiveData<Resource<Booking>> getCancelState() {
        return cancelState;
    }

    public void setStatusFilter(String status) {
        this.currentStatusFilter = "all".equalsIgnoreCase(status) ? null : status;
        loadBookings();
    }

    public void loadBookings() {
        bookingsState.setValue(Resource.loading());

        apiService.getBookings(currentStatusFilter, 1, 50).enqueue(new Callback<ApiResponse<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<ApiResponse<Map<String, Object>>> call,
                    Response<ApiResponse<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Map<String, Object> dataMap = response.body().getData();
                    Object rawList = dataMap.get("bookings");
                    if (rawList != null) {
                        String json = gson.toJson(rawList);
                        Type listType = new TypeToken<List<Booking>>() {
                        }.getType();
                        List<Booking> list = gson.fromJson(json, listType);
                        bookingsState.setValue(Resource.success(list != null ? list : new ArrayList<>()));
                        return;
                    }
                }
                bookingsState.setValue(Resource.error(ApiErrorParser.parseMessage(response)));
            }

            @Override
            public void onFailure(Call<ApiResponse<Map<String, Object>>> call, Throwable t) {
                bookingsState.setValue(Resource.error(ApiErrorParser.parseThrowable(t)));
            }
        });
    }

    public void cancelBooking(Long bookingId, String reason) {
        cancelState.setValue(Resource.loading());
        Map<String, String> body = new HashMap<>();
        body.put("cancellation_reason", reason != null ? reason : "Cancelled by tourist");

        apiService.cancelBooking(bookingId, body).enqueue(new Callback<ApiResponse<Booking>>() {
            @Override
            public void onResponse(Call<ApiResponse<Booking>> call, Response<ApiResponse<Booking>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    cancelState.setValue(Resource.success(response.body().getData()));
                    loadBookings();
                } else {
                    cancelState.setValue(Resource.error(ApiErrorParser.parseMessage(response)));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Booking>> call, Throwable t) {
                cancelState.setValue(Resource.error(ApiErrorParser.parseThrowable(t)));
            }
        });
    }
}
